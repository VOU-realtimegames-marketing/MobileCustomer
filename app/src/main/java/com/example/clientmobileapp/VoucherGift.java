package com.example.clientmobileapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class VoucherGift extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context voucher_gift_context = this;

        setContentView(R.layout.voucher_gift_layout);

        Button give_away_button = (Button) findViewById(R.id.button_give_away);
        Button trade_gift_button = (Button) findViewById(R.id.button_trade_gift);

        give_away_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent give_away_intent = new Intent(voucher_gift_context, GiveAwayActivity.class);
                startActivity(give_away_intent);
            }
        });

        trade_gift_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent trade_gift_intent = new Intent(voucher_gift_context, TradeGiftActivity.class);
                startActivity(trade_gift_intent);
            }
        });


    }
}