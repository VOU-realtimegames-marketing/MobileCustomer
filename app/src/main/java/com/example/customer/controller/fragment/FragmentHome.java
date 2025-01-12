package com.example.customer.controller.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.customer.R;
import com.example.customer.data.Event;

import java.util.ArrayList;
import java.util.List;


public class FragmentHome extends Fragment {

    Event event1 = new Event("event1", new String[]{"Game1", "Game2"}, "Event 1", R.drawable.ic_launcher_foreground, 20f, null, null);
    Event event2 = new Event("event2", new String[]{"Game1", "Game2"}, "Event 2", R.drawable.ic_launcher_foreground, 30f, null, null);
    Event event3 = new Event("event3", new String[]{"Game1", "Game2"}, "Event 3", R.drawable.ic_launcher_foreground, 10f, null, null);
    Event event4 = new Event("event4", new String[]{"Game1", "Game2"}, "Event 4", R.drawable.ic_launcher_foreground, 20f, null, null);
    Event event5 = new Event("event5", new String[]{"Game1", "Game2"}, "Event 5", R.drawable.ic_launcher_foreground, 40f, null, null);

    Event[] wishlist_events = {event1, event2, event3};
    Event[] upcoming_events = {event4, event5};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ListView listView = view.findViewById(R.id.list_events);
        Button btnWishlist = view.findViewById(R.id.btn_wishlist);
        Button btnUpcoming = view.findViewById(R.id.btn_upcoming);


        EventAdapter adapter = new EventAdapter(getContext(), new ArrayList<>());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Event clickedEvent = adapter.getItem(position);

            if (clickedEvent != null) {
                boolean isInWishlist = false;
                for (Event e : wishlist_events) {
                    if (e.getEventId().equals(clickedEvent.getEventId())) {
                        isInWishlist = true;
                        break;
                    }
                }


                FragmentEventDetail detailFragment = new FragmentEventDetail();
                Bundle args = new Bundle();
                args.putSerializable("event", clickedEvent);
                args.putBoolean("isInWishlist", isInWishlist);
                detailFragment.setArguments(args);


                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, detailFragment)
                        .addToBackStack("FragmentEventDetail")
                        .commit();
            }
        });



        btnWishlist.setOnClickListener(v -> {
            adapter.clear();
            adapter.addAll(List.of(wishlist_events));
            adapter.notifyDataSetChanged();
        });

        btnUpcoming.setOnClickListener(v -> {
            adapter.clear();
            adapter.addAll(List.of(upcoming_events));
            adapter.notifyDataSetChanged();
        });


        btnWishlist.performClick();

        return view;
    }
}

class EventAdapter extends ArrayAdapter<Event> {
    private Context context;
    private List<Event> events;

    public EventAdapter(@NonNull Context context, @NonNull List<Event> events) {
        super(context, 0, events);
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        }

        Event event = events.get(position);

        ImageView imageView = convertView.findViewById(R.id.event_image);
        TextView textView = convertView.findViewById(R.id.event_name);
        TextView voucherQuantity = convertView.findViewById(R.id.voucher_quantity);

        imageView.setImageResource(event.getEventImage());
        textView.setText(event.getEventName());
        voucherQuantity.setText(" - " + String.valueOf(event.getVoucherQuantity()) + " %");

        return convertView;
    }
}

