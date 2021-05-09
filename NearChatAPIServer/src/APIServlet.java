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
        if (!sqlUtils.tableExists("users"))
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

        if (mode == null)
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
                        boolean usernameTaken = sqlUtils.usernameTaken(username);
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
                        }

                        sqlUtils.deregisterAPIToken(accessToken);
                        putStatus(headNode, true);
                        break;

                    case "get_cur_userinfo":
                        if (accessToken == null)
                        {
                            putStatus(headNode, false, ErrorReason.ERR_ACCESS_TOKEN_EMPTY);
                        }

                        String resolvedUsername = sqlUtils.getUsernameFromAPIToken(accessToken);

                        if (resolvedUsername == null)
                        {
                            putStatus(headNode, false, ErrorReason.ERR_ACCESS_TOKEN_INVALID);
                        }
                        else
                        {
                            JSONObject obj = sqlUtils.getNearChatUser(resolvedUsername).toJSONObject();
                            // System.out.println(obj);
                            JSONArray arr = new JSONArray();
                            arr.put(obj);
                            headNode.put("results", arr);
                        }

                    case "update_info":
                    case "update_geo":
                        if (accessToken == null)
                        {
                            putStatus(headNode, false, ErrorReason.ERR_ACCESS_TOKEN_EMPTY);
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

                                if (paramName.equals("token"))
                                    continue;

                                if (!sqlUtils.columnExists(paramName))
                                    continue;

                                sqlUtils.updateAccountStringInfo(resolvedUsername, paramName,
                                    paramMap.get(paramName)[0]);
                                putStatus(headNode, true);
                            }
                        }
                        break;

                    case "search_nearby":
                        if (accessToken == null)
                        {
                            putStatus(headNode, false, ErrorReason.ERR_ACCESS_TOKEN_EMPTY);
                        }

                        resolvedUsername = sqlUtils.getUsernameFromAPIToken(accessToken);

                        if (resolvedUsername == null)
                        {
                            putStatus(headNode, false, ErrorReason.ERR_ACCESS_TOKEN_INVALID);
                        }
                        else
                        {
                            NearChatUser user = sqlUtils.getNearChatUser(resolvedUsername);
                            double userLat = user.lat;
                            double userLon = user.lon;

                            List<NearChatUser> allUsers = sqlUtils.getAllNearChatUsers();
                            List<UserDistancePair> usersByDistance = new ArrayList<>();
                            
                            for (NearChatUser cur : allUsers)
                            {
                                usersByDistance.add(new UserDistancePair(cur, cur.distanceFromCoords(userLat, userLon)));
                            }
                            
                            Collections.sort(usersByDistance);
                            
                            JSONArray toSend = new JSONArray();
                            double radius = Double.parseDouble(request.getParameter("radius"));

                            for (UserDistancePair curPair : usersByDistance)
                            {
                                if (curPair.distance > radius)
                                    break;
                                toSend.put(curPair.user.toJSONObject());
                            }
                            
                            headNode.put("results", toSend);
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

    @SuppressWarnings("unused")
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
