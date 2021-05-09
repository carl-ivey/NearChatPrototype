import org.json.JSONArray;
import org.json.JSONObject;

public class NearChatUser
{
    public int id;
    public String username;
    public int age;
    public String gender;
    public String relationship_status;
    public String bio;
    public JSONArray interests;
    public String telegram;
    public boolean visible;
    public double lat;
    public double lon;

    public NearChatUser(int id, String username, int age, String gender, String relationship_status, String bio,
        JSONArray interests, String telegram, boolean visible, double lat, double lon)
    {
        this.id = id;
        this.username = username;
        this.age = age;
        this.gender = gender;
        this.relationship_status = relationship_status;
        this.bio = bio;
        this.interests = interests;
        this.telegram = telegram;
        this.visible = visible;
        this.lat = lat;
        this.lon = lon;
    }

    public JSONObject toJSONObject(boolean showCoords)
    {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("username", username);
        obj.put("age", age);
        obj.put("gender", gender);
        obj.put("relationship_status", relationship_status);
        obj.put("bio", bio);
        obj.put("interests", interests == null ? null : interests.toString());
        obj.put("telegram", telegram);
        obj.put("visible", visible);
        if (showCoords)
        {
            obj.put("lat", lat);
            obj.put("lon", lon);
        }
        return obj;
    }

    public JSONObject toJSONObject()
    {
        return toJSONObject(true);
    }
    
    @Override
    public String toString()
    {
        return toJSONObject(true).toString();
    }
}
