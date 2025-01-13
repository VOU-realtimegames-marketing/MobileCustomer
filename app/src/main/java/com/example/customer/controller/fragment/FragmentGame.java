package com.example.customer.controller.fragment;

import android.content.Context;
import android.os.Bundle;
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

import com.example.customer.R;
import com.example.customer.data.Game;

import java.util.ArrayList;
import java.util.List;

public class FragmentGame extends Fragment {
    Game game1 = new Game("event1", "game1", "quiz", "GameQuiz1", R.drawable.ic_launcher_foreground, null, null);
    Game game2 = new Game("event1", "game2", "shake", "GameShake1", R.drawable.ic_launcher_foreground, null, null);
    Game game3 = new Game("event2", "game3", "quiz", "GameQuiz2", R.drawable.ic_launcher_foreground, null, null);
    Game game4 = new Game("event3", "game4", "shake", "GameShake2", R.drawable.ic_launcher_foreground, null, null);
    Game game5 = new Game("event3", "game5", "quiz", "GameQuiz3", R.drawable.ic_launcher_foreground, null, null);

    Game[] about_to_start_games = {game1, game2, game3};
    Game[] upcoming_games = {game4, game5};

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
            adapter.addAll(List.of(about_to_start_games));
            adapter.notifyDataSetChanged();
        });

        btnUpcominggame.setOnClickListener(v -> {
            adapter.clear();
            adapter.addAll(List.of(upcoming_games));
            adapter.notifyDataSetChanged();
        });


        btnAbouttoStartGame.performClick();

        return view;
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
