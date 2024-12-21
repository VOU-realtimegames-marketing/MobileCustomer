package com.example.clientmobileapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context login_context = this;

        setContentView(R.layout.login_layout);


        Button login_button = (Button) findViewById(R.id.button_login);
        Button regist_button = (Button) findViewById(R.id.button_regist);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.finish();
            }
        });

        regist_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regist_intent = new Intent(login_context, RegistActivity.class);
                startActivity(regist_intent);
            }
        });


    }
}
