package com.example.customer.controller.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.customer.R;
import com.example.customer.data.Event;
import com.example.customer.data.Game;
import com.example.customer.data.Question;


public class FragmentQuizGame extends Fragment {
    private TextView questionText, timerText, resultText;
    private Button option1, option2, option3, option4;
    private Game game;
    private int correctAnswers = 0;
    private int questionNumber = 1;
    private int currentQuestionIndex = 0;
    private Question question;
    private CountDownTimer questionTimer, resultTimer;
    private boolean isAnswerSelected = false;
    private boolean isCorrect = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            game = (Game) getArguments().getSerializable("game");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_game, container, false);


        questionText = view.findViewById(R.id.questionText);
        timerText = view.findViewById(R.id.timerText);
        resultText = view.findViewById(R.id.resultText);
        option1 = view.findViewById(R.id.option1);
        option2 = view.findViewById(R.id.option2);
        option3 = view.findViewById(R.id.option3);
        option4 = view.findViewById(R.id.option4);

        View.OnClickListener optionClickListener = v -> checkAnswer((Button) v);
        option1.setOnClickListener(optionClickListener);
        option2.setOnClickListener(optionClickListener);
        option3.setOnClickListener(optionClickListener);
        option4.setOnClickListener(optionClickListener);

        loadQuestion();
        showQuestion();

        return view;
    }

    private void loadQuestion() {
        question = new Question(game.getEventId(), game.getGameId(), questionNumber, currentQuestionIndex,"What is the capital of France?", "Paris", "London", "Rome", "Berlin");

    }

    private void showQuestion() {
        if (currentQuestionIndex >= question.getNumber()) {

            FragmentEnd endFragment = new FragmentEnd();
            Bundle bundle = new Bundle();
            bundle.putInt("correctAnswers", correctAnswers);
            bundle.putInt("totalQuestions", questionNumber);
            endFragment.setArguments(bundle);


            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, endFragment)
                    .addToBackStack("FragmentEnd")
                    .commit();
            return;
        }
        isAnswerSelected = false;
        isCorrect = false;


        questionText.setText(question.getQuestion());
        option1.setText(question.getOption1());
        option2.setText(question.getOption2());
        option3.setText(question.getOption3());
        option4.setText(question.getOption4());
        resultText.setVisibility(View.GONE);

        questionTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("Time: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                if (!isAnswerSelected) {
                    showResult(false);
                } else {
                    showResult(isCorrect);
                }
            }
        };
        questionTimer.start();
    }

    private void checkAnswer(Button selectedOption) {
        if (isAnswerSelected) return;
        isAnswerSelected = true;
        isCorrect = selectedOption.getText().toString().equals(question.getAnswer());
        if (isCorrect) {
            correctAnswers++;
        }
    }

    private void showResult(boolean isCorrect) {
        resultText.setVisibility(View.VISIBLE);
        resultText.setText(isCorrect ? "Correct!" : "Wrong!");
        resultTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                currentQuestionIndex++;
                showQuestion();
            }
        };
        resultTimer.start();
    }

    @Override
    public void onDestroy() {
        if (questionTimer != null) questionTimer.cancel();
        if (resultTimer != null) resultTimer.cancel();
        super.onDestroy();
    }
}
