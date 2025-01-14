package com.example.customer.controller.fragment;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.customer.Config.Config;
import com.example.customer.R;
import com.example.customer.data.Event;
import com.example.customer.data.Friend;
import com.example.customer.data.Voucher;
import com.example.customer.utils.AuthInterceptor;
import com.example.customer.utils.Utils;
import com.google.protobuf.Timestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import vou.proto.GatewayGrpc;
import vou.proto.RpcGetAllOtherUsers;
import vou.proto.RpcWinVoucher;


public class FragmentEnd extends Fragment {
    private TextView resultText;
    private Button exitButton;

    private Voucher voucher;
    private int correctAnswers;
    private int totalQuestions;
    private long event_id;
    private Boolean isWin;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            correctAnswers = getArguments().getInt("correctAnswers", 0);
            totalQuestions = getArguments().getInt("totalQuestions", 0);
            event_id = getArguments().getLong("event_id", 0);
            isWin = getArguments().getBoolean("isWin", false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_end, container, false);

        resultText = view.findViewById(R.id.resultText);
        exitButton = view.findViewById(R.id.exitButton);



        resultText.setText(String.format("You answered %d out of %d questions correctly!", correctAnswers, totalQuestions));


        if (isWin) {
            openReceiveVoucherDialog(true);
        } else {
            openReceiveVoucherDialog(false);
        }

        exitButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack("FragmentGameDetail", 0);
        });

        return view;
    }
    public interface WinVoucherCallback {
        void onWinVoucherCompleted(Voucher voucher);
    }

    private class WinVoucher extends AsyncTask<String, Void, RpcWinVoucher.WinVoucherResponse> {
        private String accessToken;
        private WinVoucherCallback callback;

        public WinVoucher(String accessToken, WinVoucherCallback callback) {
            this.accessToken = accessToken;
            this.callback = callback;
        }

        @Override
        protected RpcWinVoucher.WinVoucherResponse doInBackground(String... params) {
            String event_id_string = params[0];
            long event_id = Long.parseLong(event_id_string);

            ManagedChannel channel = null;
            try {
                channel = ManagedChannelBuilder.forAddress(Config.ip, Config.port)
                        .usePlaintext()
                        .intercept(new AuthInterceptor(accessToken))
                        .build();

                GatewayGrpc.GatewayBlockingStub blockingStub = GatewayGrpc.newBlockingStub(channel);
                RpcWinVoucher.WinVoucherRequest request = RpcWinVoucher.WinVoucherRequest.newBuilder()
                        .setEventId(event_id)
                        .build();

                return blockingStub.winVoucher(request);
            } catch (Exception e) {
                Log.e("Error in WinVoucher", e.getMessage());
            } finally {
                if (channel != null) {
                    channel.shutdown();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(RpcWinVoucher.WinVoucherResponse response) {
            super.onPostExecute(response);

            Voucher resultVoucher = null;
            if (response != null && response.hasVoucher()) {
                resultVoucher = new Voucher(
                        response.getVoucher().getId(),
                        response.getVoucher().getEventId(),
                        response.getVoucher().getQrCode(),
                        response.getVoucher().getType(),
                        response.getVoucher().getStatus(),
                        response.getVoucher().getDiscount(),
                        convertTimestampToLocalDateTime(response.getVoucher().getExpiresAt())
                );
            }

            if (callback != null) {
                callback.onWinVoucherCompleted(resultVoucher);
            }
        }
    }

    public LocalDateTime convertTimestampToLocalDateTime(Timestamp timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
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
            tvMsg.setText("Congratulations!");
            tvVoucher.setText("Processing your voucher...");
            btnReceive.setText("Accept");

            String accessToken = Utils.getAccessToken(requireActivity());
            String event_id_string = String.valueOf(event_id);

            // Gọi WinVoucher với callback
            WinVoucher winVoucher = new WinVoucher(accessToken, voucher -> {
                if (voucher != null) {
                    tvVoucher.setText("You received a Voucher: " + voucher.getDiscount() + " %");
                } else {
                    tvVoucher.setText("Failed to retrieve voucher.");
                }
            });

            winVoucher.execute(event_id_string);
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
