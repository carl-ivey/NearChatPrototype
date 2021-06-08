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
    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

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
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    AlertDialog alertDialog = new AlertDialog.Builder(LoginScreen.this).create();
                                    alertDialog.setTitle("Error");
                                    alertDialog.setMessage("Login unsuccessful.");
                                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            alertDialog.dismiss();
                                        }
                                    });
                                    alertDialog.show();
                                }
                            });
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
        catch (Exception e)
        {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage(e.getMessage());
            alertDialog.show();
            e.printStackTrace();
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