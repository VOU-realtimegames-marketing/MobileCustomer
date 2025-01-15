package com.example.customer.controller.fragment;

import android.icu.util.Calendar;
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

import java.time.LocalDateTime;
import java.time.ZoneId;


public class FragmentGameDetail extends Fragment {

    private Game game;

    private long startTimeMillis;
    private long endTimeMillis;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            game = (Game) getArguments().getSerializable("game");


            startTimeMillis = calculateQuizStartTime(game.getStartTime());
            endTimeMillis = calculateQuizEndTime(game.getEndTime());
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
            if (game.getGameId() == 1) {
                gameType.setText("Type: Quiz");
            }
            else {
                gameType.setText("Type: Shake");
            }

            gameStartTime.setText("Start: " + game.getStartTime());
            gameEndTime.setText("End: " + game.getEndTime());
        }


        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis <= endTimeMillis) {
            joinGameButton.setVisibility(View.VISIBLE);
            joinGameButton.setOnClickListener(v -> joinGame(game));
        } else {
            joinGameButton.setVisibility(View.GONE);
        }

        return view;
    }

    private long calculateQuizStartTime(LocalDateTime startTime) {
        return startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    private long calculateQuizEndTime(LocalDateTime endTime) {
        return endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    private void joinGame(Game game) {
        if (game.getGameId() == 1) {
            FragmentWaiting waitingFragment = new FragmentWaiting();

            Bundle args = new Bundle();
            args.putSerializable("game", game);
            args.putLong("start_time", startTimeMillis); // Gửi start time dưới dạng long
            waitingFragment.setArguments(args);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, waitingFragment)
                    .addToBackStack("FragmentWaiting")
                    .commit();
        } else if (game.getGameId() == 2) {
            FragmentShakeGame shakeGameFragment = new FragmentShakeGame();
            Bundle args = new Bundle();
            args.putSerializable("game", game);

            shakeGameFragment.setArguments(args);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, shakeGameFragment)
                    .commit();
        }
    }
}
