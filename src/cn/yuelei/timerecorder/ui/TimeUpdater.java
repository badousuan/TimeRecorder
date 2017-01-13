package cn.yuelei.timerecorder.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.SystemClock;

public class TimeUpdater {
    private Context mContext;
    private int mIntervalMs;
    private Thread mThread;
    private boolean mShouldStop = false;
    private OnUpdateListener mUpdateListener; 
    interface OnUpdateListener{
        void onUpdate(String time);
    }
    public TimeUpdater(Context context,int intervalMs,OnUpdateListener lsr){
        mContext = context;
        mIntervalMs = intervalMs;
        mUpdateListener = lsr;
        mThread = new Thread(new Runnable() {
            int counter = 0;
            @Override
            public void run() {
                while (!mShouldStop) {
                    if (mUpdateListener != null) {
                        mUpdateListener.onUpdate(formatedSecond(counter));
                    }
                    counter++;
                    SystemClock.sleep(mIntervalMs);
                }
            }
        });
    }
    
    public void setInterVal(int ms){
        mIntervalMs = ms;
    }
    
    public int getInterVal(){
        return mIntervalMs;
    }
    
    public void start(){
        mThread.start();
    }
    
    public void stop(){
        mShouldStop = true;
        mThread = null;
    }
    
    public static String formatedSecond(int seconds) {
        String timeFormatStr = "00:00:00";
        int ss = seconds%60;
        int hh = seconds/3600;
        int mm = (seconds-hh*3600-ss)/60;
        if (hh >=100) {
            return String.format("%03d:%02d:%02d",hh,mm,ss);
        }else{
            return String.format("%02d:%02d:%02d",hh,mm,ss);
        }
        
    }
}
