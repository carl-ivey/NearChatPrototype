import java.io.Serializable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class NearChatUser implements Serializable
{

    private static final long serialVersionUID = 1L;

    @SerializedName("id")
    public long id;
    @SerializedName("username")
    public String username;
    @SerializedName("age")
    public int age;
    @SerializedName("gender")
    public String gender;
    @SerializedName("relationship_status")
    public String relationship_status;
    @SerializedName("bio")
    public String bio;
    @SerializedName("interests")
    public List<String> interests;
    @SerializedName("telegram")
    public String telegram;
    @SerializedName("visible")
    public boolean visible;
    @SerializedName("lat")
    public double lat;
    @SerializedName("lon")
    public double lon;

    public NearChatUser(long id, String username, int age, String gender, String relationship_status, String bio,
        List<String> interests, String telegram, boolean visible, double lat, double lon)
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

    public static NearChatUser fromJSONObject(JSONObject jsonObj) throws JSONException
    {
        Gson gson = new Gson();
        return (NearChatUser) gson.fromJson(jsonObj.toString(), NearChatUser.class);
    }

    public JSONObject toJSONObject(boolean showCoords)
    {
        Gson gson = new Gson();
        JSONObject toReturn = new JSONObject(gson.toJson(this));
        if (!showCoords)
        {
            toReturn.remove("lat");
            toReturn.remove("lon");
        }
        return toReturn;
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
     * A function to return the Haversine distance in a specified unit between
     * two coordinate pairs P1 and P2.
     * 
     * @param lat1
     *            the latitude of point P1
     * @param lon1
     *            the longitude of point P1
     * @param lat2
     *            the latitude of point P2
     * @param lon2
     *            the longitude of point P2
     * @param metric,
     *            if true distance unit is in kilometers, if false it's in miles
     * @return the Haversine distance between points P1 and P2.
     */
    private double distance(double lat1, double lon1, double lat2, double lon2, boolean metric)
    {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2.0), 2)
            + Math.pow(Math.sin(dLon / 2.0), 2) * Math.cos(lat1) * Math.cos(lat2);
        double rad = 6371.0; // radius of earth in km
        double c = 2 * Math.asin(Math.sqrt(a));

        return metric ? rad * c : rad * c * 0.62137119;
    }

    /**
     * A function to return the distance of the user in kilometers (in relation
     * to another pair of coordinates).
     *
     * @param lat1
     *            (the latitude of the measured object in degrees)
     * @param lon1
     *            (the longitude of the measured object in degrees)
     * @return the distance in miles between the location of the instance of
     *         this NearChatUser class and another pair of coordinates.
     */
    public double distanceFromCoords(double lat1, double lon1)
    {
        return distance(lat1, lon1, this.lat, this.lon, false);
    }
}
