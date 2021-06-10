package com.natusvincere.nearchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.natusvincere.nearchat.api.NearChatUser;

public class EditProfileDetailsActivity extends AppCompatActivity
{
    private UIUtil uiUtil;
    private TextView usernameTextView;
    private EditText genderEditText;
    private EditText ageEditText;
    private EditText relationshipStatusEditText;
    private EditText telegramEditText;
    private EditText bioEditText;
    private EditText interestsEditText;
    private NearChatUser selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_details);
        setTitle("Edit Profile");

        usernameTextView = (TextView) findViewById(R.id.profileDetailsUsernameEdit);
        genderEditText = (EditText) findViewById(R.id.profileDetailsGenderEditText);
        ageEditText = (EditText) findViewById(R.id.profileDetailsAgeEditText);
        relationshipStatusEditText = (EditText) findViewById(R.id.profileDetailsRelationshipStatusEditText);
        telegramEditText = (EditText) findViewById(R.id.profileDetailsTelegramEditText);
        bioEditText = (EditText) findViewById(R.id.profileDetailsBiographyEditText);
        interestsEditText = (EditText) findViewById(R.id.profileDetailsInterestsListEditText);

        uiUtil = new UIUtil(this, this);
        selectedUser = uiUtil.getContextNearChatUser();
        updateTextViews();
    }

    public void updateTextViews()
    {
        usernameTextView.setText(selectedUser.username);
        genderEditText.setText(selectedUser.gender == null ? "" : selectedUser.gender);
        ageEditText.setText(selectedUser.age == 0 ? "" : "" + selectedUser.age);
        relationshipStatusEditText.setText(selectedUser.relationship_status == null ? "" : selectedUser.relationship_status);
        telegramEditText.setText(selectedUser.telegram == null ? "" : selectedUser.telegram);
        bioEditText.setText(selectedUser.bio == null ? "" : selectedUser.bio);
        if (selectedUser.interests != null)
        {
            String interestsTextViewText = "";
            for (String interest : selectedUser.interests)
            {
                interestsTextViewText += interest + "\n";
            }
            interestsEditText.setText(interestsTextViewText);
        }
    }

    public void updateProfileAndExitActivity(View view)
    {

    }
}