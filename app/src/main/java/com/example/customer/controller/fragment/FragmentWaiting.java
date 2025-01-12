package com.example.customer.controller.fragment;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.customer.R;
import com.example.customer.data.Game;

public class FragmentWaiting extends Fragment {
    private TextView countdownText;
    private long quizStartTime = 0;

    private CountDownTimer countDownTimer;


    private Game game;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            game = (Game) getArguments().getSerializable("game");
            quizStartTime = getArguments().getLong("start_time");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waiting, container, false);

        countdownText = view.findViewById(R.id.countdownText);
        startCountdown();

        return view;
    }

    private void startCountdown() {
        long currentTime = System.currentTimeMillis();
        long timeLeft = quizStartTime - currentTime;

        if (timeLeft <= 0) {
            navigateToQuiz();
        } else {
            countDownTimer = new CountDownTimer(timeLeft, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long hours = millisUntilFinished / (1000 * 60 * 60);
                    long minutes = (millisUntilFinished / (1000 * 60)) % 60;
                    long seconds = (millisUntilFinished / 1000) % 60;


                    countdownText.setText(String.format("Starting in: %02d:%02d:%02d", hours, minutes, seconds));
                }

                @Override
                public void onFinish() {
                    navigateToQuiz();
                }
            };
            countDownTimer.start();
        }
    }

    private void navigateToQuiz() {
        if (!isAdded()) {
            return;
        }

        FragmentQuizGame quizFragment = new FragmentQuizGame();
        Bundle args = new Bundle();
        args.putSerializable("game", game);
        quizFragment.setArguments(args);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, quizFragment)
                .addToBackStack("FragmentQuizGame")
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}

