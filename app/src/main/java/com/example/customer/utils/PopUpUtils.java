package com.example.customer.utils;

import android.app.Activity;
import android.app.AlertDialog;

import com.example.customer.R;

public class PopUpUtils {
    public static AlertDialog.Builder createBuilderWaiting(Activity context){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(context.getLayoutInflater().inflate(R.layout.popup_waiting,null));
        return builder;
    }
}
