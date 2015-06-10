package com.everett.li;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;

public class MediaPlayerTest implements Runnable{
    
    static final String TAG ="MediaPlayerTest";

    private Context mContext;
    private OnTestResult mListener;
    
    public interface OnTestResult{
        void onResult(String result);
    }
    
    public MediaPlayerTest(Context context, OnTestResult listener) {
        mContext = context;
        mListener = listener;
    }
    
    @Override
    public void run() {
        final MediaPlayer player = MediaPlayer.create(mContext, R.raw.test);
        player.setOnCompletionListener(new OnCompletionListener() {
            
            @Override
            public void onCompletion(MediaPlayer mp) {
                TSLog.endTs("play");
                mListener.onResult(TSLog.outPutAll().toString());
            }
        });
        try {
            TSLog.startTs("prepare");
            player.setOnPreparedListener(new OnPreparedListener() {
                
                @Override
                public void onPrepared(MediaPlayer mp) {
                    TSLog.endTs("prepare");
                    player.start();
                    TSLog.startTs("play");
                    
                }
            });
            player.prepareAsync();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } 
    }
    
}
