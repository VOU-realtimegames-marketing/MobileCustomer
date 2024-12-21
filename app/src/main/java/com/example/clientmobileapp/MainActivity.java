package com.example.clientmobileapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity{

    String[] items ={"Quiz vui có quà", "Lắc ngay trúng thưởng", "Thích thì Quiz, không thì Quit", "Trò nào cũng có"};
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context main_context = this;
        Intent login_intent = new Intent(main_context, LoginActivity.class);
        startActivity(login_intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Context main_context = this;
        setContentView(R.layout.main_layout);
        ListView list_event = (ListView) findViewById(R.id.listview_event);
        list_event.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent login_intent = new Intent(main_context, EventActivity.class);
                startActivity(login_intent);
            }
        });

        ArrayAdapter<String> event_adapter = new ArrayAdapter<String>(this, R.layout.item_layout, R.id.event_name, items);
        list_event.setAdapter(event_adapter);



    }
}
