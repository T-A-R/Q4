package pro.quizer.quizer3.view.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.HiddenCameraUtils;
import com.androidhiddencamera.config.CameraFacing;
import com.androidhiddencamera.config.CameraImageFormat;
import com.androidhiddencamera.config.CameraResolution;
import com.androidhiddencamera.config.CameraRotation;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;

import pro.quizer.quizer3.R;
import pro.quizer.quizer3.camera.ShowCamera;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.FileUtils;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Anim;

public class Reg2Fragment extends ScreenFragment implements View.OnClickListener {
    private Button btnPhoto;
    private Button btnNext;
    private ImageView photoView;
    private boolean hasPhoto = false;
    private boolean wasInit = false;
    private Long mTimeToken = null;

    Camera camera;
    ShowCamera showCamera;
    FrameLayout cameraCont;

    public Reg2Fragment() {
        super(R.layout.fragment_reg2);
    }

    @Override
    protected void onReady() {
        RelativeLayout cont = findViewById(R.id.cont_reg2_fragment);
        btnPhoto = findViewById(R.id.btn_photo);
        btnNext = findViewById(R.id.btn_next);
        photoView = findViewById(R.id.photo_image);
        cameraCont = findViewById(R.id.camera_cont);

        MainFragment.disableSideMenu();

        btnPhoto.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        UiUtils.setButtonEnabled(btnNext, false);

        cont.startAnimation(Anim.getAppear(getContext()));
        btnPhoto.startAnimation(Anim.getAppearSlide(getContext(), 500));
        btnNext.startAnimation(Anim.getAppearSlide(getContext(), 500));

        camera = Camera.open(1);
        showCamera = new ShowCamera(getMainActivity(), camera);
        cameraCont.addView(showCamera);
        deleteRecursive(new File(FileUtils.getRegStoragePath(getMainActivity()) + FileUtils.FOLDER_DIVIDER + getCurrentUserId()));
    }

    @Override
    public void onClick(View view) {
        if (view == btnPhoto) {
            capturePhoto();
        } else if (view == btnNext) {
            UiUtils.setButtonEnabled(btnNext, false);
            UiUtils.setButtonEnabled(btnPhoto, false);
            if (camera != null) {
                camera.stopPreview();
                camera.release();
            }
            ScreenFragment reg3 = new Reg3Fragment();
            Bundle bundle = new Bundle();
            bundle.putLong("time", mTimeToken);
            reg3.setArguments(bundle);
            replaceFragment(reg3);
        }
    }

    @Override
    public boolean onBackPressed() {
        replaceFragment(new Reg1Fragment());
        return true;
    }

    Camera.PictureCallback mPictureCallback = (bytes, camera) -> {

        new Thread(() -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            UserModelR user = getCurrentUser();
            mTimeToken = DateUtils.getCurrentTimeMillis();
            //            [admin]^[project_id]^[user_login]^[unixtime].[extension]

            try {
                File dir = new File(FileUtils.getRegStoragePath(getMainActivity()) + File.separator
                        + user.getUser_id());
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast("Ошибка сохранения фото");
                return;
            }

            try {
                File dir = new File(FileUtils.getRegStoragePath(getMainActivity()) + File.separator
                        + user.getUser_id() + File.separator + mTimeToken);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast("Ошибка сохранения фото");
                return;
            }

            String path = FileUtils.getRegStoragePath(getMainActivity()) + File.separator
                    + user.getUser_id() + File.separator
                    + mTimeToken + File.separator
                    + user.getConfigR().getLoginAdmin()
                    + "^" + user.getConfigR().getProjectInfo().getProjectId()
                    + "^" + user.getLogin()
                    + "^" + mTimeToken;

            CameraConfig mCameraConfig = new CameraConfig()
                    .getBuilder(getContext())
                    .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                    .setCameraResolution(CameraResolution.LOW_RESOLUTION)
                    .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                    .setImageRotation(CameraRotation.ROTATION_270)
                    .buildForReg(path);

            Bitmap rotatedBitmap;

//            if (mCameraConfig.getImageRotation() != CameraRotation.ROTATION_0) {
                rotatedBitmap = flip(HiddenCameraUtils.rotateBitmap(bitmap, mCameraConfig.getImageRotation()));

                //noinspection UnusedAssignment
                bitmap = null;
//            } else {
//                rotatedBitmap = bitmap;
//            }

            //Save image to the file.
            if (HiddenCameraUtils.saveImageFromFile(rotatedBitmap,
                    mCameraConfig.getImageFile(),
                    mCameraConfig.getImageFormat())) {
                showToast(getString(R.string.captured));
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        hasPhoto = true;
                        btnPhoto.setText(R.string.remake);
                        UiUtils.setButtonEnabled(btnNext, true);
                        cameraCont.setVisibility(View.GONE);
                        photoView.setVisibility(View.VISIBLE);
                        Picasso.with(getMainActivity())
                                .load(getImageUri(rotatedBitmap))
                                .into(photoView);
                    }
                });
            } else {
                new Handler(Looper.getMainLooper()).post(() -> showToast(getString(R.string.save_photo_error)));
            }
        }).start();
    };

    private void capturePhoto() {
        if (!hasPhoto) {
            if (camera != null) {
                camera.startFaceDetection();
//                Camera.Parameters parameters = camera.getParameters();
//                parameters.ro
                camera.takePicture(null, null, mPictureCallback);
            }
        } else {
            photoView.setVisibility(View.GONE);
            cameraCont.setVisibility(View.VISIBLE);
            btnPhoto.setText("Фото");
            try {
                camera.startPreview();
                hasPhoto = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            UiUtils.setButtonEnabled(btnNext, false);
            if (mTimeToken != null)
                deleteRecursive(new File(
                        FileUtils.getRegStoragePath(getMainActivity()) + File.separator
                                + getCurrentUserId() + File.separator + mTimeToken));
        }
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getMainActivity().getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);
        fileOrDirectory.delete();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (camera != null) {
                camera.stopPreview();
                camera.release();
                camera = null;
            }
            if (showCamera != null) {
                showCamera.destroyDrawingCache();
                showCamera.setCamera(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap flip(Bitmap src) {
        // create new matrix for transformation
        Matrix matrix = new Matrix();

        matrix.preScale(-1.0f, 1.0f);

        // return transformed image
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }
}

