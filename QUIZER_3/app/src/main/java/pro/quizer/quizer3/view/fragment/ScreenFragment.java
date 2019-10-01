package pro.quizer.quizer3.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import pro.quizer.quizer3.view.activity.ScreenActivity;
import pro.quizer.quizer3.model.User;

@SuppressWarnings("unused")
public abstract class ScreenFragment extends SmartFragment {
    static protected int numActivities = 0;
    private IMainFragment main;
    private ScreenListener screenListener;
    private boolean delegateScreen;
    private Class<? extends ScreenFragment> prevClass;
    private String cameraPhotoPath;
    private int requestCodeFragment;

    final int RequestCameraPermissionID = 1001;

    public ScreenFragment(int layoutSrc) {
        super(layoutSrc);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentActivity activity = getActivity();
        if (activity != null)
            KeyboardVisibilityEvent.setEventListener(activity, this::onKeyboardVisible);
    }

    protected void onKeyboardVisible(boolean isOpen) {

    }

    public IMainFragment getMain() {
        return main;
    }

    public void setMain(IMainFragment main) {
        this.main = main;
    }


    public void setScreenListener(ScreenListener listener) {
        this.screenListener = listener;
    }

    public void showScreensaver(int titleId, boolean full) {
        String title = getResources().getString(titleId);
        showScreensaver(title, full);
    }


    public boolean isMenuShown() {
        return false;
    }

    public boolean isDelegateScreen() {
        return delegateScreen;
    }

    public ScreenFragment setDelegateScreen(boolean delegateScreen) {
        this.delegateScreen = delegateScreen;
        return this;
    }

//    public void showLoadImageAlert(int cameraRequestCode, int galleryRequestCode) {
//        Context context = getContext();
//        if (context == null)
//            return;
//
//        new AlertDialog.Builder(context)
//                .setMessage(R.string.load_image)
//                .setPositiveButton(R.string.camera, (dialogInterface, i) -> openCamera(cameraRequestCode))
//                .setNegativeButton(R.string.gallery, (dialogInterface, i) -> openGallery(galleryRequestCode))
//                .create()
//                .show();
//    }

//    public void openGallery(int requestCode) {
//        System.gc();
//
//        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//        photoPickerIntent.setType("image/*");
//        startActivityForResult(photoPickerIntent, requestCode);
//    }

//    public void openCamera(int requestCode) {
//        requestCodeFragment = requestCode;
//        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(
//                    new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);
//            return;
//        }
//
//        Context context = getContext();
//        FragmentActivity activity = getActivity();
//        if (activity == null || context == null) {
//            return;
//        }
//
//        System.gc();
//
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(activity.getPackageManager()) == null) {
//            return;
//        }
//
//        cameraPhotoPath = null;
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File photoFile;
//        try {
//            photoFile = File.createTempFile(imageFileName, ".jpg", storageDir);
//        } catch (IOException e) {
//            Log.d(TAG, "ScreenFragment.openCamera() " + e);
//            return;
//        }
//
//        cameraPhotoPath = photoFile.getAbsolutePath();
//
//        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileUtils.getUriForFile(context, photoFile));
//        startActivityForResult(takePictureIntent, requestCode);
//    }

    public void showScreensaver(boolean full) {
        showScreensaver("", full);
    }

    public void showScreensaver(String title, boolean full) {
        hideKeyboard();
        if (main != null)
            main.showScreensaver(title, full);
    }

    public void hideScreensaver() {
        if (main != null)
            main.hideScreensaver();
    }

    public void showMenu() {
        if (main != null)
            main.showMenu();
    }

    public void hideMenu() {
        if (main != null)
            main.hideMenu();
    }

    public void setMenuCursor(int index) {
        if (main != null)
            main.setMenuCursor(index);
    }

    public void showToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    public User getUser() {
        return User.getUser();
    }

    public void openScreenInNewActivity(ScreenFragment newScreen) {
        openScreenInNewActivity(newScreen, this);
    }

    static public void openScreenInNewActivity(ScreenFragment newScreen, Fragment fragment) {
        if (fragment == null || fragment.getContext() == null)
            return;

        numActivities = Math.min(numActivities + 1, 3);
        MainFragment.newActivityScreen = newScreen;
        Intent intent = new Intent(fragment.getContext(), ScreenActivity.class);
        fragment.startActivity(intent);
    }

    protected boolean needCloseActivity() {
        if (getPrevClass() != null)
            return false;

        numActivities = Math.max(numActivities - 1, -1);
        return numActivities >= 0;
    }

    protected void replaceFragment(ScreenFragment newScreen) {
        //TODO Поменять имена фрагментов.
//        if (newScreen instanceof EventFragment ||
//                newScreen instanceof PlaceFragment ||
//                newScreen instanceof ParticipFragment ||
//                newScreen instanceof ComplainFragment ||
//                newScreen instanceof ComplainMessageFragment) {
//
//            openScreenInNewActivity(newScreen);
//            return;
//        }

        View view = getView();
        if (screenListener != null && view != null)
            view.post(() -> screenListener.fragmentReplace(ScreenFragment.this, newScreen));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        hideScreensaver();
    }

    public Class<? extends ScreenFragment> getPrevClass() {
        return prevClass;
    }

    public void setPrevClass(Class<? extends ScreenFragment> prevClass) {
        this.prevClass = prevClass;
    }

    public interface ScreenListener {
        void fragmentReplace(ScreenFragment curScreen, ScreenFragment newScreen);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case RequestCameraPermissionID: {
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                        openCamera(requestCodeFragment);
//                        return;
//                    }
//                }
//                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
//                    onBackPressed();
//                }
//            }
//            break;
//        }
//    }
}
