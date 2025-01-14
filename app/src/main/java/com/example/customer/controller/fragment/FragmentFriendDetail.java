package com.example.customer.controller.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.customer.R;
import com.example.customer.data.Friend;


public class FragmentFriendDetail extends Fragment {

    private Friend friend;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            friend = (Friend) getArguments().getSerializable("friend");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_detail, container, false);

        ImageView imageView = view.findViewById(R.id.friend_detail_image);
        TextView fullname = view.findViewById(R.id.friend_fullname);
        TextView name = view.findViewById(R.id.friend_name);
        TextView email = view.findViewById(R.id.friend_email);
        TextView phone = view.findViewById(R.id.friend_phone);
        TextView status = view.findViewById(R.id.friend_status);
        Button btnGiveAwayVoucher = view.findViewById(R.id.btn_give_away_voucher);
        Button btnGiveAwayPlayturn = view.findViewById(R.id.btn_give_away_playturn);

        if (friend != null) {
            imageView.setImageResource(R.drawable.default_avatar);
            fullname.setText(friend.getFullName());
            name.setText("Name: " + friend.getName());
//            email.setText("Email: " + friend.getEmail());
//            phone.setText("Phone: " + friend.getPhone());
//            if (friend.getStatus().equals("ask_playturn")){
//                status.setText("Status: Asking for Playturn");
//            }
//            else{
//                status.setText("Status: " + friend.getStatus());
//            }

        }

        btnGiveAwayVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnGiveAwayPlayturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        return view;

    }
}