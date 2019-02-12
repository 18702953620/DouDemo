package com.ch.doudemo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.ch.doudemo.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class Record2Activity extends AppCompatActivity implements SurfaceHolder.Callback, EasyPermissions.PermissionCallbacks {

    @BindView(R.id.sv_record)
    SurfaceView svRecord;
    @BindView(R.id.btn_start)
    Button btnStart;
    @BindView(R.id.btn_switch)
    Button btnSwitch;
    @BindView(R.id.btn_end)
    Button btnEnd;
    @BindView(R.id.pb_record)
    SeekBar pbRecord;

    private static final int RC_STORAGE = 1001;
    private SurfaceHolder surfaceHolder;
    private CameraDevice camera;

    private ImageReader imageReader;


    @SuppressLint("HandlerLeak")
    private Handler handler;
    private CaptureRequest.Builder mCaptureRequestBuilder;
    private CaptureRequest mCaptureRequest;
    private CameraCaptureSession mPreviewSession;
    private HandlerThread mThreadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record2);
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

        mThreadHandler = new HandlerThread("CAMERA2");
        mThreadHandler.start();
        handler = new Handler(mThreadHandler.getLooper());
    }

    @OnClick({R.id.btn_start, R.id.btn_switch, R.id.btn_end})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                break;
            case R.id.btn_switch:
                break;
            case R.id.btn_end:
                break;
        }
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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceHolder = holder;
        requestPermision();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        //停止预览并释放摄像头资源
        stopPreview();
        //停止录制
        startRecord();

    }

    private void startPreview() {
        if (svRecord == null || surfaceHolder == null) {
            return;
        }

        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String[] cameras = manager.getCameraIdList();
            if (cameras != null && cameras.length > 0) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameras[0]);

                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());

                surfaceHolder.setFixedSize(largest.getWidth(), largest.getHeight());

                imageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, /*maxImages*/2);//初始化ImageReader

                imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                    @Override
                    public void onImageAvailable(ImageReader reader) {

                    }
                }, handler);


                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                manager.openCamera(cameras[0], new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(@NonNull CameraDevice c) {
                        Log.e("cheng", "onOpened");
                        camera = c;

                        try {
                            //创建CaptureRequestBuilder，TEMPLATE_PREVIEW比表示预览请求
                            mCaptureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                            //设置Surface作为预览数据的显示界面
                            mCaptureRequestBuilder.addTarget(surfaceHolder.getSurface());

                            camera.createCaptureSession(Arrays.asList(surfaceHolder.getSurface(), imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                                @Override
                                public void onConfigured(@NonNull CameraCaptureSession session) {
                                    try {
                                        Log.e("cheng", "onConfigured");
                                        //创建捕获请求
                                        mCaptureRequest = mCaptureRequestBuilder.build();
                                        mPreviewSession = session;
                                        //设置反复捕获数据的请求，这样预览界面就会一直有数据显示
                                        mPreviewSession.setRepeatingRequest(mCaptureRequest, null, null);
                                    } catch (CameraAccessException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                                    Log.e("cheng", "onConfigureFailed");
                                    camera.close();
                                }
                            }, handler);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onDisconnected(@NonNull CameraDevice camera) {
                        Log.e("cheng", "onDisconnected");

                    }

                    @Override
                    public void onError(@NonNull CameraDevice camera, int error) {
                        Log.e("cheng", "error=" + error);
                        if (error == CameraDevice.StateCallback.ERROR_CAMERA_IN_USE) {
                            Log.e("cheng", "ERROR_CAMERA_IN_USE");
                        }

                        camera.close();
                    }
                }, null);
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    private void startRecord() {
    }

    private void stopPreview() {
        if (camera != null) {
            camera.close();
        }
    }


    private static class CompareSizesByArea implements Comparator<android.util.Size> {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public int compare(android.util.Size lhs, android.util.Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
        }
    }
}
