package com.natusvincere.nearchat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.natusvincere.nearchat.api.NearChatUser;

public class UIUtil
{
    private AppCompatActivity activity;
    private Context context;

    public UIUtil(AppCompatActivity activity, Context context)
    {
        this.activity = activity;
        this.context = context;
    }

    public void spawnDialogBox(String title, String message)
    {
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle(title);
                alertDialog.setMessage(message);
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

    public NearChatUser getContextNearChatUser()
    {
        return (NearChatUser) activity.getIntent().getSerializableExtra("NearChatUser");
    }

    public void launchActivityWithNearChatUser(Class<?> cls, int requestId, NearChatUser nearChatUser)
    {
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Intent intent = new Intent(context, cls);
                intent.putExtra("NearChatUser", nearChatUser);
                activity.startActivityForResult(intent, requestId);
            }
        });
    }

    public void launchActivity(Class<?> cls, int requestId)
    {
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Intent intent = new Intent(context, cls);
                activity.startActivityForResult(intent, requestId);
            }
        });
    }

    public void finishActivity()
    {
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                activity.finish();
            }
        });
    }
}
