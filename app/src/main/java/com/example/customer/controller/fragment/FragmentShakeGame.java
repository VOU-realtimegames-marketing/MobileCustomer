package com.example.customer.controller.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.customer.Config.Config;
import com.example.customer.R;
import com.example.customer.data.Game;
import com.example.customer.data.Voucher;
import com.example.customer.sensors.ShakeDetector;
import com.example.customer.utils.AuthInterceptor;
import com.example.customer.utils.Utils;
import com.google.protobuf.Timestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import vou.proto.GatewayGrpc;
import vou.proto.RpcWinVoucher;


public class FragmentShakeGame extends Fragment {

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private static final int SHAKE_TIMEOUT = 3000; // 3 giây
    private Handler shakeHandler = new Handler();
    private Runnable shakeTimeoutRunnable;
    private int playTurn = 3;
    private ImageButton btnBack;
    private TextView tvPlayTurns;
    private Button btnShare;
    private Button btnAskFriends;
    private Game game;



    public FragmentShakeGame() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            game = (Game) getArguments().getSerializable("game");
        }

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                //Gọi logic
                if (shakeTimeoutRunnable != null) {
                    shakeHandler.removeCallbacks(shakeTimeoutRunnable);
                }

                shakeTimeoutRunnable = new Runnable() {
                    @Override
                    public void run() {
                        handleShakeComplete();
                    }
                };

                shakeHandler.postDelayed(shakeTimeoutRunnable, SHAKE_TIMEOUT);

            }
        });

    }

    private void handleShakeComplete() {
        //Log handleShakeComplete
        Log.d("Shake", "Shake complete");
        if(playTurn > 0){
            //Gọi logic
            playTurn -= 1;
            tvPlayTurns.setText("Lượt chơi: " + String.valueOf(playTurn));

            //Random from 0 to 9
            int random = (int) (Math.random() * 9);
            if(random >4){
                openReceiveVoucherDialog(true);
            }
            else{
                openReceiveVoucherDialog(false);
            }
        }
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
            tvVoucher.setText("Xử lý...");
            btnReceive.setText("Nhận");

            String accessToken = Utils.getAccessToken(requireActivity());
            String event_id_string = String.valueOf(game.getEventId());

            // Gọi WinVoucher với callback
            WinVoucher winVoucher = new WinVoucher(accessToken, voucher -> {
                if (voucher != null) {
                    tvVoucher.setText("Voucher " + voucher.getDiscount() + "%");
                } else {
                    tvVoucher.setText("Voucher lỗi.");
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shake_game, container, false);
        btnBack = view.findViewById(R.id.btnBack);
        tvPlayTurns = view.findViewById(R.id.tvShakeCount);
        btnShare = view.findViewById(R.id.btnShare);
        btnAskFriends = view.findViewById(R.id.btnAskFriends);

        btnBack.setOnClickListener(v -> {
            //Back
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        tvPlayTurns.setText("Lượt chơi: " + String.valueOf(playTurn));

        btnShare.setOnClickListener(v -> {
            //Share
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            String context = "Chơi game cùng tôi";
            sendIntent.putExtra(Intent.EXTRA_TEXT, context);
            startActivity(Intent.createChooser(sendIntent, "Chia sẻ"));

            playTurn += 1;
            tvPlayTurns.setText("Lượt chơi: " + String.valueOf(playTurn));
        });
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        // Connect when resume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Unconnected when pause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
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

    public interface WinVoucherCallback {
        void onWinVoucherCompleted(Voucher voucher);
    }

    public LocalDateTime convertTimestampToLocalDateTime(Timestamp timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

}