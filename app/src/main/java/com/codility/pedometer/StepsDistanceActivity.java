package com.codility.pedometer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;

import com.codility.pedometer.service.StepsDistanceService;
import com.codility.pedometer.utils.Constant;
import com.codility.pedometer.utils.SharedPref;

public class StepsDistanceActivity extends AppCompatActivity {

    IMyServiceConnectorInterface mService;
    private AppCompatTextView  textView;
    private String TEXT_NUM_STEPS = "Number of Steps =";
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IMyServiceConnectorInterface.Stub.asInterface(service);
            try {
                mService.registerActivityCallback(mCallback);
                mService.setForeground(false);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if(mService != null){
                mService = null;
            }
        }
    };

    IActivity.Stub mCallback = new IActivity.Stub() {
        @Override
        public void onUiUpdate(long steps, float distance) throws RemoteException {
            textView.setText( TEXT_NUM_STEPS+steps +" \nDistance ="+(distance/1000)+" km");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.tvSteps);
        connectToService();
        showCurrentValue();
    }

    private void showCurrentValue(){
        long steps = SharedPref.readLong(Constant.KEY_STEP_COUNT);
        float distance = SharedPref.readFloat(Constant.KEY_DISTANCE);
        textView.setText( TEXT_NUM_STEPS+ steps +" \nDistance ="+(distance / 1000)+" km");
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectToService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disconnectFromService();
    }

    private void connectToService() {
        Intent serviceIntent = new Intent(this, StepsDistanceService.class);
        startService(serviceIntent);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);

    }

    private void disconnectFromService() {
        if (mService != null) {
            try {
                mService.setForeground(true);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService(serviceConnection);
            mService = null;
        }
    }

}
