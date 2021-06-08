package com.natusvincere.nearchat.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class APIClient
{
    public boolean isDebug;
    public String serverUrl;
    public String apiToken;

    public APIClient(String serverUrl)
    {
        this(serverUrl, false);
    }

    public APIClient(String serverUrl, boolean isDebug)
    {
        this.serverUrl = serverUrl;
        this.isDebug = isDebug;
    }

    private HttpResponseData getHttpResponse(String requestMethod, Map<String, String> requestProperties) throws IOException
    {
        URL url = new URL(serverUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(requestMethod);

        for (String key : requestProperties.keySet())
        {
            conn.setRequestProperty(key, requestProperties.get(key));
        }

        conn.connect();

        int responseCode = conn.getResponseCode();
        String responseData = "";

        if (responseCode != 200)
        {
            return new HttpResponseData(responseCode, null);
        }

        Scanner scanner = new Scanner(url.openStream());
        while (scanner.hasNext())
        {
            responseData += scanner.nextLine();
        }

        //Close the scanner
        scanner.close();

        return new HttpResponseData(responseCode, responseData);
    }

    public boolean doLogin(String username, String password) throws IOException, JSONException
    {
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("mode", "login");
        requestProperties.put("username", username);
        requestProperties.put("password", password);
        HttpResponseData responseData = getHttpResponse("GET", requestProperties);
        if (responseData.responseStr == null)
            return false;
        JSONObject mainObj = new JSONObject(responseData.responseStr);

        if (mainObj == null)
            return false;

        boolean success = mainObj.getBoolean("success");
        apiToken = success ? mainObj.getString("token") : apiToken;
        return success;
    }

    public boolean doRegistration(String email, String username, String password) throws IOException, JSONException
    {
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("mode", "create_account");
        requestProperties.put("email", email);
        requestProperties.put("username", username);
        requestProperties.put("password", password);
        HttpResponseData responseData = getHttpResponse("GET", requestProperties);
        if (responseData.responseStr == null)
            return false;
        JSONObject mainObj = new JSONObject(responseData.responseStr);

        if (mainObj == null)
            return false;

        boolean success = mainObj.getBoolean("success");
        apiToken = success ? mainObj.getString("token") : apiToken;
        return success;
    }

    public NearChatUser getLoggedInUser() throws IOException, JSONException
    {
        if (this.apiToken == null)
            return null;

        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("mode", "get_cur_userinfo");
        requestProperties.put("token", this.apiToken);
        HttpResponseData responseData = getHttpResponse("GET", requestProperties);
        if (responseData.responseStr == null)
            return null;
        JSONObject mainObj = new JSONObject(responseData.responseStr);

        if (mainObj == null)
            return null;

        boolean success = mainObj.getBoolean("success");
        if (!success)
            return null;
        String resultStr = mainObj.getString("result");
        JSONObject resultObj = new JSONObject(resultStr);
        return resultObj == null ? null : NearChatUser.fromJSONObject(resultObj);
    }

    public List<NearChatUser> getNearbyUsers(double latitude, double longitude, double radius) throws IOException, JSONException
    {
        if (this.apiToken == null)
            return null;

        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("mode", "search_nearby");
        requestProperties.put("lat", "" + latitude);
        requestProperties.put("lon", "" + longitude);
        requestProperties.put("radius", "" + radius);
        requestProperties.put("token", this.apiToken);
        HttpResponseData responseData = getHttpResponse("GET", requestProperties);

        if (responseData.responseStr == null)
            return null;

        JSONObject mainObj = new JSONObject(responseData.responseStr);

        if (mainObj == null)
            return null;

        boolean success = mainObj.getBoolean("success");

        if (!success)
            return null;

        String resultsArrStr = mainObj.getString("results");
        Type userListType = new TypeToken<ArrayList<NearChatUser>>(){}.getType();
        return new Gson().fromJson(resultsArrStr, userListType);
    }

    public boolean updateCurrentUserLocation(double latitude, double longitude) throws IOException, JSONException
    {
        if (this.apiToken == null)
            return false;

        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("mode", "update_geo");
        requestProperties.put("lat", "" + latitude);
        requestProperties.put("lon", "" + longitude);
        requestProperties.put("token", this.apiToken);
        HttpResponseData responseData = getHttpResponse("GET", requestProperties);

        if (responseData.responseStr == null)
            return false;

        JSONObject mainObj = new JSONObject(responseData.responseStr);

        if (mainObj == null)
            return false;

        boolean success = mainObj.getBoolean("success");
        return success;
    }

    public boolean updateUserFieldsFromClass(NearChatUser user) throws IOException, JSONException
    {
        if (this.apiToken == null || user == null)
            return false;

        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("mode", "update_geo");
        requestProperties.put("token", this.apiToken);

        JSONObject jsonObject = user.toJSONObject();

        if (jsonObject == null)
            return false;

        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext())
        {
            String key = keys.next();
            requestProperties.put(key, jsonObject.get(key).toString());
        }

        HttpResponseData responseData = getHttpResponse("GET", requestProperties);

        if (responseData.responseStr == null)
            return false;

        JSONObject mainObj = new JSONObject(responseData.responseStr);

        if (mainObj == null)
            return false;

        boolean success = mainObj.getBoolean("success");
        return success;
    }

    public NearChatUser getUserById(long id) throws IOException, JSONException
    {
        if (this.apiToken == null)
            return null;

        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("mode", "get_userinfo");
        requestProperties.put("id", "" + id);
        requestProperties.put("token", this.apiToken);
        HttpResponseData responseData = getHttpResponse("GET", requestProperties);

        if (responseData.responseStr == null)
            return null;

        JSONObject mainObj = new JSONObject(responseData.responseStr);

        if (mainObj == null)
            return null;

        boolean success = mainObj.getBoolean("success");
        if (!success)
            return null;
        String resultStr = mainObj.getString("result");
        JSONObject resultObj = new JSONObject(resultStr);
        return resultObj == null ? null : NearChatUser.fromJSONObject(resultObj);
    }

    public NearChatUser getUserByUsername(String username) throws IOException, JSONException
    {
        if (this.apiToken == null)
            return null;

        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("mode", "get_userinfo");
        requestProperties.put("username", "" + username);
        requestProperties.put("token", this.apiToken);
        HttpResponseData responseData = getHttpResponse("GET", requestProperties);

        if (responseData.responseStr == null)
            return null;

        JSONObject mainObj = new JSONObject(responseData.responseStr);

        if (mainObj == null)
            return null;

        boolean success = mainObj.getBoolean("success");
        if (!success)
            return null;
        String resultStr = mainObj.getString("result");
        JSONObject resultObj = new JSONObject(resultStr);
        return resultObj == null ? null : NearChatUser.fromJSONObject(resultObj);
    }
}
