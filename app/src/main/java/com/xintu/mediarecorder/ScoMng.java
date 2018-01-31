package com.xintu.mediarecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by xintu on 2018/1/26.
 */

public class ScoMng {

    private ScoReceiver scoReceiver;
    private AudioManager mAudioManager;
    private static ScoMng scoMng = null;

    public static ScoMng getInstance() {
        if (scoMng == null) {
            scoMng = new ScoMng();
        }
        return scoMng;
    }

    public void init(Context mContext) {
        Log.d("scoTest", "sco init");
        mAudioManager = (AudioManager) mContext.getSystemService(mContext.AUDIO_SERVICE);

        IntentFilter filter = new IntentFilter(AudioManager
                .ACTION_SCO_AUDIO_STATE_UPDATED);
        scoReceiver = new ScoReceiver();
        mContext.registerReceiver(scoReceiver, filter);
    }

    public void connSco() {
        Log.d("scoTest", "start conn sco: " + mAudioManager
                .isBluetoothScoOn());
        if (!mAudioManager.isBluetoothScoOn()) {
            Log.d("scoTest", "start conn sco: 1");
            mAudioManager.startBluetoothSco();
            Log.d("scoTest", "start conn sco: 2");
            mAudioManager.setBluetoothScoOn(true);
            Log.d("scoTest", "start conn sco: 3");
            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            Log.d("scoTest", "start conn sco: 4");
        }
    }

    public void disconnSco() {
        Log.d("scoTest", "disconnSco");
        if (mAudioManager.isBluetoothScoOn()) {
//            // 如果连接了sco则断开连接
            mAudioManager.setBluetoothScoOn(false);
            mAudioManager.stopBluetoothSco();
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
        }
    }

    class ScoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(AudioManager
                    .EXTRA_SCO_AUDIO_STATE, -1);
            Log.d("scoTest", "onReceive.state=" + state);

            switch (state) {
                case -1:
                    //Bluetooth SCO device error
                    Log.d("scoTest", "-1 Bluetooth SCO device error");
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(MainActivity.this, "sco device " +
//                                    "error",Toast.LENGTH_SHORT).show();
//                        }
//                    });
                    break;
                case 0:
                    //Bluetooth SCO device disconnected
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    mAudioManager.startBluetoothSco();
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(MainActivity.this, "sco
// disconnected",Toast.LENGTH_SHORT).show();
//                        }
//                    });
                    Log.d("scoTest", "0 Bluetooth SCO device disconnected");
                    break;
                case 1:
                    //Bluetooth SCO device connected
//                    mAudioManager.setBluetoothScoOn(true);
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(MainActivity.this, "sco
// connected",Toast.LENGTH_SHORT).show();
//                        }
//                    });
                    Log.d("scoTest", "1 Bluetooth SCO device connected");
                    break;
                case 2:
                    //Bluetooth SCO device connecting
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(MainActivity.this, "sco
// connecting",Toast.LENGTH_SHORT).show();
//                        }
//                    });
                    Log.d("scoTest", "2 Bluetooth SCO device connecting");
                    break;
                default:
                    //Bluetooth SCO device unknown event;
                    Log.d("scoTest", state + " Bluetooth SCO device " +
                            "unknown event");
                    break;
            }
        }
    }

    private MediaRecorder mediaRecorder = null;
    // 以文件的形式保存
    private File recordFile = null;
    String voiceTempPath;

    public void startRecording() {
        if (recordFile != null || mediaRecorder != null) {
            return;
        }
        mediaRecorder = new MediaRecorder();
        // 判断，若当前文件已存在，则删除

        voiceTempPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/lx_" + System.currentTimeMillis() + ".mp3";
        recordFile = new File(voiceTempPath);
        if (!recordFile.exists()) {
            try {
                recordFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediaRecorder.setOutputFile(recordFile.getAbsolutePath());

        try {
            // 准备好开始录音
            mediaRecorder.prepare();

            mediaRecorder.start();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void stopRecording() {
        if (recordFile != null && mediaRecorder != null) {
            try {
                mediaRecorder.setOnErrorListener(null);
                mediaRecorder.setOnInfoListener(null);
                mediaRecorder.setPreviewDisplay(null);
                mediaRecorder.stop();
            } catch (IllegalStateException e) {
                Log.w("Yixia", "stopRecord", e);
            } catch (RuntimeException e) {
                Log.w("Yixia", "stopRecord", e);
            } catch (Exception e) {
                Log.w("Yixia", "stopRecord", e);
            }
            mediaRecorder.release();
            recordFile = null;
            mediaRecorder = null;
        }
    }

}
