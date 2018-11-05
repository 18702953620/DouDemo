package com.ch.doudemo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.ch.doudemo.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 录制
 */
public class RecordActivity extends AppCompatActivity implements SurfaceHolder.Callback, EasyPermissions.PermissionCallbacks {

    @BindView(R.id.sv_record)
    SurfaceView svRecord;
    @BindView(R.id.btn_start)
    Button btnStart;
    @BindView(R.id.btn_end)
    Button btnEnd;
    @BindView(R.id.btn_switch)
    Button btnSwitch;
    @BindView(R.id.pb_record)
    SeekBar pbRecord;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private MediaRecorder mediaRecorder;
    //当前打开的摄像头标记 1--后，2--前
    private int currentCameraType = -1;
    private boolean isRecording;
    private File temFile;
    private MyTimer myTimer;
    private static final long TIME_MAX = 15 * 1000;
    private static final long TIME_INTERVAL = 500;

    private static final int RC_STORAGE = 1001;

    private Camera.Size size;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        surfaceHolder = svRecord.getHolder();
        surfaceHolder.addCallback(this);
        //设置一些参数方便后面绘图
        svRecord.setFocusable(true);
        svRecord.setKeepScreenOn(true);
        svRecord.setFocusableInTouchMode(true);

        pbRecord.setMax(100);
        pbRecord.setProgress(0);
    }

    @OnClick({R.id.btn_start, R.id.btn_end, R.id.btn_switch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //开始录制
            case R.id.btn_start:
                startRecord();
                break;
            //停止录制
            case R.id.btn_end:
                stopRecord(false);
                break;
            //切换摄像头
            case R.id.btn_switch:
                stopPreview();
                if (currentCameraType == 1) {
                    camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    currentCameraType = 2;
                    btnSwitch.setText("前");
                } else {
                    camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                    currentCameraType = 1;
                    btnSwitch.setText("后");
                }

                startPreview();
                break;
        }
    }

    /**
     * 开始录制
     */
    private void startRecord() {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }
        temFile = getTemFile();


        try {
            camera.unlock();
            mediaRecorder.setCamera(camera);
            //从相机采集视频
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            // 从麦克采集音频信息
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            //编码格式
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mediaRecorder.setVideoSize(size.width, size.height);

            //每秒的帧数
            mediaRecorder.setVideoFrameRate(24);
            // 设置帧频率，然后就清晰了
            mediaRecorder.setVideoEncodingBitRate(1 * 1024 * 1024 * 100);


            mediaRecorder.setOutputFile(temFile.getAbsolutePath());
            mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
            //解决录制视频, 播放器横向问题
            if (currentCameraType == 1) {
                //后置
                mediaRecorder.setOrientationHint(90);
            } else {
                //前置
                mediaRecorder.setOrientationHint(270);
            }
            mediaRecorder.prepare();
            //正式录制
            mediaRecorder.start();

            myTimer = new MyTimer(TIME_MAX, TIME_INTERVAL);
            myTimer.start();

            isRecording = true;
            showtoast("开始录制");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPreview();
    }

    /**
     * 获取临时文件目录
     *
     * @return
     */
    private File getTemFile() {
        String basePath = Environment.getExternalStorageDirectory().getPath() + "/doudemo/";

        File baseFile = new File(basePath);
        if (!baseFile.exists()) {
            baseFile.mkdirs();
        }

        File temp = new File(basePath + System.currentTimeMillis() + ".mp4");

        return temp;
    }

    /**
     * 停止录制
     */
    private void stopRecord(boolean delete) {
        if (mediaRecorder == null) {
            return;
        }
        if (myTimer != null) {
            myTimer.cancel();
        }

        try {
            mediaRecorder.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;
        if (camera != null) {
            camera.lock();
        }
        isRecording = false;

        if (delete) {
            if (temFile != null && temFile.exists()) {
                temFile.delete();
            }
        } else {
            //停止预览
            stopPreview();

            Intent intent = new Intent(RecordActivity.this, PrepareActivity.class);
            intent.putExtra(PrepareActivity.VIDEO_PATH, temFile.getPath());
            startActivity(intent);

        }
        showtoast("停止录制");
    }


    /**
     * 开始预览
     */
    private void startPreview() {
        if (svRecord == null || surfaceHolder == null) {
            return;
        }


        if (camera == null) {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            currentCameraType = 1;
            btnSwitch.setText("后");
        }


        try {
            camera.setPreviewDisplay(surfaceHolder);
            Camera.Parameters parameters = camera.getParameters();

            camera.setDisplayOrientation(90);

            //实现Camera自动对焦
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes != null) {
                for (String mode : focusModes) {
                    mode.contains("continuous-video");
                    parameters.setFocusMode("continuous-video");
                }
            }

            List<Camera.Size> sizes = parameters.getSupportedVideoSizes();
            if (sizes.size() > 0) {
                size = sizes.get(sizes.size() - 1);
            }

            camera.setParameters(parameters);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 停止预览
     */
    private void stopPreview() {
        //停止预览并释放摄像头资源
        if (camera == null) {
            return;
        }

        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }


    @Override
    protected void onStop() {
        super.onStop();
        stopPreview();
        stopRecord(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceHolder = holder;
        requestPermision();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        surfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //停止预览并释放摄像头资源
        stopPreview();
        //停止录制
        startRecord();
    }

    private void requestPermision() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            startPreview();
            // ...
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "我们的app需要以下权限",
                    RC_STORAGE, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        // Some permissions have been granted
        startPreview();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        // Some permissions have been denied
        finish();
    }


    public class MyTimer extends CountDownTimer {
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int progress = (int) ((TIME_MAX - millisUntilFinished) / (double) TIME_MAX * 100);
            Log.e("cheng", "millisUntilFinished=" + progress);
            pbRecord.setProgress(progress);

        }

        @Override
        public void onFinish() {
            stopRecord(false);
        }
    }

    /**
     * @param s
     */
    public void showtoast(@NonNull String s) {
        Toast.makeText(RecordActivity.this, s, Toast.LENGTH_SHORT).show();
    }

}
