import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet implementation class APIServlet
 */
@WebServlet("/APIServlet")
public class APIServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private SQLUtils sqlUtils;

    /**
     * @throws SQLException
     * @see HttpServlet#HttpServlet()
     */
    public APIServlet() throws SQLException
    {
        super();
        sqlUtils = new SQLUtils("C://nearchat//db.sqlite");
        if (!sqlUtils.tableExists(SQLUtils.USER_TABLE_NAME))
            sqlUtils.initUserTables();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // TODO Auto-generated method stub
        JSONObject headNode = new JSONObject();

        String mode = request.getParameter("mode");
        String accessToken = request.getParameter("token");

        if (mode == null || mode.length() == 0)
        {
            putStatus(headNode, false, ErrorReason.ERR_MODE_EMPTY);
        }
        else
        {
            try
            {
                switch (mode)
                {
                    case "create_account":
                        String username = request.getParameter("username");
                        String email = request.getParameter("email");
                        String password = request.getParameter("password");

                        if (username == null || email == null || password == null || username.trim().length() == 0
                            || email.trim().length() == 0 || password.trim().length() == 0)
                        {
                            putStatus(headNode, false, ErrorReason.ERR_PARAMETERS_MISSING_OR_INVALID);
                            break;
                        }

                        boolean usernameTaken = sqlUtils.usernameExists(username);
                        boolean emailTaken = sqlUtils.emailTaken(email);

                        if (usernameTaken || emailTaken)
                        {
                            putStatus(headNode, false,
                                usernameTaken && emailTaken ? ErrorReason.ERR_ACCOUNT_USERNAME_AND_EMAIL_EXISTS
                                    : usernameTaken ? ErrorReason.ERR_ACCOUNT_USERNAME_EXISTS
                                        : ErrorReason.ERR_ACCOUNT_EMAIL_EXISTS);
                        }
                        else
                        {
                            sqlUtils.addUser(email, username, password);
                            sqlUtils.updateAccountStringInfo(username, "visible", "1");
                            headNode.put("token", sqlUtils.generateAPITokenForAccount(username));
                            putStatus(headNode, true);
                        }

                        break;

                    case "login":
                        username = request.getParameter("username");
                        password = request.getParameter("password");

                        if (sqlUtils.doLogin(username, password))
                        {
                            String token = sqlUtils.generateAPITokenForAccount(username);
                            headNode.put("token", token);
                            putStatus(headNode, true);
                        }
                        else
                        {
                            putStatus(headNode, false, ErrorReason.ERR_LOGIN_UNSUCCESSFUL);
                        }

                        break;

                    case "logout":
                        if (accessToken == null)
                        {
                            putStatus(headNode, false, ErrorReason.ERR_ACCESS_TOKEN_EMPTY);
                            break;
                        }

                        sqlUtils.deregisterAPIToken(accessToken);
                        putStatus(headNode, true);
                        break;

                    case "get_cur_userinfo":
                        if (accessToken == null)
                        {
                            putStatus(headNode, false, ErrorReason.ERR_ACCESS_TOKEN_EMPTY);
                            break;
                        }

                        String resolvedUsername = sqlUtils.getUsernameFromAPIToken(accessToken);

                        if (resolvedUsername == null)
                        {
                            putStatus(headNode, false, ErrorReason.ERR_ACCESS_TOKEN_INVALID);
                        }
                        else
                        {
                            JSONObject obj = sqlUtils.getNearChatUserByUsername(resolvedUsername).toJSONObject(true);
                            headNode.put("result", obj);
                            putStatus(headNode, true);
                        }
                        break;

                    case "get_userinfo":
                        if (accessToken == null)
                        {
                            putStatus(headNode, false, ErrorReason.ERR_ACCESS_TOKEN_EMPTY);
                            break;
                        }

                        resolvedUsername = sqlUtils.getUsernameFromAPIToken(accessToken);

                        if (resolvedUsername == null)
                        {
                            putStatus(headNode, false, ErrorReason.ERR_ACCESS_TOKEN_INVALID);
                        }
                        else
                        {
                            String tgtUsername = request.getParameter("username");
                            String tgtIdStr = request.getParameter("id");

                            int chkSum = (tgtUsername == null ? 1 : 0) + (tgtIdStr == null ? 1 : 0);

                            if (chkSum == 2)
                            {
                                // both possible parameters are missing
                                headNode.put("note",
                                    "Please provide either an \"id\" parameter or a \"username\" parameter.");
                                putStatus(headNode, false, ErrorReason.ERR_PARAMETERS_MISSING_OR_INVALID);
                            }
                            else if (chkSum == 0)
                            {
                                // both possible parameters are provided
                                headNode.put("note",
                                    "Please provide ONLY an \"id\" parameter or a \"username\" parameter.");
                                putStatus(headNode, false, ErrorReason.ERR_PARAMETERS_MISSING_OR_INVALID);
                            }
                            else
                            {
                                NearChatUser tgtUser = null;

                                if (tgtIdStr != null)
                                {
                                    // search by ID
                                    long tgtId = Long.parseLong(tgtIdStr);
                                    tgtUser = sqlUtils.getNearChatUserByID(tgtId);
                                }
                                else
                                {
                                    // search by username
                                    tgtUser = sqlUtils.getNearChatUserByUsername(tgtUsername);
                                }

                                if (tgtUser == null)
                                {
                                    putStatus(headNode, false, ErrorReason.ERR_ACCOUNT_NONEXISTANT);
                                    break;
                                }

                                if (!tgtUser.visible && !resolvedUsername.equals(tgtUser.username))
                                {
                                    // current account unauthorized to view
                                    // resolved user
                                    putStatus(headNode, false, ErrorReason.ERR_ACCESS_PERMISSION_DENIED);
                                    break;
                                }

                                JSONObject obj = tgtUser.toJSONObject(true);
                                headNode.put("result", obj);
                                putStatus(headNode, true);
                            }
                        }
                        break;

                    case "update_info":
                    case "update_geo":
                        if (accessToken == null)
                        {
                            putStatus(headNode, false, ErrorReason.ERR_ACCESS_TOKEN_EMPTY);
                            break;
                        }

                        resolvedUsername = sqlUtils.getUsernameFromAPIToken(accessToken);

                        if (resolvedUsername == null)
                        {
                            putStatus(headNode, false, ErrorReason.ERR_ACCESS_TOKEN_INVALID);
                        }
                        else
                        {
                            Map<String, String[]> paramMap = request.getParameterMap();
                            for (String paramName : paramMap.keySet())
                            {
                                if (paramName.equals("username"))
                                {
                                    headNode.put("note", "username cannot be changed!");
                                    continue;
                                }

                                if (paramName.equals("token") || paramName.equals("rowid"))
                                    continue;

                                if (!sqlUtils.columnExists(paramName))
                                    continue;

                                String paramInfo = paramMap.get(paramName)[0];

                                if (paramName.equals("visible"))
                                {
                                    paramInfo = paramInfo.toLowerCase().equals("true") || paramInfo.equals("1") ? "1"
                                        : "0";
                                }
                                else if (paramName.equals("age"))
                                {
                                    // check validity of age integer string
                                    Integer newAge = parseIntAndReturnNullIfNotInt(paramInfo);
                                    if (newAge == null)
                                    {
                                        continue;
                                    }

                                    // do not allow underage users!
                                    if (newAge < 18)
                                    {
                                        putStatus(headNode, false, ErrorReason.ERR_USER_UNDERAGE);
                                        response.getWriter().append(headNode.toString());
                                        return;
                                    }
                                }
                                else if (paramName.equals("lat") || paramName.equals("lon"))
                                {
                                    Double newDouble = parseDoubleAndReturnNullIfNotDouble(paramInfo);
                                    // check validity of lat/lon double string
                                    if (newDouble == null)
                                    {
                                        continue;
                                    }
                                }

                                sqlUtils.updateAccountStringInfo(resolvedUsername, paramName, paramInfo);
                            }
                            putStatus(headNode, true);
                        }
                        break;

                    case "search_nearby":
                        if (accessToken == null)
                        {
                            putStatus(headNode, false, ErrorReason.ERR_ACCESS_TOKEN_EMPTY);
                            break;
                        }

                        resolvedUsername = sqlUtils.getUsernameFromAPIToken(accessToken);
                        NearChatUser loggedInUser = sqlUtils.getNearChatUserByUsername(resolvedUsername);

                        if (resolvedUsername == null)
                        {
                            putStatus(headNode, false, ErrorReason.ERR_ACCESS_TOKEN_INVALID);
                        }
                        else
                        {
                            String radiusStr = request.getParameter("radius");
                            String latitudeStr = request.getParameter("lat");
                            String longitudeStr = request.getParameter("lon");

                            if (radiusStr == null || latitudeStr == null || longitudeStr == null)
                            {
                                putStatus(headNode, false, ErrorReason.ERR_PARAMETERS_MISSING_OR_INVALID);
                            }
                            else
                            {
                                double radius = Double.parseDouble(radiusStr);
                                double userLat = Double.parseDouble(latitudeStr);
                                double userLon = Double.parseDouble(longitudeStr);

                                List<NearChatUser> allUsers = sqlUtils.getAllNearChatUsers();
                                List<UserDistancePair> usersByDistance = new ArrayList<>();

                                for (NearChatUser cur : allUsers)
                                {
                                    if (!cur.visible || loggedInUser.id == cur.id)
                                        continue;
                                    usersByDistance
                                        .add(new UserDistancePair(cur, cur.distanceFromCoords(userLat, userLon)));
                                }

                                Collections.sort(usersByDistance);

                                JSONArray toSend = new JSONArray();

                                for (UserDistancePair curPair : usersByDistance)
                                {
                                    if (curPair.distance > radius)
                                        break;
                                    toSend.put(curPair.user.toJSONObject());
                                }

                                headNode.put("results", toSend);
                                putStatus(headNode, true);
                            }
                        }
                        break;

                    default:
                        putStatus(headNode, false, ErrorReason.ERR_MODE_NOT_RECOGNIZED);
                        break;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                headNode.put("exception", e.getMessage());
                putStatus(headNode, false, ErrorReason.ERR_GENERIC);
            }
        }

        response.getWriter().append(headNode.toString());
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

    public void putStatus(JSONObject head, boolean successful)
    {
        putStatus(head, successful, null);
    }

    public void putStatus(JSONObject head, boolean successful, ErrorReason reason)
    {
        if (successful)
        {
            head.put("status", "success");
        }
        else
        {
            head.put("status", "failed");
            head.put("reason", reason);
        }
    }

    private Integer parseIntAndReturnNullIfNotInt(String str)
    {
        try
        {
            return Integer.parseInt(str);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private Double parseDoubleAndReturnNullIfNotDouble(String str)
    {
        try
        {
            return Double.parseDouble(str);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static class UserDistancePair implements Comparable<UserDistancePair>
    {
        public NearChatUser user;
        public double distance;

        public UserDistancePair(NearChatUser user, double distance)
        {
            this.user = user;
            this.distance = distance;
        }

        @Override
        public int compareTo(APIServlet.UserDistancePair arg0)
        {
            // TODO Auto-generated method stub
            return Double.compare(distance, arg0.distance);
        }

    }

}
