import org.json.JSONArray;
import org.json.JSONObject;

public class NearChatUser
{
    public long id;
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

    public NearChatUser(long id, String username, int age, String gender, String relationship_status, String bio,
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
        return toJSONObject(false);
    }
    
    @Override
    public String toString()
    {
        return toJSONObject(true).toString();
    }
    
    /**
     * A function to return the Haversine distance in a specified unit between two coordinate pairs P1 and P2.
     * 
     * @param lat1 the latitude of point P1
     * @param lon1 the longitude of point P1
     * @param lat2 the latitude of point P2
     * @param lon2 the longitude of point P2
     * @param metric, if true distance unit is in kilometers, if false it's in miles
     * @return the Haversine distance between points P1 and P2.
     */
    private double distance(double lat1, double lon1, double lat2, double lon2, boolean metric)
    {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2.0), 2) + Math.pow(Math.sin(dLon / 2.0), 2) * Math.cos(lat1) * Math.cos(lat2);
        double rad = 6371.0; // radius of earth in km
        double c = 2 * Math.asin(Math.sqrt(a));
        
        return metric ? rad * c : rad * c * 0.62137119;
    }

    /**
     * A function to return the distance of the furry in kilometers (in relation to another pair of coordinates).
     *
     * @param lat1 (the latitude of the measured object in degrees)
     * @param lon1 (the longitude of the measured object in degrees)
     * @return the distance in miles between the location of the instance of this Furry class and another pair of coordinates.
     */
    public double distanceFromCoords(double lat1, double lon1)
    {
        return distance(lat1, lon1, this.lat, this.lon, false);
    }
}
