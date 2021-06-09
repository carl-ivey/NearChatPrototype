package com.natusvincere.nearchat;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.natusvincere.nearchat.api.APIClient;

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
{
    public static final int LOGIN_REQUEST = 1;

    private UIUtil uiUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        uiUtil = new UIUtil(this, this);

        AndroidNetworking.initialize(getApplicationContext());

        if (DataStore.apiClient == null)
            DataStore.apiClient = new APIClient("http://launchtestrun.zapto.org:42069/NearChatAPIServer/APIServlet", true);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if (!DataStore.apiClient.checkSessionValid())
                    {
                        uiUtil.launchActivity(LoginScreen.class, LOGIN_REQUEST);
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == LOGIN_REQUEST)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run() {
                    try {
                        if (!DataStore.apiClient.checkSessionValid())
                        {
                            uiUtil.launchActivity(LoginScreen.class, LOGIN_REQUEST);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}