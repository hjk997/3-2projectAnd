package com.example.androidmain;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

public class SleepTimer extends Service implements SensorEventListener {
    private int a = 0;

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Sensor accelerometerSensor;

    private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;
    private float x, y, z;

    private static final int SHAKE_THRESHOLD = 800;
    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;

    //흔들림 횟수 초기화
    private int mShakeCounter;          //흔들림 횟수 저장할 변수
    private float mMaxLuxValue;            //조도센서 최대 lux값 저장

    private long myStartSleepTime = 0;       //시간재기 시작하는 시간
    private long myGapSleepTime = 0;         //Start와 Stop사이의 시간
    private long myStopSleepTime = 0;        //시간재기 끝내는 시간

    private int sleepCounter;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate()
    {
        super.onCreate();
        Log.i("서비스 테스트 : ", "onCreate() 동작");

        sleepCounter = 0;

        //조도 센서를 사용하기 위해 등록함. 센서가 없으면 메시지를 Toast로 띄움.
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor == null) {
            Toast.makeText(this, "No Light Sensor Found!", Toast.LENGTH_SHORT).show();
        }

        //흔들림 감지를 위한 센서를 사용하기 위해 등록함. 센서가 없으면 메시지를 Toast로 띄움.
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometerSensor == null) {
            Toast.makeText(this, "No Light Sensor Found!", Toast.LENGTH_SHORT).show();
        }

        //조도 센서를 사용하기 위해 등록함. 센서가 없으면 메시지를 Toast로 띄움.
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor == null) {
            Toast.makeText(this, "No Light Sensor Found!", Toast.LENGTH_SHORT).show();
        }

        //흔들림 감지를 위한 센서를 사용하기 위해 등록함. 센서가 없으면 메시지를 Toast로 띄움.
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometerSensor == null) {
            Toast.makeText(this, "No Light Sensor Found!", Toast.LENGTH_SHORT).show();
        }

        mShakeCounter = 0;

        TimerTask SleepingTimer = new TimerTask() {
            @Override
            public void run() {
                if ((mShakeCounter < 10) && mMaxLuxValue < 10) {   // 잔다고 판정되었다.
                    if (sleepCounter == 0) {
                        myStartSleepTime = System.currentTimeMillis();
                        SingletonSleepTimerValue.getInstance().setStartSleepTIme( myStartSleepTime );
                    } //처음 으로 잔다고 판정되었다. -> 시간 재기 시작할 시간 초기화
                    else {
                        mShakeCounter = 0;
                        mMaxLuxValue = 0;
                        myStopSleepTime = System.currentTimeMillis();
                        SingletonSleepTimerValue.getInstance().setFinishSleepTime(myStopSleepTime);
                    }   //단위 시간당 잔다고 판단되어 있다면 초기화
                    sleepCounter = sleepCounter + 1;
                }//end if 잔다고 판정되었다.
                else {
                    sleepCounter = 0;       //안잤으니 초기화
                    mShakeCounter = 0;
                    mMaxLuxValue = 0;
                }//end else 잔다고 판정이 되지 않았다.

                SingletonSleepTimerValue.getInstance().setSleepCounter(sleepCounter);
            }   // end run
        };      //end TimerTask

        Timer timer = new Timer();
        timer.schedule(SleepingTimer, 0, 1000 * 5 );  //60 * 15
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals("Action1")) {
            Log.i("onStartCommand-Action1", "---서비스 스타트--- ");

            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setAction("Action2");

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.ic_launcher_round);

            Notification notification = new NotificationCompat.Builder(this, "channel1")
                    .setContentTitle("background machine")
                    .setTicker("측정중... ")
                    .setContentText("백그라운드 동작중")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true).build();

            startForeground(111, notification);

            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

            Thread bt = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.i("background-counter", String.valueOf(++a));
                    }
                }
            });
            bt.setName("백그라운드스레드");
            bt.start();

        } else if (intent.getAction().equals("Action2")) {
        }

        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//흔들림을 감지한 센서에서 변화를 감지했다면.
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            // 350 -> 3.5초 길어질수록 카운트 세기가 어려워짐.
            if (gabOfTime > 100) {
                lastTime = currentTime;
                x = event.values[SensorManager.DATA_X];
                y = event.values[SensorManager.DATA_Y];
                z = event.values[SensorManager.DATA_Z];

                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    // Shake 감지할 때마다 업데이트 될 부분.
                    mShakeCounter++;
                }

                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
                lastZ = event.values[DATA_Z];
            }
        }

        //조도 센서에서 빛 변화 감지하면 인식
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            if (event.values[0] > mMaxLuxValue) {
                mMaxLuxValue = event.values[0];
            }
        }

        if (((mShakeCounter > 10) || (mMaxLuxValue > 10)) && (sleepCounter > 1)) {      // sleepcounter 16 : 4시간 이상 잔다. 기준시간만큼 자고 일어났다
            myGapSleepTime = myStopSleepTime - myStartSleepTime;
            String gapTime = String.format("%02d 시간 %02d 분 %02d 초", (myGapSleepTime / (1000*60*60))%60 , (myGapSleepTime / (1000 * 60))%60, (myGapSleepTime / 1000)%60 );
            SingletonSleepTimerValue.getInstance().setGapSleepTime(gapTime);
            Log.i("gaptIme", gapTime);

        }

        SingletonSleepTimerValue.getInstance().setMaxLuxValue(mMaxLuxValue);
        SingletonSleepTimerValue.getInstance().setShakeCounter(mShakeCounter);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}

