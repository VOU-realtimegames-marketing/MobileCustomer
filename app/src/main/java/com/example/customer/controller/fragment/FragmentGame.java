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
import com.example.customer.data.Game;
import com.example.customer.utils.AuthInterceptor;
import com.example.customer.utils.Utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import vou.proto.GatewayGrpc;
import vou.proto.RpcGetAllEvents;

public class FragmentGame extends Fragment {


    private List<Game> about_to_start_games = new ArrayList<>();
    private List<Game> upcoming_games = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String accessToken = Utils.getAccessToken(requireActivity());
        GetAllEvents getAllEvents = new GetAllEvents(accessToken);
        getAllEvents.execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        ListView listView = view.findViewById(R.id.list_games);
        Button btnAbouttoStartGame = view.findViewById(R.id.btn_start_game);
        Button btnUpcominggame = view.findViewById(R.id.btn_upcoming_game);


        GameAdapter adapter = new GameAdapter(getContext(), new ArrayList<>());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Game clickedGame = adapter.getItem(position);

            if (clickedGame != null) {

                FragmentGameDetail detailFragment = new FragmentGameDetail();
                Bundle args = new Bundle();
                args.putSerializable("game", clickedGame);
                detailFragment.setArguments(args);


                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, detailFragment)
                        .addToBackStack("FragmentGameDetail")
                        .commit();
            }
        });



        btnAbouttoStartGame.setOnClickListener(v -> {
            adapter.clear();
            adapter.addAll(about_to_start_games);
            adapter.notifyDataSetChanged();
        });

        btnUpcominggame.setOnClickListener(v -> {
            adapter.clear();
            adapter.addAll(upcoming_games);
            adapter.notifyDataSetChanged();
        });


        btnAbouttoStartGame.performClick();

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
                Log.e("Error in GetAllGames:", e.getMessage());
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
                List<Game> convertedGames = new ArrayList<>();
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
                    Game game = new Game(
                            event.getEventId(),
                            event.getGamesId(),
                            event.getEventName(),
                            R.drawable.ic_launcher_foreground,
                            event.getStartTime(),
                            event.getEndTime(),
                            event.getQuizNum()
                    );
                    convertedGames.add(game);
                }

                about_to_start_games.clear();
                about_to_start_games.addAll(convertedGames);

                FragmentGame.this.getActivity().runOnUiThread(() -> {
                    Button btnAbouttoStartGame = getView().findViewById(R.id.btn_start_game);
                    if (btnAbouttoStartGame != null) {
                        btnAbouttoStartGame.performClick();
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

class GameAdapter extends ArrayAdapter<Game> {
    private Context context;
    private List<Game> games;

    public GameAdapter(@NonNull Context context, @NonNull List<Game> games) {
        super(context, 0, games);
        this.context = context;
        this.games = games;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_game, parent, false);
        }

        Game game = games.get(position);

        ImageView imageView = convertView.findViewById(R.id.game_image);
        TextView name = convertView.findViewById(R.id.game_name);
        TextView startTime = convertView.findViewById(R.id.start_time);

        imageView.setImageResource(game.getGameImage());
        name.setText(game.getGameName());
        startTime.setText("");

        return convertView;
    }
}
