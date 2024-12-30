package com.example.customer.controller.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.customer.R;
import com.example.customer.sensors.ShakeDetector;


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



    public FragmentShakeGame() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if(playTurn > 0){
            //Gọi logic
            playTurn -= 1;

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
        dialog.setContentView(R.layout.dialog_receive_voucher);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

//        Window window = dialog.getWindow();
//        if(window == null){
//            return;
//        }
//
//        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        window.setBackgroundDrawableResource(android.R.color.transparent);
//
//        WindowManager.LayoutParams windowAttributes = window.getAttributes();
//        windowAttributes.gravity = Gravity

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

        tvPlayTurns.setText("Lượt chơi" + String.valueOf(playTurn));

        btnShare.setOnClickListener(v -> {
            //Share
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            String context = "Chơi game cùng tôi";
            sendIntent.putExtra(Intent.EXTRA_TEXT, context);
            startActivity(Intent.createChooser(sendIntent, "Chia sẻ"));

            playTurn += 1;
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


}