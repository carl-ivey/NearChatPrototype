import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        sqlUtils = new SQLUtils("db.sqlite");
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
            putStatus(headNode, false, "Mode cannot be empty.");
        }
        else
        {
            switch (mode)
            {
                case "create_account":
                    break;
                    
                case "login":
                    break;
                    
                case "logout":
                    break;
                    
                case "update_info":
                    break;
                    
                case "update_geo":
                    break;
                    
                case "search_nearby":
                    break;

                default:
                    putStatus(headNode, false, "Mode is not recognized.");
                    break;
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

    public void putStatus(JSONObject head, boolean successful, String reason)
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

}
