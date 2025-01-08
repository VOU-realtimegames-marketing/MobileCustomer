package com.example.customer.controller.fragment;

import android.icu.util.Calendar;
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

public class FragmentWaiting extends Fragment {
    private TextView countdownText;
    private long quizStartTime = 0; // Thời gian bắt đầu quiz cố định (8:00 AM)

    String event_id;
    String game_id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event_id = getArguments().getString("event_id");
            game_id = getArguments().getString("game_id");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waiting, container, false);

        countdownText = view.findViewById(R.id.countdownText);

        // Thiết lập thời gian bắt đầu quiz cố định
        quizStartTime = calculateQuizStartTime();
        startCountdown();

        return view;
    }

    private long calculateQuizStartTime() {
        // Lấy thời gian hiện tại
        Calendar calendar = Calendar.getInstance();

        // Thiết lập thời gian cố định là 8:00 AM
        Calendar quizStartCalendar = Calendar.getInstance();
        quizStartCalendar.set(Calendar.HOUR_OF_DAY, 10);
        quizStartCalendar.set(Calendar.MINUTE, 5);
        quizStartCalendar.set(Calendar.SECOND, 0);


        return quizStartCalendar.getTimeInMillis();
    }

    private void startCountdown() {
        long currentTime = System.currentTimeMillis();
        long timeLeft = quizStartTime - currentTime;

        if (timeLeft <= 0) {
            navigateToQuiz();
        } else {
            new CountDownTimer(timeLeft, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // Tính toán thời gian còn lại
                    long hours = millisUntilFinished / (1000 * 60 * 60);
                    long minutes = (millisUntilFinished / (1000 * 60)) % 60;
                    long seconds = (millisUntilFinished / 1000) % 60;

                    // Hiển thị thời gian đếm ngược
                    countdownText.setText(String.format("Starting in: %02d:%02d:%02d", hours, minutes, seconds));
                }

                @Override
                public void onFinish() {
                    navigateToQuiz();
                }
            }.start();
        }
    }

    private void navigateToQuiz() {
        FragmentQuizGame quizFragment = new FragmentQuizGame();
        Bundle args = new Bundle();
        args.putString("event_id", event_id);
        args.putString("game_id", game_id);
        quizFragment.setArguments(args);


        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, quizFragment)
                .addToBackStack(null)
                .commit();
    }
}

