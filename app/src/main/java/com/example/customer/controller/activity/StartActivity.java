package com.example.customer.controller.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.customer.Config.Config;
import com.example.customer.R;

public class StartActivity extends Activity {
    AlertDialog.Builder builder;
    AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

//        builder=new AlertDialog.Builder(this);
//        builder.setCancelable(false);
//
//        builder.setView(getLayoutInflater().inflate(R.layout.popup_waiting,null));
//        dialog=builder.create();
//        dialog.show();

        Intent intent;
        SharedPreferences userInfo=getSharedPreferences(Config.userInfo,Activity.MODE_PRIVATE);

        if (userInfo != null && userInfo.getBoolean("isLoggedIn", false)) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, LogInActivity.class);
        }
//        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}