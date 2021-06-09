package com.natusvincere.nearchat;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.androidnetworking.AndroidNetworking;
import com.natusvincere.nearchat.api.APIClient;
import com.natusvincere.nearchat.api.NearChatUser;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import im.delight.android.location.SimpleLocation;

public class MainActivity extends AppCompatActivity
{
    public static final int LOGIN_REQUEST = 1;
    public static final int PROFILE_DETAILS_REQUEST = 2;

    private UIUtil uiUtil;
    private SimpleLocation location;
    private ListView proximityListView;
    private ArrayAdapter<NearChatUser> itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        uiUtil = new UIUtil(this, this);
        location = new SimpleLocation(this);

        if (!location.hasLocationEnabled())
        {
            // ask the user to enable location access
            SimpleLocation.openSettings(this);
        }

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
        setTitle(String.format("Nearby Users (%.1f km)", DataStore.searchDistance));

        proximityListView = (ListView) findViewById(R.id.proximityListView);

        proximityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
                NearChatUser selectedUser = DataStore.nearbyUsers.get(position);
                uiUtil.launchActivityWithNearChatUser(ProfileDetailsActivity.class, PROFILE_DETAILS_REQUEST, selectedUser);
            }
        });

        updateProximityList();

        /*
        location.setListener(new SimpleLocation.Listener() {

            public void onPositionChanged() {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
            }

        });
         */
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
                        else
                        {
                            DataStore.nearbyUsers = DataStore.apiClient.getNearbyUsers(0.0, 0.0, DataStore.searchDistance);
                            updateProximityList();
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

    private void updateProximityList()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int index = proximityListView.getFirstVisiblePosition(); //This changed
                View v = proximityListView.getChildAt(0);
                int top = (v == null) ? 0 : v.getTop();
                itemsAdapter = new ArrayAdapter<>
                        (MainActivity.this, android.R.layout.simple_list_item_1,
                                DataStore.nearbyUsers);
                proximityListView.setAdapter(itemsAdapter);
                itemsAdapter.notifyDataSetChanged();
                proximityListView.setSelectionFromTop(index, top);
            }
        });

    }
}