package github.everett.li;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

public class AudioRecorderTestTask extends FutureTask<List<Entry<String, Period>>> {

    static final String TAG = "AudioRecorderTestTask";

    public AudioRecorderTestTask() {
        super(new RecorderTestCallable());
    }

    private static class RecorderTestCallable implements Callable<List<Entry<String, Period>>> {

        @Override
        public List<Entry<String, Period>> call() throws Exception {
            List<Entry<String, Period>> retList = null;
            TSLog.startTs("recorder new");
            AudioRecord ar = new AudioRecord(AudioSource.MIC, mSampleRate, mAudioChannel,
                    mAudioFormat, getAudioRecorderBufferSize());
            TSLog.endTs("recorder new");
            if (ar.getState() == AudioRecord.STATE_INITIALIZED) {
                TSLog.startTs("recorder start");
                ar.startRecording();
                TSLog.endTs("recorder start");
                byte[] buffer = new byte[getReadBufferSize()];
                if (ar.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                    TSLog.startTs("recorder read 1s");
                    for (int i = 0; i < 20; i++) {
                        ar.read(buffer, 0, buffer.length);
                    }
                    TSLog.endTs("recorder read 1s");
                }
                TSLog.startTs("recorder stop");
                ar.stop();
                TSLog.endTs("recorder stop");
                TSLog.startTs("recorder restart");
                ar.startRecording();
                TSLog.endTs("recorder restart");
                TSLog.startTs("recorder restop");
                ar.stop();
                TSLog.endTs("recorder restop");
                TSLog.startTs("recorder release");
                ar.release();
                TSLog.endTs("recorder release");
                retList = TSLog.getAllTs();
            }
            TSLog.reset();
            return retList;
        }

        int mAudioChannel = AudioFormat.CHANNEL_IN_MONO; // mono channel
        int mSampleRate = 16000; // 16kHz sample rate
        int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int mInterval = 50; // 50ms in per to throw out audio data

        private int getAudioRecorderBufferSize() {
            int minBufferSize = AudioRecord.getMinBufferSize(mSampleRate, mAudioChannel,
                    mAudioFormat);
            int intervalBufferSize = getReadBufferSize();
            Log.d(TAG, "[minBufferSize}:" + minBufferSize + ",  [intervalBufferSize]:"
                    + intervalBufferSize);
            return intervalBufferSize * 2;
        }

        private int getReadBufferSize() {
            return mSampleRate * mAudioFormat
                    * ((mAudioChannel == AudioFormat.CHANNEL_IN_MONO) ? 1 : 2) * mInterval / 1000;
        }
    }

}
