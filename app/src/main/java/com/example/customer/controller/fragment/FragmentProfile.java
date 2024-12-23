package com.example.customer.controller.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.customer.R;
import com.example.customer.controller.activity.LogInActivity;
import com.example.customer.utils.Utils;

public class FragmentProfile extends Fragment {
    Activity context;
    ImageView imgAvatar;
    TextView txtUserName, txtFullName, txtEmail, txtRole;
    Button btnLogOut;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_profile, null);
        imgAvatar = (ImageView) linearLayout.findViewById(R.id.imgAvatar);
        txtUserName = (TextView) linearLayout.findViewById(R.id.txtUserName);
        txtFullName = (TextView) linearLayout.findViewById(R.id.txtFullName);
        txtEmail = (TextView) linearLayout.findViewById(R.id.txtEmail);
        txtRole = (TextView) linearLayout.findViewById(R.id.txtRole);
        btnLogOut = (Button) linearLayout.findViewById(R.id.btnLogOut);


        txtUserName.setText("User Name: " + Utils.getUserName(context));
        txtFullName.setText("Full Name: " + Utils.getFullName(context));
        txtEmail.setText("Email: " + Utils.getEmail(context));
        txtRole.setText("Role: " + Utils.getRole(context));


        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.removeUserInfo(context);
                Intent intent = new Intent(context, LogInActivity.class);
                startActivity(intent);
                context.finish();
            }
        });

        return linearLayout;
    }


}
