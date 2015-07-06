package github.everett.li;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class MediaPlayerTestTask extends FutureTask<List<Entry<String, Period>>> {

    static final String TAG = "MediaPlayerTest";

    public MediaPlayerTestTask(Context context) {
        super(new PlayerTestCallable(context));
    }

    private static class PlayerTestCallable implements Callable<List<Entry<String, Period>>> {

        Context mContext;

        public PlayerTestCallable(Context context) {
            mContext = context;
        }

        @Override
        public List<Entry<String, Period>> call() throws Exception {
            final MediaPlayer player = MediaPlayer.create(mContext, R.raw.test);
            player.setOnCompletionListener(new OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    TSLog.endTs("player play");
                    synchronized (player) {
                        player.notify();
                    }
                    mp.release();
                }
            });
            try {
                TSLog.startTs("player play");
                player.start();
                synchronized (player) {
                    try {
                        player.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                List<Entry<String, Period>> retList = TSLog.getAllTs();
                TSLog.reset();
                return retList;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            return null;
        }

    };

}
