package com.example.customer.controller.fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.customer.Config.Config;
import com.example.customer.R;
import com.example.customer.data.Event;
import com.example.customer.utils.AuthInterceptor;
import com.example.customer.utils.Utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import vou.proto.GatewayGrpc;
import vou.proto.RpcGetAllEvents;


public class FragmentHome extends Fragment {

    private List<Event> wishlist_events = new ArrayList<>();
    private List<Event> upcoming_events = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Event event = (Event) getArguments().getSerializable("event");
            boolean isInWishlist = getArguments().getBoolean("isInWishlist", false);
            if (isInWishlist) {
                wishlist_events.add(event);
            }
        }

        String accessToken = Utils.getAccessToken(requireActivity());
        GetAllEvents getAllEvents = new GetAllEvents(accessToken);
        getAllEvents.execute();
    }

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
                    if (e.getEventId() == clickedEvent.getEventId()) {
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
            adapter.addAll(wishlist_events);
            adapter.notifyDataSetChanged();
        });

        btnUpcoming.setOnClickListener(v -> {
            adapter.clear();
            adapter.addAll(upcoming_events);
            adapter.notifyDataSetChanged();
        });


        btnWishlist.performClick();

        return view;
    }



    private class GetAllEvents extends AsyncTask<Void, Void, RpcGetAllEvents.GetAllEventsResponse> {
        private String accessToken;

        public GetAllEvents(String accessToken) {
            this.accessToken = accessToken;
        }

        @Override
        protected RpcGetAllEvents.GetAllEventsResponse doInBackground(Void... voids) {
            ManagedChannel channel = null;
            try {
                channel = ManagedChannelBuilder.forAddress(Config.ip, Config.port)
                        .usePlaintext()
                        .intercept(new AuthInterceptor(accessToken))
                        .build();

                GatewayGrpc.GatewayBlockingStub blockingStub = GatewayGrpc.newBlockingStub(channel);
                RpcGetAllEvents.GetAllEventsRequest request = RpcGetAllEvents.GetAllEventsRequest.newBuilder()
                        .build();
                RpcGetAllEvents.GetAllEventsResponse response = blockingStub.getAllEvents(request);
                return response;

            } catch (Exception e) {
                Log.e("Error in GetAllEvents:", e.getMessage());
            } finally {
                if (channel != null) {
                    channel.shutdown();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(RpcGetAllEvents.GetAllEventsResponse response) {
            super.onPostExecute(response);

            if (response != null) {
                List<Event> convertedEvents = new ArrayList<>();
                for (vou.proto.EventOuterClass.Event grpcEvent : response.getEventsList()) {
                    Event event = new Event(
                            grpcEvent.getId(),
                            grpcEvent.getGameId(),
                            grpcEvent.getStoreId(),
                            grpcEvent.getName(),
                            R.drawable.ic_launcher_foreground,
                            grpcEvent.getVoucherQuantity(),
                            convertToLocalDateTime(grpcEvent.getStartTime().getSeconds()),
                            convertToLocalDateTime(grpcEvent.getEndTime().getSeconds()),
                            grpcEvent.getGameType(),
                            grpcEvent.getStore(),
                            grpcEvent.getQuizNum()
                    );
                    convertedEvents.add(event);
                }

                upcoming_events.clear();
                upcoming_events.addAll(convertedEvents);

                FragmentHome.this.getActivity().runOnUiThread(() -> {
                    Button btnUpcoming = getView().findViewById(R.id.btn_upcoming);
                    if (btnUpcoming != null) {
                        btnUpcoming.performClick();
                    }
                });
            }
        }

        private LocalDateTime convertToLocalDateTime(long seconds) {
            return Instant.ofEpochSecond(seconds)
                    .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                    .toLocalDateTime();
        }
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
        TextView name = convertView.findViewById(R.id.event_name);
        TextView voucherQuantity = convertView.findViewById(R.id.voucher_quantity);

        imageView.setImageResource(event.getEventImage());
        name.setText(event.getEventName());
        voucherQuantity.setText("Quantity: " + String.valueOf(event.getVoucherQuantity()));

        return convertView;
    }
}

