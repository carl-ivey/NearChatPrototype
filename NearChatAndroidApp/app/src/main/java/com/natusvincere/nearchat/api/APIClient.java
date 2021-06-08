package com.natusvincere.nearchat.api;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
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

    public static final String CLASS_NAME = "APIClient";

    public APIClient(String serverUrl)
    {
        this(serverUrl, false);
    }

    public APIClient(String serverUrl, boolean isDebug)
    {
        this.serverUrl = serverUrl;
        this.isDebug = isDebug;
    }

    private HttpResponseData getHttpResponse(Map<String, String> requestProperties) throws IOException
    {
        ANRequest.PostRequestBuilder builder = AndroidNetworking.post(serverUrl);
        for (String paramName : requestProperties.keySet())
        {
            builder = builder.addBodyParameter(paramName, requestProperties.get(paramName));
        }
        boolean[] done = {false};
        String[] responseArr = new String[1];

        if (isDebug)
        {
            Log.d(CLASS_NAME, "properties=" + requestProperties.toString());
        }

        builder.setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        // do anything with response
                        responseArr[0] = response;
                        done[0] = true;
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        done[0] = true;
                    }
                });

        double time = System.currentTimeMillis();
        while (!done[0])
        {
            if (System.currentTimeMillis() - time >= 5000)
            {
                Log.d(CLASS_NAME, "Timeout");
                return new HttpResponseData(6295, responseArr[0]);
            }
        }

        Log.d(CLASS_NAME, "Response=" + responseArr[0]);

        return new HttpResponseData(6295, responseArr[0]);
    }

    public boolean checkSessionValid() throws IOException, JSONException
    {
        return getLoggedInUser() != null;
    }

    public boolean setToken(String apiToken) throws IOException, JSONException
    {
        this.apiToken = apiToken;
        return checkSessionValid();
    }

    public boolean doLogin(String username, String password) throws IOException, JSONException
    {
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("mode", "login");
        requestProperties.put("username", username);
        requestProperties.put("password", password);
        HttpResponseData responseData = getHttpResponse(requestProperties);
        if (responseData.responseStr == null)
            return false;
        JSONObject mainObj = new JSONObject(responseData.responseStr);

        if (mainObj == null)
            return false;

        boolean success = mainObj.getString("status").equals("success");
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
        HttpResponseData responseData = getHttpResponse(requestProperties);
        if (responseData.responseStr == null)
            return false;
        JSONObject mainObj = new JSONObject(responseData.responseStr);

        if (mainObj == null)
            return false;

        boolean success = mainObj.getString("status").equals("success");
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
        HttpResponseData responseData = getHttpResponse(requestProperties);
        if (responseData.responseStr == null)
            return null;
        JSONObject mainObj = new JSONObject(responseData.responseStr);

        if (mainObj == null)
            return null;

        boolean success = mainObj.getString("status").equals("success");
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
        HttpResponseData responseData = getHttpResponse(requestProperties);

        if (responseData.responseStr == null)
            return null;

        JSONObject mainObj = new JSONObject(responseData.responseStr);

        if (mainObj == null)
            return null;

        boolean success = mainObj.getString("status").equals("success");

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
        HttpResponseData responseData = getHttpResponse(requestProperties);

        if (responseData.responseStr == null)
            return false;

        JSONObject mainObj = new JSONObject(responseData.responseStr);

        if (mainObj == null)
            return false;

        boolean success = mainObj.getString("status").equals("success");
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

        HttpResponseData responseData = getHttpResponse(requestProperties);

        if (responseData.responseStr == null)
            return false;

        JSONObject mainObj = new JSONObject(responseData.responseStr);

        if (mainObj == null)
            return false;

        boolean success = mainObj.getString("status").equals("success");
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
        HttpResponseData responseData = getHttpResponse(requestProperties);

        if (responseData.responseStr == null)
            return null;

        JSONObject mainObj = new JSONObject(responseData.responseStr);

        if (mainObj == null)
            return null;

        boolean success = mainObj.getString("status").equals("success");
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
        HttpResponseData responseData = getHttpResponse(requestProperties);

        if (responseData.responseStr == null)
            return null;

        JSONObject mainObj = new JSONObject(responseData.responseStr);

        if (mainObj == null)
            return null;

        boolean success = mainObj.getString("status").equals("success");
        if (!success)
            return null;
        String resultStr = mainObj.getString("result");
        JSONObject resultObj = new JSONObject(resultStr);
        return resultObj == null ? null : NearChatUser.fromJSONObject(resultObj);
    }

    public boolean logOut() throws IOException, JSONException
    {
        if (this.apiToken == null)
            return false;

        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("mode", "logout");
        requestProperties.put("token", this.apiToken);

        HttpResponseData responseData = getHttpResponse(requestProperties);

        if (responseData.responseStr == null)
            return false;

        JSONObject mainObj = new JSONObject(responseData.responseStr);

        if (mainObj == null)
            return false;

        boolean success = mainObj.getString("status").equals("success");
        return success;
    }
}
