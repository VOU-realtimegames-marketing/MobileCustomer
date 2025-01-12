package com.example.customer.controller.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.customer.R;


public class FragmentEnd extends Fragment {
    private TextView resultText;
    private Button exitButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_end, container, false);

        resultText = view.findViewById(R.id.resultText);
        exitButton = view.findViewById(R.id.exitButton);

        // Nhận dữ liệu số câu đúng từ arguments
        Bundle arguments = getArguments();
        int correctAnswers = arguments != null ? arguments.getInt("correctAnswers", 0) : 0;
        int totalQuestions = arguments != null ? arguments.getInt("totalQuestions", 0) : 0;

        // Hiển thị kết quả
        resultText.setText(String.format("You answered %d out of %d questions correctly!", correctAnswers, totalQuestions));

        // Thêm sự kiện cho nút thoát
        exitButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack("FragmentGameDetail", 0);
        });

        return view;
    }
}
