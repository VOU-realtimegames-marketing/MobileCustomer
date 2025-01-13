package com.example.customer.controller.fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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


        Bundle arguments = getArguments();
        int correctAnswers = arguments != null ? arguments.getInt("correctAnswers", 0) : 0;
        int totalQuestions = arguments != null ? arguments.getInt("totalQuestions", 0) : 0;


        resultText.setText(String.format("You answered %d out of %d questions correctly!", correctAnswers, totalQuestions));


        if (correctAnswers == totalQuestions) {
            openReceiveVoucherDialog(true);
        } else {
            openReceiveVoucherDialog(false);
        }

        exitButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack("FragmentGameDetail", 0);
        });

        return view;
    }

    private void openReceiveVoucherDialog(boolean isWin) {
        //Gọi logic
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_receive_voucher);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView tvMsg = dialog.findViewById(R.id.tvMsg);
        TextView tvVoucher = dialog.findViewById(R.id.tvVoucher);
        Button btnReceive = dialog.findViewById(R.id.btnReceive);

        if(isWin){
            tvMsg.setText("Chúc mừng bạn đã trúng thưởng");
            tvVoucher.setText("Voucher 100.000đ");
            btnReceive.setText("Nhận");
        }
        else{
            tvMsg.setText("Rất tiếc, bạn chưa trúng thưởng");
            tvVoucher.setText("");
            btnReceive.setText("Thử lại");
        }

        btnReceive.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }
}
