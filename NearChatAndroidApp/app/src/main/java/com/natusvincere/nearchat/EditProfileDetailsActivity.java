package com.natusvincere.nearchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class EditProfileDetailsActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_details);
        setTitle("Edit Profile");
    }

    public void updateProfileAndExitActivity(View view)
    {

    }
}