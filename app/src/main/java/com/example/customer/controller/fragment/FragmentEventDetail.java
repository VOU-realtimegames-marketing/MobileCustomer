package com.example.customer.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.customer.R;
import com.example.customer.data.Event;


public class FragmentEventDetail extends Fragment {

    private Event event;
    private boolean isInWishlist;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event");
            isInWishlist = getArguments().getBoolean("isInWishlist", false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);

        ImageView eventImage = view.findViewById(R.id.event_detail_image);
        TextView eventName = view.findViewById(R.id.event_detail_name);
        TextView eventGames = view.findViewById(R.id.event_detail_games);
        TextView eventStartTime = view.findViewById(R.id.event_detail_start_time);
        TextView eventEndTime = view.findViewById(R.id.event_detail_end_time);
        TextView eventVoucherQuantity = view.findViewById(R.id.event_detail_voucher_quantity);
        Button wishlistButton = view.findViewById(R.id.btn_add_to_wishlist);

        if (event != null) {
            eventImage.setImageResource(event.getEventImage());
            eventName.setText(event.getEventName());
            eventVoucherQuantity.setText("Voucher: " + String.valueOf(event.getVoucherQuantity()) + "%");


            eventStartTime.setText("Start: " + event.getStartTime());
            eventEndTime.setText("End: " + event.getEndTime());
        }

        if (isInWishlist) {
            wishlistButton.setVisibility(View.GONE);
        } else {
            wishlistButton.setVisibility(View.VISIBLE);
            wishlistButton.setOnClickListener(v -> {
                isInWishlist = true;
                addToWishlist(event);
                wishlistButton.setVisibility(View.GONE);
            });
        }

        return view;
    }

    private void addToWishlist(Event event) {
        FragmentHome homeFragment = new FragmentHome();
        Bundle args = new Bundle();
        args.putSerializable("event", event);
        args.putBoolean("isInWishlist", isInWishlist);
        homeFragment.setArguments(args);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .addToBackStack("FragmentHome")
                .commit();
    }
}
