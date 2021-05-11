import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SQLUtils
{
    public Connection sqlConnection = null;

    private HashMap<String, String> apiKeyList;

    public static final String USER_TABLE_NAME = "users";
    public static final String SQL_LOGIN = "root";
    public static final String SQL_PASSWORD = "";

    // crypto stuff.
    public static final String MD5_SALT_PART_1 = "aasdffssfasDFJFGJAFdBigChungus";
    public static final String MD5_SALT_PART_2 = "asjf9844t9hgas9246562@$@%!%";

    public SQLUtils(String filePath) throws SQLException
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
        }
        catch (ClassNotFoundException cnfe)
        {
            cnfe.printStackTrace();
        }

        apiKeyList = new HashMap<String, String>();
        sqlConnection = DriverManager.getConnection("jdbc:sqlite:" + filePath);

        if (!tableExists(USER_TABLE_NAME))
        {
            initUserTables();
        }
    }

    public boolean tableExists(String tableName) throws SQLException
    {
        return sqlConnection.getMetaData().getTables(null, null, tableName, null).next();
    }

    public boolean columnExists(String columnName) throws SQLException
    {
        return sqlConnection.getMetaData().getColumns(null, null, USER_TABLE_NAME, columnName).next();
    }

    public void initUserTables() throws SQLException
    {
        String query = "CREATE TABLE " + USER_TABLE_NAME
            + "(email VARCHAR, username VARCHAR, password VARCHAR, age INT NULL, gender VARCHAR NULL, relationship_status VARCHAR NULL, bio VARCHAR NULL, interests VARCHAR NULL, telegram VARCHAR NULL, visible INT, lat FLOAT NULL, lon FLOAT NULL);";
        PreparedStatement stmt = sqlConnection.prepareStatement(query);
        stmt.execute();
    }

    public void addUser(String email, String username, String password) throws SQLException
    {
        String query = "INSERT INTO " + USER_TABLE_NAME + "(email, username, password) VALUES (?, ?, ?);";
        PreparedStatement stmt = sqlConnection.prepareStatement(query);
        stmt.setString(1, email);
        stmt.setString(2, username);
        stmt.setString(3, password);
        stmt.execute();
    }

    public boolean doLogin(String username, String password) throws SQLException
    {
        String query = "SELECT * FROM " + USER_TABLE_NAME + " WHERE username = ? AND password = ?;";
        PreparedStatement stmt = sqlConnection.prepareStatement(query);
        stmt.setString(1, username);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    public String generateAPITokenForAccount(String username)
    {
        String token = UUID.randomUUID().toString();

        // keep generating random UUIDs for API key if the current key is taken
        while (!addAPIToken(token, username))
        {
            token = UUID.randomUUID().toString();
        }

        return token;
    }

    private boolean addAPIToken(String token, String username)
    {
        if (apiKeyList.containsKey(token))
        {
            return false;
        }
        else
        {
            apiKeyList.put(token, username);
            return true;
        }
    }

    public String getUsernameFromAPIToken(String token)
    {
        if (apiKeyList.containsKey(token))
        {
            return apiKeyList.get(token);
        }
        else
        {
            return null;
        }
    }

    public void deregisterAPIToken(String token)
    {
        apiKeyList.remove(token);
    }

    public void updateAccountStringInfo(String username, String parameter, String newInfo) throws SQLException
    {
        String query = "UPDATE " + USER_TABLE_NAME + " SET " + parameter + " = ? WHERE username = ?";
        PreparedStatement stmt = sqlConnection.prepareStatement(query);
        stmt.setString(1, newInfo);
        stmt.setString(2, username);
        stmt.execute();
    }

    public long getRowCount(ResultSet rs) throws SQLException
    {
        long i = 0L;
        while (rs.next())
        {
            i++;
        }
        return i;
    }

    public boolean usernameExists(String username) throws SQLException
    {
        String query = "SELECT * FROM " + USER_TABLE_NAME + " WHERE username = ?;";
        PreparedStatement stmt = sqlConnection.prepareStatement(query);
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    public boolean emailTaken(String email) throws SQLException
    {
        String query = "SELECT * FROM " + USER_TABLE_NAME + " WHERE email = ?;";
        PreparedStatement stmt = sqlConnection.prepareStatement(query);
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    private NearChatUser getNearChatUserFromResultSet(ResultSet rs) throws SQLException
    {
        String interestsStr = rs.getString("interests");

        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<String>>()
        {
        }.getType();

        return new NearChatUser(rs.getLong("rowid"), rs.getString("username"), rs.getInt("age"), rs.getString("gender"),
            rs.getString("relationship_status"), rs.getString("bio"),
            interestsStr == null ? null : gson.fromJson(interestsStr, listType), rs.getString("telegram"),
            rs.getBoolean("visible"), rs.getDouble("lon"), rs.getDouble("lat"));
    }

    public NearChatUser getNearChatUserByUsername(String username) throws SQLException
    {
        String query = "SELECT rowid, * FROM " + USER_TABLE_NAME + " WHERE username = ?;";
        PreparedStatement stmt = sqlConnection.prepareStatement(query);
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();

        if (rs == null || !rs.next())
            return null;

        return getNearChatUserFromResultSet(rs);
    }

    public NearChatUser getNearChatUserByID(long id) throws SQLException
    {
        String query = "SELECT rowid, * FROM " + USER_TABLE_NAME + " WHERE rowid = ?;";
        PreparedStatement stmt = sqlConnection.prepareStatement(query);
        stmt.setLong(1, id);
        ResultSet rs = stmt.executeQuery();

        if (rs == null || !rs.next())
            return null;

        return getNearChatUserFromResultSet(rs);
    }

    public List<NearChatUser> getAllNearChatUsers() throws SQLException
    {
        String query = "SELECT rowid, * FROM " + USER_TABLE_NAME + ";";
        PreparedStatement stmt = sqlConnection.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();

        List<NearChatUser> toReturn = new ArrayList<>();

        while (rs.next())
        {
            toReturn.add(getNearChatUserFromResultSet(rs));
        }

        return toReturn;
    }

    public static String hashMD5(String input)
    {
        String md5 = null;

        if (input == null)
        {
            return null;
        }

        String saltedInput = "";
        saltedInput += MD5_SALT_PART_1;
        saltedInput += input;
        saltedInput += MD5_SALT_PART_2;

        try
        {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(saltedInput.getBytes(), 0, saltedInput.length());
            md5 = new BigInteger(1, digest.digest()).toString(16);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        return md5;
    }
}