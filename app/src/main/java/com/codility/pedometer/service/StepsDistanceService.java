package com.codility.pedometer.service;

/*
 *  ****************************************************************************
 *  * Created by : Md. Azizul Islam on 3/21/2018 at 6:00 PM.
 *  * Email : azizul@w3engineers.com
 *  *
 *  * Last edited by : Md. Azizul Islam on 3/21/2018.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.codility.pedometer.IActivity;
import com.codility.pedometer.IMyServiceConnectorInterface;
import com.codility.pedometer.R;
import com.codility.pedometer.listener.StepListener;
import com.codility.pedometer.utils.StepDetector;

public class StepsDistanceService extends Service implements SensorEventListener, StepListener {

    public static final String START_FOREGROUND_ACTION = "io.left.meshim.action.startforeground";
    public static final String STOP_FOREGROUND_ACTION = "io.left.meshim.action.stopforeground";
    public static final int FOREGROUND_SERVICE_ID = 101;

    private boolean mIsBound = false;
    private boolean mIsForeground = false;

    private Notification mServiceNotification;

    private final String TAG = StepsDistanceService.class.getName();

    private StepDetector simpleStepDetector = null;
    private SensorManager sensorManager = null;
    private String  TEXT_NUM_STEPS = "Number of Steps =";

    private IActivity iActivity;
    @Override
    public void onCreate() {
        super.onCreate();
        Intent stopForegroundIntent = new Intent(this, StepsDistanceService.class);
        stopForegroundIntent.setAction(STOP_FOREGROUND_ACTION);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, stopForegroundIntent, 0);

        NotificationCompat.Builder builder;
        String channelId = "";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            channelId = getChannelId("setpdistance", "Step_distance");
        }
        builder = new NotificationCompat.Builder(this, channelId);

        mServiceNotification = builder.setAutoCancel(false)
                .setTicker("Step")
                .setContentTitle("Steps is Running")
                .setContentText("Tap to go offline.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setNumber(100)
                .build();


        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getChannelId(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        mIsBound = true;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind");
        mIsBound = false;
        return false;
    }

    IMyServiceConnectorInterface.Stub mBinder = new IMyServiceConnectorInterface.Stub() {
        @Override
        public void setForeground(boolean value) throws RemoteException {
            if (value) {
                startInForeground();
                mIsForeground = true;
            } else {
                stopForeground(true);
                Log.i(TAG, "stop startInForeground");
                mIsForeground = false;
            }
        }

        @Override
        public void registerActivityCallback(IActivity callback) throws RemoteException {
            iActivity = callback;
        }
    };

    private void startInForeground() {
        Log.i(TAG, "startInForeground");
        startForeground(FOREGROUND_SERVICE_ID, mServiceNotification);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        sensorManager.unregisterListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = null;
        if (intent != null) {
            action = intent.getAction();
        }
        if (action != null && action.equals(STOP_FOREGROUND_ACTION)) {
            if (mIsBound) {
                stopForeground(true);
            } else {
                stopSelf();
            }
        }
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccelerometer(event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onStepAndDistance(long steps, float distance) {
        try {
            iActivity.onUiUpdate(steps, distance);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
