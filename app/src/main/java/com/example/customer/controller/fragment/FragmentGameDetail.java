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


public class FragmentGameDetail extends Fragment {

    private Game game;

    private long startTimeMillis;
    private long endTimeMillis;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            game = (Game) getArguments().getSerializable("game");


            startTimeMillis = calculateQuizStartTime();
            endTimeMillis = calculateQuizEndTime();
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
            gameType.setText("Type: " + game.getType());
            gameStartTime.setText("Start: " + game.getStartTime());
            gameEndTime.setText("End: " + game.getEndTime());
        }


        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis >= startTimeMillis && currentTimeMillis <= endTimeMillis) {
            joinGameButton.setVisibility(View.VISIBLE);
            joinGameButton.setOnClickListener(v -> joinGame(game));
        } else {
            joinGameButton.setVisibility(View.GONE);
        }

        return view;
    }

    private long calculateQuizStartTime() {
        Calendar quizStartCalendar = Calendar.getInstance();
        quizStartCalendar.set(Calendar.HOUR_OF_DAY, 1);
        quizStartCalendar.set(Calendar.MINUTE, 35);
        quizStartCalendar.set(Calendar.SECOND, 0);
        return quizStartCalendar.getTimeInMillis();
    }

    private long calculateQuizEndTime() {
        Calendar quizEndCalendar = Calendar.getInstance();
        quizEndCalendar.set(Calendar.HOUR_OF_DAY, 23);
        quizEndCalendar.set(Calendar.MINUTE, 37);
        quizEndCalendar.set(Calendar.SECOND, 30);
        return quizEndCalendar.getTimeInMillis();
    }

    private void joinGame(Game game) {
        if ("quiz".equals(game.getType())) {
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
        } else if ("shake".equals(game.getType())) {
            FragmentShakeGame shakeGameFragment = new FragmentShakeGame();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, shakeGameFragment)
                    .commit();
        }
    }
}
