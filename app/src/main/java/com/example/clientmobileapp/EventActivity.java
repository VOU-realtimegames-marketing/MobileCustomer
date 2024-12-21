package com.example.clientmobileapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EventActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context event_context = this;
        setContentView(R.layout.event_layout);

        Button quiz_button = (Button) findViewById(R.id.button_quiz);
        Button shake_button = (Button) findViewById(R.id.button_shake);
        Button back_button = (Button) findViewById(R.id.button_back);

        quiz_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent quiz_intent = new Intent(event_context, QuizActivity.class);
                startActivity(quiz_intent);
            }
        });

        shake_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shake_intent = new Intent(event_context, ShakeActivity.class);
                startActivity(shake_intent);
            }
        });


        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventActivity.this.finish();
            }
        });


    }
}
