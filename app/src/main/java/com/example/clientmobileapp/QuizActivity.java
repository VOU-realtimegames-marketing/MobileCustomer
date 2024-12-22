package com.example.clientmobileapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class QuizActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.quiz_layout);

        Button quit_button = (Button) findViewById(R.id.button_quit);

        quit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuizActivity.this.finish();
            }
        });
    }
}