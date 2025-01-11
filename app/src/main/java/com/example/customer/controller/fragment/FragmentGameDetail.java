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
import com.example.customer.data.Game;


public class FragmentGameDetail extends Fragment {

    private Game game;
    private boolean isAboutoStartGame;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            game = (Game) getArguments().getSerializable("game");
            isAboutoStartGame = getArguments().getBoolean("isAboutoStartGame", false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_detail, container, false);

        ImageView gameImage = view.findViewById(R.id.game_detail_image);
        TextView gameName = view.findViewById(R.id.game_detail_name);
        TextView gameType = view.findViewById(R.id.game_detail_type);
        TextView gameStartTime = view.findViewById(R.id.game_detail_start_time);
        TextView gameEndTime = view.findViewById(R.id.game_detail_end_time);
        Button joinGameButton = view.findViewById(R.id.btn_join);

        if (game != null) {
            gameImage.setImageResource(game.getGameImage());
            gameName.setText(game.getGameName());
            gameType.setText("Type" + game.getType());
            gameStartTime.setText("Start: " + game.getStartTime());
            gameEndTime.setText("End: " + game.getEndTime());
        }

        if (isAboutoStartGame) {
            joinGameButton.setVisibility(View.VISIBLE);
            joinGameButton.setOnClickListener(v -> {

                joinGame(game);
            });
        } else {
            joinGameButton.setVisibility(View.GONE);
        }

        return view;
    }

    private void joinGame(Game game) {
        if (game.getType() == "quiz"){
            FragmentWaiting waitingFragment = new FragmentWaiting();
            Bundle args = new Bundle();
            args.putString("event_id", game.getEventId());
            args.putString("game_id", game.getGameId());
            waitingFragment.setArguments(args);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, waitingFragment)
                    .addToBackStack(null)
                    .commit();
        }
        else if (game.getType() == "shake"){
            FragmentShakeGame shakeGameFragment = new FragmentShakeGame();


        }
    }
}
