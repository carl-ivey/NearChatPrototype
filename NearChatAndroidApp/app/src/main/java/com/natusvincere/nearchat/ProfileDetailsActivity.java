package com.natusvincere.nearchat;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.natusvincere.nearchat.api.NearChatUser;

public class ProfileDetailsActivity extends AppCompatActivity
{
    private UIUtil uiUtil;
    private TextView usernameTextView;
    private TextView genderTextView;
    private TextView ageTextView;
    private TextView relationshipStatusTextView;
    private TextView telegramTextView;
    private TextView bioTextView;
    private TextView interestsTextView;
    private NearChatUser selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
        setTitle("User Info");

        usernameTextView = (TextView) findViewById(R.id.profileDetailsUsername);
        genderTextView = (TextView) findViewById(R.id.profileDetailsGender);
        ageTextView = (TextView) findViewById(R.id.profileDetailsAge);
        relationshipStatusTextView = (TextView) findViewById(R.id.profileDetailsRelationshipStatus);
        telegramTextView = (TextView) findViewById(R.id.profileDetailsTelegram);
        bioTextView = (TextView) findViewById(R.id.profileDetailsBiographyText);
        interestsTextView = (TextView) findViewById(R.id.profileDetailsInterestsList);

        uiUtil = new UIUtil(this, this);
        selectedUser = uiUtil.getContextNearChatUser();
        updateTextViews();
    }

    public void updateTextViews()
    {
        usernameTextView.setText(selectedUser.username);
        genderTextView.setText(selectedUser.gender == null ? "Unknown gender" : selectedUser.gender);
        ageTextView.setText(selectedUser.age == 0 ? "Unknown age" : "Age " + selectedUser.age);
        relationshipStatusTextView.setText(selectedUser.relationship_status == null ? "Unknown relationship status" : selectedUser.relationship_status);
        telegramTextView.setText(selectedUser.telegram == null ? "Unknown Telegram" : "Telegram: " + selectedUser.telegram);
        bioTextView.setText(selectedUser.bio == null ? "-" : selectedUser.bio);
        if (selectedUser.interests == null)
        {
            interestsTextView.setText("-");
        }
        else
        {
            String interestsTextViewText = "";
            for (String interest : selectedUser.interests)
            {
                interestsTextViewText += interest + "\n";
            }
            interestsTextView.setText(interestsTextViewText);
        }
    }

    public void openTelegramLink(View view)
    {
        if (selectedUser == null || selectedUser.telegram == null)
        {
            return;
        }
        if (selectedUser.telegram.trim().length() == 0)
            return;
        Intent telegramIntent = new Intent(Intent.ACTION_VIEW , Uri.parse("https://telegram.me/" + selectedUser.telegram));
        startActivity(telegramIntent);
    }
}