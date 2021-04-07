import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class SQLUtils 
{
    public Connection sqlConnection = null;
    
    private HashMap<String, Long> apiKeyList;

    public static final String USER_TABLE_NAME = "userdatabase";
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
        
        apiKeyList = new HashMap<String, Long>();
        sqlConnection = DriverManager.getConnection("jdbc:sqlite:" + filePath);
    }
    
    public String generateAPIKeyForAccount(long id)
    {
        String token = UUID.randomUUID().toString();
        
        // keep generating random UUIDs for API key if the current key is taken
        while (!addAPIKey(token, id))
        {   
            token = UUID.randomUUID().toString();
        }
        
        return token;
    }
    
    private boolean addAPIKey(String key, long id)
    {
        if (apiKeyList.containsKey(key))
        {
            return false;
        }
        else
        {
            apiKeyList.put(key, id);
            return true;
        }
    }
    
    public long getIDFromAPIKey(String key)
    {
        if (apiKeyList.containsKey(key))
        {
            return apiKeyList.get(key);
        }
        else
        {
            return -1L;
        }
    }
    
    public void updateAccountStringInfo(long id, String parameter, String newInfo) throws SQLException
    {
        if (parameter.equals("id"))
        {
            return;
        }
        
        synchronized (sqlConnection)
        {
            String query = "UPDATE " + USER_TABLE_NAME + " SET " + parameter + " = ? WHERE id = ?";
            PreparedStatement stmt = sqlConnection.prepareStatement(query);
            stmt.setString(1, newInfo);
            stmt.setLong(2, id);
            stmt.execute();
        }
    }
    
    
    public long getNextId() throws SQLException
    {
        String query = "SELECT * FROM " + USER_TABLE_NAME;
        PreparedStatement stmt = sqlConnection.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        return getRowCount(rs);
    }
    
    private long getRowCount(ResultSet rs) throws SQLException 
    {
        long i = 0L;
        while (rs.next()) 
        {
            i++;
        }
        rs.first();
        return i;
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
