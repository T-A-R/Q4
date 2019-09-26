package pro.quizer.quizerexit.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.CameraError;
import com.androidhiddencamera.HiddenCameraFragment;
import com.androidhiddencamera.config.CameraFacing;
import com.androidhiddencamera.config.CameraImageFormat;
import com.androidhiddencamera.config.CameraResolution;
import com.androidhiddencamera.config.CameraRotation;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.activity.MainActivity;
import pro.quizer.quizerexit.utils.FileUtils;
import pro.quizer.quizerexit.utils.FontUtils;
import pro.quizer.quizerexit.utils.UiUtils;

import static pro.quizer.quizerexit.activity.BaseActivity.TAG;

public class BaseFragment extends HiddenCameraFragment implements Serializable {

    private BaseActivity mBaseActivity;

    public BaseFragment() {

    }

    private String mUserLogin = Constants.Strings.UNKNOWN;
    private String mLoginAdmin = Constants.Strings.UNKNOWN;
    private String mToken = Constants.Strings.UNKNOWN;
    private int mRelativeId = -1;
    private int mUserId = -1;
    private int mProjectId = -1;

    @Nullable
    public BaseActivity getBaseActivity() {
        return mBaseActivity;
    }

    public View onCreateView(@NonNull LayoutInflater pInflater, ViewGroup pContainer, Bundle pSavedInstanceState) {
        Log.d("FragmentLifeCycle", this + " - > onCreateView()");

        return null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d("FragmentLifeCycle", this + " - > onViewCreated()");

        super.onViewCreated(view, savedInstanceState);

        final Context context = getContext();
        if (context instanceof BaseActivity) {
            mBaseActivity = (BaseActivity) context;
        }


        mBaseActivity.setChangeFontCallback(new BaseActivity.ChangeFontCallback() {
            @Override
            public void onChangeFont() {
                refreshFragment();
            }
        });
    }

    public void refreshFragment() {
        if(getVisibleFragment() instanceof SettingsFragment) {

        } else {
            if(!mBaseActivity.isFinishing()) {
                showToast(getString(R.string.SETTED) + " " + FontUtils.getCurrentFontName(mBaseActivity.getFontSizePosition()));
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
            }
        }
    }

    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = mBaseActivity.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null){
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    @Override
    public void onAttach(Context context) {
//        Log.d("FragmentLifeCycle", this + " - > onAttach()");

        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
//        Log.d("FragmentLifeCycle", this + " - > onCreate()");

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        Log.d("FragmentLifeCycle", this + " - > onActivityCreated()");

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
//        Log.d("FragmentLifeCycle", this + " - > onStart()");

        super.onStart();
    }

    @Override
    public void onResume() {
//        Log.d("FragmentLifeCycle", this + " - > onResume()");

        super.onResume();
    }

    @Override
    public void onPause() {
//        Log.d("FragmentLifeCycle", this + " - > onPause()");

        super.onPause();
    }

    @Override
    public void onStop() {
//        Log.d("FragmentLifeCycle", this + " - > onStop()");

        super.onStop();
    }

    @Override
    public void onDestroyView() {
//        Log.d("FragmentLifeCycle", this + " - > onDestroyView()");

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
//        Log.d("FragmentLifeCycle", this + " - > onDestroy()");

        super.onDestroy();
    }

    @Override
    public void onDetach() {
//        Log.d("FragmentLifeCycle", this + " - > onDetach()");

        super.onDetach();
    }

    public void showToast(final CharSequence message) {
        final BaseActivity baseActivity = getBaseActivity();

        if (baseActivity != null) {
            baseActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getContext() != null)
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Nullable
    public View getProgressBar() {
        final View view = getView();

        if (view != null) {
            return view.findViewById(R.id.progressBar);
        } else {
            return null;
        }
    }

    @Nullable
    public TextView getEmptyView() {
        final View view = getView();

        if (view != null) {
            return view.findViewById(R.id.empty_text_label);
        } else {
            return null;
        }
    }

    public void hideProgressBar() {
        final BaseActivity activity = getBaseActivity();

        if (activity == null) {
            return;
        }

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                final View progressBar = getProgressBar();
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void showProgressBar() {
        final BaseActivity activity = getBaseActivity();

        if (activity == null) {
            return;
        }

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                final View progressBar = getProgressBar();
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void hideEmptyView() {
        final BaseActivity activity = getBaseActivity();

        if (activity == null) {
            return;
        }

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                final View emptyView = getEmptyView();
                if (emptyView != null) {
                    emptyView.setVisibility(View.GONE);
                }
            }
        });
    }

    public void showEmptyView(final String pError) {
        final BaseActivity activity = getBaseActivity();

        if (activity == null) {
            return;
        }

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                UiUtils.setTextOrHide(getEmptyView(), pError);
            }
        });
    }

    @Override
    public void onImageCapture(@NonNull File pImageFile) {
//        showToast("Фото сделано");

        if (FileUtils.renameFile(getContext(),
                pImageFile,
                mUserId,
                FileUtils.generatePhotoFileName(mLoginAdmin, mProjectId, mUserLogin, mToken, mRelativeId))) {
//            showToast("Фото переименованно");
        } else {
//            showToast("Фото не переименованно");
        }
    }

    @Override
    public void onCameraError(int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                //Camera open failed. Probably because another application
                //is using the camera
//                showToast(getString(R.string.CANT_START_CAMERA));
//                shotPicture(mLoginAdmin, mToken, mRelativeId, mUserId, mProjectId, mUserLogin);

                break;
            case CameraError.ERROR_IMAGE_WRITE_FAILED:
                //Image write failed. Please check if you have provided WRITE_EXTERNAL_STORAGE permission
//                showToast("Не удается сохранить фото");

                break;
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                //camera permission is not available
                //Ask for the camera permission before initializing it.
                showToast(getString(R.string.NO_ACCESS_TO_CAMERA));

                break;
            case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
                //Display information dialog to the user with steps to grant "Draw over other app"
                //permission for the app.
                //                HiddenCameraUtils.openDrawOverPermissionSetting(this);

//                showToast("Нет возможности делать фото поверх приложений");

                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                showToast(getString(R.string.NO_FRONT_CAMERA));

                break;
        }
    }


    @SuppressLint("MissingPermission")
    public void shotPicture(final String pLoginAdmin,
                            final String pToken,
                            final int pRelativeId,
                            final int pUserId,
                            final int pProjectId,
                            final String pUserLogin) {
        try {
            startCamera(new CameraConfig()
                    .getBuilder(getContext())
                    .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                    .setCameraResolution(CameraResolution.LOW_RESOLUTION)
                    .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                    .setImageRotation(CameraRotation.ROTATION_270)
                    .build());
        } catch (final Exception pException) {
            showToast("Не удается стартануть камеру");
            pException.printStackTrace();
            return;
        }

        mProjectId = pProjectId;
        mUserLogin = pUserLogin;
        mLoginAdmin = pLoginAdmin;
        mToken = pToken;
        mRelativeId = pRelativeId;
        mUserId = pUserId;

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    takePicture();
                    mBaseActivity.setHasPhoto("true");
                } catch (Exception e) {
                    e.printStackTrace();
                    onCameraError(CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA);
                    mBaseActivity.setHasPhoto("false");
                }
            }
        }, 1000);
    }
}
