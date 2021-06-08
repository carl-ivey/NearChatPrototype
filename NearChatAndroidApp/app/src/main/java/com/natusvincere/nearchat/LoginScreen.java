package com.natusvincere.nearchat;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;

import java.io.IOException;

public class LoginScreen extends AppCompatActivity
{
    public static final int REGISTER_ACCOUNT_REQUEST = 2;

    private UIUtil uiUtil;
    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        uiUtil = new UIUtil(this, this);

        usernameEditText = (EditText) findViewById(R.id.loginScreenUsername);
        passwordEditText = (EditText) findViewById(R.id.loginScreenPassword);
    }

    public void checkLoginDetails(View view)
    {
        try
        {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        if (DataStore.apiClient.doLogin(username, password))
                        {
                            finish();
                        }
                        else
                        {
                            uiUtil.spawnDialogBox("Error", "Login unsuccessful.");
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        uiUtil.spawnDialogBox("Error", e.getMessage());
                    }
                }
            });
            thread.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            uiUtil.spawnDialogBox("Error", e.getMessage());
            finish();
        }
    }

    public void launchRegisterAccountScreen(View view)
    {
        Intent intent = new Intent(this, RegisterAcctScreen.class);
        startActivityForResult(intent, REGISTER_ACCOUNT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REGISTER_ACCOUNT_REQUEST)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (DataStore.apiClient.checkSessionValid())
                        {
                            finish();
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