package com.xintu.mediarecorder;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.File;
import java.util.Calendar;

/**
 * Created by xintu on 2018/1/30.
 */

public class MediaRecorderMng {
    private static final String TAG = "MediaRecorderMng";
    private static MediaRecorderMng mediaRecorderMng = null;

    private MediaRecorder mRecorder;//多媒体录音
    private Camera mCamera;//相机
    private String path;//视频保存路径


    MediaRecorderMngListener listener = null;

    public static MediaRecorderMng getInstance() {
        if (mediaRecorderMng == null) {
            mediaRecorderMng = new MediaRecorderMng();
        }
        return mediaRecorderMng;
    }

    public void setListener(MediaRecorderMngListener listener) {
        Log.d(TAG, "setListener: ");
        this.listener = listener;
    }

    public void startRecord(SurfaceHolder mSurfaceHolder) {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
        }
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        if (mCamera != null) {
            mCamera.setDisplayOrientation(90);
            mCamera.unlock();
            mRecorder.setCamera(mCamera);
        }
        try {
            // 这两项需要放在setOutputFormat之前,设置音频和视频的来源
            mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);//摄录像机
            mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);//相机

            // Set output file format
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//输出格式 mp4

            // 这两项需要放在setOutputFormat之后  设置编码器
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//音频编码格式
            mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);//视频编码格式

            mRecorder.setVideoSize(640, 480);//视频分辨率
            mRecorder.setVideoFrameRate(30);//帧速率
            mRecorder.setVideoEncodingBitRate(3 * 1024 * 1024);//视频清晰度
            mRecorder.setOrientationHint(90);//输出视频播放的方向提示
            //设置记录会话的最大持续时间（毫秒）
            mRecorder.setMaxDuration(30 * 1000);
            mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());//预览显示的控件

            path = getSDPath();
            if (path != null) {
                File dir = new File(path + "/recordtest");
                if (!dir.exists()) {//如果不存在这个文件，则创建。
                    dir.mkdir();
                }
                path = dir + "/" + getDate() + ".mp4";
                mRecorder.setOutputFile(path);//输出文件路径
                mRecorder.prepare();//准备
                mRecorder.start();//开始
                if (listener != null) {
                    listener.notifyStartRecordVideo();
                }
//                mStartedFlg = true;//录像开始
//                mBtnStartStop.setText("结束录制");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        if (mRecorder != null) {
            mRecorder.stop();//停止
            mRecorder.reset();//重置，设置为空闲状态
            mRecorder.release();//释放
            mRecorder = null;
        }
        if (listener != null) {
            listener.notifyStopRecordVideo();
        }
//        mBtnStartStop.setText("开始录制");
//        text = 0;
        //释放相机
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public interface MediaRecorderMngListener {
        public abstract void notifyStartRecordVideo();

        public abstract void notifyStopRecordVideo();

    }

    //获取系统时间 视频保存的时间
    public static String getDate() {
        Calendar mCalendar = Calendar.getInstance();
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DATE);
        int hour = mCalendar.get(Calendar.HOUR);
        int minute = mCalendar.get(Calendar.MINUTE);
        int second = mCalendar.get(Calendar.SECOND);
        String date = "" + year + (month + 1) + day + hour + minute + second;
        Log.d("date", "date:" + date);
        return date;
    }

    //获取SD卡路径
    public String getSDPath() {
        File sdDir = null;
        // 判断sd卡是否存在
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取根目录
            return sdDir.toString();
        }
        return null;
    }

}
