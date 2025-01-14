package com.example.customer.controller.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.customer.Config.Config;
import com.example.customer.R;
import com.example.customer.data.Answer;
import com.example.customer.data.Game;
import com.example.customer.data.Question;
import com.example.customer.utils.Utils;


import java.util.Locale;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import vou.proto.GameServiceGrpc;
import vou.proto.RpcAnswerQuestion;
import vou.proto.RpcGetQuestion;


public class FragmentQuizGame extends Fragment {
    private TextView questionText, timerText, resultText;
    private Button option1, option2, option3, option4;
    private Game game;
    private int correctAnswers = 0;
    private int questionNumber = 0;
    private int currentQuestionIndex = 0;
    private Question question;
    private Answer answer;
    private Boolean isWin = false;
    private CountDownTimer questionTimer, resultTimer;
    private boolean isAnswerSelected = false;
    private boolean isCorrect = false;
    private Button selectedOption = null;
    private TextToSpeech textToSpeech;

    private String username;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            game = (Game) getArguments().getSerializable("game");
        }
        questionNumber = (int) game.getQuizNum();
        username = Utils.getUserName((Activity) requireContext());

        textToSpeech = new TextToSpeech(requireContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
            }
        });
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

        return view;
    }

    private void loadQuestion() {
        new GetQuestion().execute(String.valueOf(currentQuestionIndex + 1), String.valueOf(game.getEventId()));

    }

    private void showQuestion() {
        if (currentQuestionIndex >= questionNumber) {

            FragmentEnd endFragment = new FragmentEnd();
            Bundle bundle = new Bundle();
            bundle.putInt("correctAnswers", correctAnswers);
            bundle.putInt("totalQuestions", questionNumber);
            bundle.putLong("event_id", game.getEventId());
            bundle.putBoolean("isWin", isWin);
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

        String questionAndOptions = question.getQuestion() + ". Option 1: " + question.getOption1() +
                ". Option 2: " + question.getOption2() + ". Option 3: " + question.getOption3() +
                ". Option 4: " + question.getOption4();

        if (textToSpeech != null) {
            textToSpeech.speak(questionAndOptions, TextToSpeech.QUEUE_FLUSH, null, null);
        }

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
                    showResult(answer.getIsCorrect());
                }
            }
        };
        questionTimer.start();
    }

    private void checkAnswer(Button selectedButton) {
        if (isAnswerSelected) return;
        isAnswerSelected = true;
        new AnswerQuestion().execute(username);
        isWin = answer.getIsWin();
        isCorrect = answer.getIsCorrect();
        selectedButton.setBackgroundResource(R.drawable.option_background_pressed);
        selectedOption = selectedButton;
        if (isCorrect) {
            correctAnswers++;
        }
    }

    private void showResult(boolean isCorrect) {
        resultText.setVisibility(View.VISIBLE);
        resultText.setText(isCorrect ? "Correct!" : "Wrong!");

        if (selectedOption != null) {
            selectedOption.setBackgroundResource(R.drawable.option_background_wrong);
        }

        if (option1.getText().toString().equals(answer.getAnswer())) {
            option1.setBackgroundResource(R.drawable.option_background_correct);
        }
        if (option2.getText().toString().equals(answer.getAnswer())) {
            option2.setBackgroundResource(R.drawable.option_background_correct);
        }
        if (option3.getText().toString().equals(answer.getAnswer())) {
            option3.setBackgroundResource(R.drawable.option_background_correct);
        }
        if (option4.getText().toString().equals(answer.getAnswer())) {
            option4.setBackgroundResource(R.drawable.option_background_correct);
        }

        String correctOptions = "The correct answer is " + answer.getAnswer();
        if (textToSpeech != null) {
            textToSpeech.speak(correctOptions, TextToSpeech.QUEUE_FLUSH, null, null);
        }

        resultTimer = new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                currentQuestionIndex++;
                selectedOption = null;
                resetOptionColor();
                showQuestion();
            }
        };
        resultTimer.start();
    }
    private void resetOptionColor() {
        option1.setBackgroundResource(R.drawable.option_background);
        option2.setBackgroundResource(R.drawable.option_background);
        option3.setBackgroundResource(R.drawable.option_background);
        option4.setBackgroundResource(R.drawable.option_background);
    }


    private class GetQuestion extends AsyncTask<String, Void, Question> {
        @Override
        protected Question doInBackground(String... params) {
            int questionNum = Integer.parseInt(params[0]);
            long eventId = Long.parseLong(params[1]);
            ManagedChannel channel = null;

            try {
                channel = ManagedChannelBuilder.forAddress(Config.ip, Config.game_port)
                        .usePlaintext()
                        .build();

                GameServiceGrpc.GameServiceStub stub = GameServiceGrpc.newStub(channel);

                RpcGetQuestion.GetQuestionRequest request = RpcGetQuestion.GetQuestionRequest.newBuilder()
                        .setQuestionNum(questionNum)
                        .setEventId(eventId)
                        .build();

                final Question[] resultQuestion = new Question[1];
                StreamObserver<RpcGetQuestion.GetQuestionResponse> responseObserver = new StreamObserver<>() {
                    @Override
                    public void onNext(RpcGetQuestion.GetQuestionResponse response) {

                        resultQuestion[0] = new Question(
                                response.getQuestion(),
                                response.getOptionsList().get(0),
                                response.getOptionsList().get(1),
                                response.getOptionsList().get(2),
                                response.getOptionsList().get(3)
                        );
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e("GetQuestion", "Error: " + t.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        Log.d("GetQuestion", "Completed");
                    }
                };

                StreamObserver<RpcGetQuestion.GetQuestionRequest> requestObserver = stub.getQuestion(responseObserver);
                requestObserver.onNext(request);
                requestObserver.onCompleted();


                Thread.sleep(1000);
                return resultQuestion[0];

            } catch (Exception e) {
                Log.e("GetQuestion", "Error: " + e.getMessage());
            } finally {
                if (channel != null) {
                    channel.shutdown();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Question questionResponse) {
            super.onPostExecute(questionResponse);

            if (questionResponse != null) {
                question = questionResponse;
                showQuestion();
            } else {
                Log.e("GetQuestion", "No question received");
            }
        }
    }

    private class AnswerQuestion extends AsyncTask<String, Void, Answer> {
        @Override
        protected Answer doInBackground(String... params) {
            String username = params[0];

            ManagedChannel channel = null;
            try {
                channel = ManagedChannelBuilder.forAddress(Config.ip, Config.game_port)
                        .usePlaintext()
                        .build();

                GameServiceGrpc.GameServiceStub stub = GameServiceGrpc.newStub(channel);

                RpcAnswerQuestion.AnswerQuestionRequest request = RpcAnswerQuestion.AnswerQuestionRequest.newBuilder()
                        .setUsername(username)
                        .build();

                StreamObserver<RpcAnswerQuestion.AnswerQuestionResponse> responseObserver = new StreamObserver<>() {
                    @Override
                    public void onNext(RpcAnswerQuestion.AnswerQuestionResponse response) {
                        answer = new Answer(
                                response.getIsCorrect(),
                                response.getIsWinner(),
                                response.getCorrectAnswer()
                        );
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e("AnswerQuestion", "Error: " + t.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        Log.d("AnswerQuestion", "Completed");
                    }
                };
            }
            catch (Exception e) {
                Log.e("AnswerQuestion", "Error: " + e.getMessage());
            }
            finally {
                if (channel != null) {
                    channel.shutdown();
                }
            }
            return null;
        }
    }


    @Override
    public void onDestroy() {
        if (questionTimer != null) questionTimer.cancel();
        if (resultTimer != null) resultTimer.cancel();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
