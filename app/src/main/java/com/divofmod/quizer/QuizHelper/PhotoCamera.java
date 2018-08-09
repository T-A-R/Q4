package com.divofmod.quizer.QuizHelper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

//new PhotoCamera(this, mFrameLayout, mPhotoName).CreatePhoto();
public class PhotoCamera {

    private Context mContext;
    private FrameLayout mFrameLayout;

    private static final int NO_FRONT_CAMERA = -1;

    private Camera mCamera;
    private boolean mSafeToTakePicture = false;
    private SurfaceHolder mSurfaceHolder;

    private String mPhotoName;

    public PhotoCamera(Context context, FrameLayout frameLayout, String photoName) {
        mContext = context;
        mFrameLayout = frameLayout;
        mPhotoName = photoName;

    }

    private class CameraPreview extends SurfaceView {
        public CameraPreview(Context context) {
            super(context);
        }
    }

    public void createPhoto() {

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
            return;

        SurfaceView surfaceView = new CameraPreview(mContext);
        mFrameLayout.addView(surfaceView);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mSafeToTakePicture = true;
                mSurfaceHolder = holder;
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        final int cameraId = getFrontCameraId();
        if (cameraId != NO_FRONT_CAMERA) {
            mCamera = Camera.open(cameraId);

            Camera.Parameters parameters = mCamera.getParameters();

            parameters.setRotation(0);

            List<String> flashModes = parameters.getSupportedFlashModes();
            if (flashModes != null && flashModes.contains(Camera.Parameters.FLASH_MODE_OFF))
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

            List<String> whiteBalance = parameters.getSupportedWhiteBalance();
            if (whiteBalance != null && whiteBalance.contains(Camera.Parameters.WHITE_BALANCE_AUTO))
                parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);

            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes != null && focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO))
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
            if (sizes != null && sizes.size() > 0) {
                Camera.Size size1 = sizes.get(0);
                Camera.Size size2 = sizes.get(sizes.size() - 1);

                if (size1.height > size2.height) {
                    for (Camera.Size size : sizes)
                        if (size.height <= 480) {
                            parameters.setPictureSize(size.width, size.height);
                            break;
                        }

                } else
                    for (Camera.Size size : sizes)
                        if (size.height >= 480) {
                            parameters.setPictureSize(size.width, size.height);
                            break;
                        }

            }

            List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
            if (previewSizes != null) {
                Camera.Size previewSize = previewSizes.get(previewSizes.size() - 1);
                parameters.setPreviewSize(previewSize.width, previewSize.height);
            }

            mCamera.setParameters(parameters);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                mCamera.enableShutterSound(false);

            new Thread(new Runnable() {
                public void run() {
                    do {
                        if (mSafeToTakePicture) {
                            try {
                                mCamera.setPreviewDisplay(mSurfaceHolder);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mCamera.startPreview();
                            try {
                                mCamera.takePicture(null, null, null, new Camera.PictureCallback() {

                                    @Override
                                    public void onPictureTaken(final byte[] data, Camera camera) {
                                        new Thread(new Runnable() {

                                            public void run() {
                                                try {
                                                    FileOutputStream os = new FileOutputStream(new File(
                                                            mContext.getFilesDir(), "files/" + mPhotoName + ".jpg"));
                                                    os.write(data);
                                                    os.close();
                                                } catch (Exception ignored) {
                                                }
                                            }
                                        }).start();

                                        if (mCamera != null) {
                                            mCamera.setPreviewCallback(null);
                                            mCamera.stopPreview();
                                            mCamera.release();
                                            mCamera = null;
                                        }
                                    }
                                });
                            } catch (Exception pE) {

                            }

                            break;
                        }
                    }
                    while (true);
                }
            }).start();
        }
    }

    private int getFrontCameraId() {
        final int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) return i;
        }
        return NO_FRONT_CAMERA;
    }
}
