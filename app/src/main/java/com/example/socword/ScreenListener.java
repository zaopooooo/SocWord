package com.example.socword;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.util.Log;


public class ScreenListener {

    private Context context;
    private ScreenBroadcastReceiver mScreenReceiver;
    private ScreenStateListener mScreenStateListener;


    public ScreenListener(Context context){
        this.context = context;
        mScreenReceiver = new ScreenBroadcastReceiver();
    }

    public interface ScreenStateListener{
        void onScreenOn();
        void onScreenOff();
        void onUserPresent();
    }


    private void getScreenState(){
        PowerManager manager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (manager.isScreenOn()){
            if (mScreenStateListener != null){
                mScreenStateListener.onScreenOn();
            }
        }else {
            if (mScreenStateListener != null){
                mScreenStateListener.onScreenOff();
            }
        }
    }


    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;


        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)){
                mScreenStateListener.onScreenOn();
                Log.d("ScreenListener","屏幕亮了");
            }else if (Intent.ACTION_SCREEN_OFF.equals(action)){
                mScreenStateListener.onScreenOff();
                Log.d("ScreenListener","屏幕暗了");
            }else if (Intent.ACTION_USER_PRESENT.equals(action)){
                mScreenStateListener.onUserPresent();
            }
        }
    }

    public void begin(ScreenStateListener listener){
        mScreenStateListener = listener;
        registerListener();
        getScreenState();
    }


    public void registerListener(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        context.registerReceiver(mScreenReceiver,filter);
    }



    public void unregisterListener(){
        context.unregisterReceiver(mScreenReceiver);
    }

}
