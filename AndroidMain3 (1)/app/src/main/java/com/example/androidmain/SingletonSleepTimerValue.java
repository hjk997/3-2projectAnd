package com.example.androidmain;

public class SingletonSleepTimerValue {
    //싱글턴 패턴으로 형식을 변환.
    private static final SingletonSleepTimerValue instance = new SingletonSleepTimerValue();

    private int sleepCounter =0;
    private int shakeCounter=0;
    private String gapSleepTime ="";
    private float maxLuxValue = 0;

    private String hour;
    private String min;

    private long startSleepTIme;
    private long finishSleepTime;

    //클래스의 생성자를 private로 하여 바깥에서 사용할 수 없도록 합니다.
    private SingletonSleepTimerValue(){

    }

    //클래스를 사용하기 위해 클래스 정보를 가지고 오는 부분입니다.
    public static SingletonSleepTimerValue getInstance() {
        return instance;
    }

    public long getFinishSleepTime() {
        return finishSleepTime;
    }

    public long getStartSleepTIme() {
        return startSleepTIme;
    }

    public String getHour() {
        return hour;
    }

    public String getMin() {
        return min;
    }

    public float getMaxLuxValue() {
        return maxLuxValue;
    }

    public int getShakeCounter() {
        return shakeCounter;
    }

    public int getSleepCounter() {
        return sleepCounter;
    }

    public String getGapSleepTime() {
        return gapSleepTime;
    }

    public void setGapSleepTime(String gapSleepTime) {
        this.gapSleepTime = gapSleepTime;
    }

    public void setMaxLuxValue(float maxLuxValue) {
        this.maxLuxValue = maxLuxValue;
    }

    public void setShakeCounter(int shakeCounter) {
        this.shakeCounter = shakeCounter;
    }

    public void setSleepCounter(int sleepCounter) {
        this.sleepCounter = sleepCounter;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public void setFinishSleepTime(long finishSleepTime) {
        this.finishSleepTime = finishSleepTime;
    }

    public void setStartSleepTIme(long startSleepTIme) {
        this.startSleepTIme = startSleepTIme;
    }
}
