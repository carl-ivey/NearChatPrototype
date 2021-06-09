package com.natusvincere.nearchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class ProfileDetailsActivity extends AppCompatActivity {
    private UIUtil uiUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
        setTitle("User Info");

        uiUtil = new UIUtil(this, this);


        Log.d("selected", uiUtil.getContextNearChatUser().toString());
    }
}