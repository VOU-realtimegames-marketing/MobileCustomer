package com.example.customer.controller.fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.customer.R;
import com.example.customer.sensors.ShakeDetector;


public class FragmentShakeGame extends Fragment {

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private static final int SHAKE_TIMEOUT = 3000; // 1 giây
    private Handler shakeHandler = new Handler();
    private Runnable shakeTimeoutRunnable;
    private int playTurn;



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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shake_game, container, false);
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