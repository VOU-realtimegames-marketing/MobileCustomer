package com.example.customer.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

public class TextWatcherUtils {
    public static TextWatcher createTextWatcher(TextView...textViews){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                for(TextView textView:textViews){
                    textView.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }
}
