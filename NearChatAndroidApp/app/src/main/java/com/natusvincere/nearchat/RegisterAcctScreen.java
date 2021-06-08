package com.natusvincere.nearchat;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import org.json.JSONException;

import java.io.IOException;

public class RegisterAcctScreen extends AppCompatActivity {

    private UIUtil uiUtil;
    private EditText emailEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private CheckBox tosCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_acct_screen);

        uiUtil = new UIUtil(this, this);
        emailEditText = (EditText) findViewById(R.id.registerAccountEmail);
        usernameEditText = (EditText) findViewById(R.id.registerAccountUsername);
        passwordEditText = (EditText) findViewById(R.id.registerAccountPassword);
        confirmPasswordEditText = (EditText) findViewById(R.id.registerAccountConfirmPassword);
        tosCheckBox = (CheckBox) findViewById(R.id.registerAccountTosCheckBox);
    }

    public void checkRegistrationDetails(View view)
    {
        String email = emailEditText.getText().toString();
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        boolean boxChecked = tosCheckBox.isChecked();
        if (!boxChecked)
        {
            uiUtil.spawnDialogBox("Error", "Please agree to the Terms of Service.");
            return;
        }

        if (!password.matches(confirmPassword))
        {
            uiUtil.spawnDialogBox("Error", "Password and confirm password contents must match.");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (DataStore.apiClient.doRegistration(email, username, password))
                    {
                        finish();
                    }
                    else
                    {
                        uiUtil.spawnDialogBox("Error", "Account creation unsuccessful.");
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