package com.xintu.mediarecorder;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaRecorderMng.MediaRecorderMngListener {
    private SurfaceView mSurfaceview;
    private Button mBtnStartStop;
    private TextView mTextView;
    private boolean mStartedFlg = false;//是否正在录像
    private boolean mStartedAudioFlg = false;//是否正在录像
    private Button mBtnStartStopAudio;
    private int text = 0;


    private SurfaceHolder mSurfaceHolder;
    private Handler handler = new Handler();//android.os  是一个移动设备，智能手机和平板电脑的操作系统

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            text++;
            mTextView.setText(text + "");
            handler.postDelayed(this, 1000);//休眠1秒
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_main);
        MediaRecorderMng.getInstance().setListener(this);

        intView();
        SurfaceHolder holder = mSurfaceview.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//缓冲区


        ScoMng.getInstance().init(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
       /* if (!mStartedFlg) {
            mImageView.setVisibility(View.GONE);
        }*/

    }

    private void intView() {
        mSurfaceview = (SurfaceView) findViewById(R.id.surfaceview);
        mBtnStartStop = (Button) findViewById(R.id.btnStartStop);
        mTextView = (TextView) findViewById(R.id.text);
        mBtnStartStopAudio = (Button) findViewById(R.id.btnStartStopAudio);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mSurfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceview = null;
        mSurfaceHolder = null;
        handler.removeCallbacks(runnable);
        MediaRecorderMng.getInstance().stopRecord();
//        if (mRecorder != null) {
//            mRecorder.release();
//            mRecorder = null;
//        }
//        if (mCamera != null) {
//            mCamera.release();
//            mCamera = null;
//        }
    }

    public void onStartStopPlay(View view) {
        switch (view.getId()) {
            case R.id.btnStartStop:
                //如果正在录像
                if (!mStartedFlg) {
                    mStartedFlg = true;//录像开始
                    handler.postDelayed(runnable, 1000);
                    MediaRecorderMng.getInstance().startRecord(mSurfaceHolder);
                }
                //停止
                else {
                    if (mStartedFlg) {
                        handler.removeCallbacks(runnable);
                        MediaRecorderMng.getInstance().stopRecord();
                    }
                    mStartedFlg = false;
                }
                break;
            case R.id.btnStartStopAudio:
                if (!mStartedAudioFlg) {
                    mStartedAudioFlg = true;
                    ScoMng.getInstance().connSco();
//                    ScoMng.getInstance().startRecording();
                    mBtnStartStopAudio.setText("结束录制音频");
                } else {
//                    ScoMng.getInstance().stopRecording();
                    ScoMng.getInstance().disconnSco();
                    mStartedAudioFlg = false;
                    mBtnStartStopAudio.setText("开始录制音频");
                }

                break;
        }
    }


    @Override
    public void notifyStartRecordVideo() {
        mBtnStartStop.setText("结束录制");
    }

    @Override
    public void notifyStopRecordVideo() {
        mBtnStartStop.setText("开始录制");
        text = 0;
    }
}
