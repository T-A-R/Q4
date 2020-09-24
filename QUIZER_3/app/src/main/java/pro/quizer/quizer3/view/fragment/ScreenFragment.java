package pro.quizer.quizer3.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.List;
import java.util.Objects;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.utils.FontUtils;
import pro.quizer.quizer3.view.activity.ScreenActivity;
import pro.quizer.quizer3.model.User;

import static pro.quizer.quizer3.MainActivity.TAG;

@SuppressWarnings("unused")
public abstract class ScreenFragment extends SmartFragment {
    static protected int numActivities = 0;
    private IMainFragment main;
    private ScreenListener screenListener;
    private boolean delegateScreen;
    //    private Class<? extends ScreenFragment> prevClass;
    private String cameraPhotoPath;
    private int requestCodeFragment;

    protected LinearLayout pageCont2;
    protected LinearLayout pageCont3;
    protected LinearLayout pageCont4;
    protected LinearLayout pageCont5;

    protected LinearLayout unhideCont;
    protected LinearLayout titleCont1;
    protected LinearLayout titleCont2;
    protected LinearLayout titleImagesCont1;
    protected LinearLayout titleImagesCont2;
    protected LinearLayout questionCont;
    protected LinearLayout questionImagesCont;
    protected LinearLayout spinnerCont;
    protected LinearLayout infoCont;
    protected FrameLayout tableCont;
    protected TextView tvUnhide;
    protected TextView tvTitle1;
    protected TextView tvTitle2;
    protected TextView tvQuestion;
    protected TextView tvTitleDesc1;
    protected TextView tvTitleDesc2;
    protected TextView tvQuestionDesc;
    protected WebView infoText;
    protected RecyclerView rvAnswers;
    protected RecyclerView rvScale;
    protected Spinner spinnerAnswers;
    protected AdaptiveTableLayout tableLayout;
    protected ImageView title1Image1;
    protected ImageView title1Image2;
    protected ImageView title1Image3;
    protected ImageView title2Image1;
    protected ImageView title2Image2;
    protected ImageView title2Image3;
    protected ImageView questionImage1;
    protected ImageView questionImage2;
    protected ImageView questionImage3;
    protected ImageView closeImage1;
    protected ImageView closeImage2;

    protected LinearLayout unhideCont_2;
    protected LinearLayout titleCont1_2;
    protected LinearLayout titleCont2_2;
    protected LinearLayout titleImagesCont1_2;
    protected LinearLayout titleImagesCont2_2;
    protected LinearLayout questionCont_2;
    protected LinearLayout questionImagesCont_2;
    protected LinearLayout spinnerCont_2;
    protected LinearLayout infoCont_2;
    protected FrameLayout tableCont_2;
    protected TextView tvUnhide_2;
    protected TextView tvTitle1_2;
    protected TextView tvTitle2_2;
    protected TextView tvQuestion_2;
    protected TextView tvTitleDesc1_2;
    protected TextView tvTitleDesc2_2;
    protected TextView tvQuestionDesc_2;
    protected WebView infoText_2;
    protected RecyclerView rvAnswers_2;
    protected RecyclerView rvScale_2;
    protected Spinner spinnerAnswers_2;
    protected AdaptiveTableLayout tableLayout_2;
    protected ImageView title1Image1_2;
    protected ImageView title1Image2_2;
    protected ImageView title1Image3_2;
    protected ImageView title2Image1_2;
    protected ImageView title2Image2_2;
    protected ImageView title2Image3_2;
    protected ImageView questionImage1_2;
    protected ImageView questionImage2_2;
    protected ImageView questionImage3_2;
    protected ImageView closeImage1_2;
    protected ImageView closeImage2_2;

    protected LinearLayout unhideCont_3;
    protected LinearLayout titleCont1_3;
    protected LinearLayout titleCont2_3;
    protected LinearLayout titleImagesCont1_3;
    protected LinearLayout titleImagesCont2_3;
    protected LinearLayout questionCont_3;
    protected LinearLayout questionImagesCont_3;
    protected LinearLayout spinnerCont_3;
    protected LinearLayout infoCont_3;
    protected FrameLayout tableCont_3;
    protected TextView tvUnhide_3;
    protected TextView tvTitle1_3;
    protected TextView tvTitle2_3;
    protected TextView tvQuestion_3;
    protected TextView tvTitleDesc1_3;
    protected TextView tvTitleDesc2_3;
    protected TextView tvQuestionDesc_3;
    protected WebView infoText_3;
    protected RecyclerView rvAnswers_3;
    protected RecyclerView rvScale_3;
    protected Spinner spinnerAnswers_3;
    protected AdaptiveTableLayout tableLayout_3;
    protected ImageView title1Image1_3;
    protected ImageView title1Image2_3;
    protected ImageView title1Image3_3;
    protected ImageView title2Image1_3;
    protected ImageView title2Image2_3;
    protected ImageView title2Image3_3;
    protected ImageView questionImage1_3;
    protected ImageView questionImage2_3;
    protected ImageView questionImage3_3;
    protected ImageView closeImage1_3;
    protected ImageView closeImage2_3;

    protected LinearLayout unhideCont_4;
    protected LinearLayout titleCont1_4;
    protected LinearLayout titleCont2_4;
    protected LinearLayout titleImagesCont1_4;
    protected LinearLayout titleImagesCont2_4;
    protected LinearLayout questionCont_4;
    protected LinearLayout questionImagesCont_4;
    protected LinearLayout spinnerCont_4;
    protected LinearLayout infoCont_4;
    protected FrameLayout tableCont_4;
    protected TextView tvUnhide_4;
    protected TextView tvTitle1_4;
    protected TextView tvTitle2_4;
    protected TextView tvQuestion_4;
    protected TextView tvTitleDesc1_4;
    protected TextView tvTitleDesc2_4;
    protected TextView tvQuestionDesc_4;
    protected WebView infoText_4;
    protected RecyclerView rvAnswers_4;
    protected RecyclerView rvScale_4;
    protected Spinner spinnerAnswers_4;
    protected AdaptiveTableLayout tableLayout_4;
    protected ImageView title1Image1_4;
    protected ImageView title1Image2_4;
    protected ImageView title1Image3_4;
    protected ImageView title2Image1_4;
    protected ImageView title2Image2_4;
    protected ImageView title2Image3_4;
    protected ImageView questionImage1_4;
    protected ImageView questionImage2_4;
    protected ImageView questionImage3_4;
    protected ImageView closeImage1_4;
    protected ImageView closeImage2_4;

    protected LinearLayout unhideCont_5;
    protected LinearLayout titleCont1_5;
    protected LinearLayout titleCont2_5;
    protected LinearLayout titleImagesCont1_5;
    protected LinearLayout titleImagesCont2_5;
    protected LinearLayout questionCont_5;
    protected LinearLayout questionImagesCont_5;
    protected LinearLayout spinnerCont_5;
    protected LinearLayout infoCont_5;
    protected FrameLayout tableCont_5;
    protected TextView tvUnhide_5;
    protected TextView tvTitle1_5;
    protected TextView tvTitle2_5;
    protected TextView tvQuestion_5;
    protected TextView tvTitleDesc1_5;
    protected TextView tvTitleDesc2_5;
    protected TextView tvQuestionDesc_5;
    protected WebView infoText_5;
    protected RecyclerView rvAnswers_5;
    protected RecyclerView rvScale_5;
    protected Spinner spinnerAnswers_5;
    protected AdaptiveTableLayout tableLayout_5;
    protected ImageView title1Image1_5;
    protected ImageView title1Image2_5;
    protected ImageView title1Image3_5;
    protected ImageView title2Image1_5;
    protected ImageView title2Image2_5;
    protected ImageView title2Image3_5;
    protected ImageView questionImage1_5;
    protected ImageView questionImage2_5;
    protected ImageView questionImage3_5;
    protected ImageView closeImage1_5;
    protected ImageView closeImage2_5;

//    protected View[][] viewsArray;

    final int RequestCameraPermissionID = 1001;

    public ScreenFragment(int layoutSrc) {
        super(layoutSrc);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
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
//        if (getPrevClass() != null)
//            return false;

        numActivities = Math.max(numActivities - 1, -1);
        return numActivities >= 0;
    }

    protected void replaceFragment(ScreenFragment newScreen) {
        View view = getView();
        if (screenListener != null && view != null)
            view.post(() -> screenListener.fragmentReplace(newScreen, false));
    }

    protected void replaceFragmentBack(ScreenFragment newScreen) {
        View view = getView();
        if (screenListener != null && view != null)
            view.post(() -> screenListener.fragmentReplace(newScreen, true));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        hideScreensaver();
    }

    public interface ScreenListener {
        void fragmentReplace(ScreenFragment newScreen, boolean fromBackPress);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().setChangeFontCallback(new MainActivity.ChangeFontCallback() {
            @Override
            public void onChangeFont() {
                refreshFragment();
            }
        });
    }

    public void refreshFragment() {
        if (getVisibleFragment() instanceof SettingsFragment) {

        } else {
            if (!getMainActivity().isFinishing()) {
                showToast(getString(R.string.setted) + " " + FontUtils.getCurrentFontName(getMainActivity().getFontSizePosition()));
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
            }
        }
    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = getMainActivity().getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    public void setViewBackground(View view, boolean visible, boolean border) {
        if(isAvia()) {
            if (visible) {
                view.setEnabled(true);
                if (getMainActivity().getAndroidVersion() < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackgroundDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), border ? R.drawable.button_background_red : R.drawable.button_background_red_without_border));
                } else {
                    view.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), border ? R.drawable.button_background_red : R.drawable.button_background_red_without_border));
                }
            } else {
                view.setEnabled(false);
                if (getMainActivity().getAndroidVersion() < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackgroundDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_gray_avia));
                } else {
                    view.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_gray_avia));
                }
            }
        } else {
            if (visible) {
                view.setEnabled(true);
                if (getMainActivity().getAndroidVersion() < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackgroundDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_green));
                } else {
                    view.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_green));
                }
            } else {
                view.setEnabled(false);
                if (getMainActivity().getAndroidVersion() < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackgroundDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_gray));
                } else {
                    view.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.button_background_gray));
                }
            }
        }
    }


//    protected void initPageCont1() {
//        viewsArray[0][0] = (LinearLayout) findViewById(R.id.unhide_cont); // unhideCont
//        viewsArray[0][1] = (LinearLayout) findViewById(R.id.title_cont_1); // titleCont1
//        viewsArray[0][2] = (LinearLayout) findViewById(R.id.title_cont_2); // titleCont2
//        viewsArray[0][3] = (LinearLayout) findViewById(R.id.title_images_cont_1); // titleImagesCont1
//        viewsArray[0][4] = (LinearLayout) findViewById(R.id.title_images_cont_2); // titleImagesCont2
//        viewsArray[0][5] = (LinearLayout) findViewById(R.id.question_cont); // questionCont
//        viewsArray[0][6] = (LinearLayout) findViewById(R.id.question_images_cont); // questionImagesCont
//        viewsArray[0][7] = (LinearLayout) findViewById(R.id.spinner_cont); // spinnerCont
//        viewsArray[0][8] = (LinearLayout) findViewById(R.id.info_cont); // infoCont
//        viewsArray[0][9] = (FrameLayout) findViewById(R.id.table_cont); // tableCont
//        viewsArray[0][10] = (RecyclerView) findViewById(R.id.answers_recyclerview); // rvAnswers
//        viewsArray[0][11] = (RecyclerView) findViewById(R.id.scale_recyclerview); // rvScale
//        viewsArray[0][12] = (Spinner) findViewById(R.id.answers_spinner); // spinnerAnswers
//        viewsArray[0][13] = (AdaptiveTableLayout) findViewById(R.id.table_question_layout); // tableLayout
//        viewsArray[0][14] = (TextView) findViewById(R.id.unhide_title); // tvUnhide
//        viewsArray[0][15] = (TextView) findViewById(R.id.title_1); // tvTitle1
//        viewsArray[0][16] = (TextView) findViewById(R.id.title_2); // tvTitle2
//        viewsArray[0][17] = (TextView) findViewById(R.id.title_desc_1); // tvTitleDesc1
//        viewsArray[0][18] = (TextView) findViewById(R.id.title_desc_2); // tvTitleDesc2
//        viewsArray[0][19] = (TextView) findViewById(R.id.question); // tvQuestion
//        viewsArray[0][20] = (TextView) findViewById(R.id.question_desc); // tvQuestionDesc
//        viewsArray[0][21] = (WebView) findViewById(R.id.info_text); // infoText
//        viewsArray[0][22] = (ImageView) findViewById(R.id.title_1_image_1); // title1Image1
//        viewsArray[0][23] = (ImageView) findViewById(R.id.title_1_image_2); // title1Image2
//        viewsArray[0][24] = (ImageView) findViewById(R.id.title_1_image_3); // title1Image3
//        viewsArray[0][25] = (ImageView) findViewById(R.id.title_2_image_1); // title2Image1
//        viewsArray[0][26] = (ImageView) findViewById(R.id.title_2_image_2); // title2Image2
//        viewsArray[0][27] = (ImageView) findViewById(R.id.title_2_image_3); // title2Image3
//        viewsArray[0][28] = (ImageView) findViewById(R.id.question_image_1); // questionImage1
//        viewsArray[0][29] = (ImageView) findViewById(R.id.question_image_2); // questionImage2
//        viewsArray[0][30] = (ImageView) findViewById(R.id.question_image_3); // questionImage3
//        viewsArray[0][31] = (ImageView) findViewById(R.id.image_close_1); // closeImage1
//        viewsArray[0][32] = (ImageView) findViewById(R.id.image_close_2); // closeImage2
//    }
//
//    protected void initPageCont2() {
//        pageCont2 = (LinearLayout) findViewById(R.id.page_cont_2);
//        unhideCont_2 = (LinearLayout) findViewById(R.id.unhide_cont_2);
//        titleCont1_2 = (LinearLayout) findViewById(R.id.title_cont_1_2);
//        titleCont2_2 = (LinearLayout) findViewById(R.id.title_cont_2_2);
//        titleImagesCont1_2 = (LinearLayout) findViewById(R.id.title_images_cont_1_2);
//        titleImagesCont2_2 = (LinearLayout) findViewById(R.id.title_images_cont_2_2);
//        questionCont_2 = (LinearLayout) findViewById(R.id.question_cont_2);
//        questionImagesCont_2 = (LinearLayout) findViewById(R.id.question_images_cont_2);
//        spinnerCont_2 = (LinearLayout) findViewById(R.id.spinner_cont_2);
//        infoCont_2 = (LinearLayout) findViewById(R.id.info_cont_2);
//        tableCont_2 = (FrameLayout) findViewById(R.id.table_cont_2);
//        rvAnswers_2 = (RecyclerView) findViewById(R.id.answers_recyclerview_2);
//        rvScale_2 = (RecyclerView) findViewById(R.id.scale_recyclerview_2);
//        spinnerAnswers_2 = (Spinner) findViewById(R.id.answers_spinner_2);
//        tableLayout_2 = (AdaptiveTableLayout) findViewById(R.id.table_question_layout_2);
//        tvUnhide_2 = (TextView) findViewById(R.id.unhide_title_2);
//        tvTitle1_2 = (TextView) findViewById(R.id.title_1_2);
//        tvTitle2_2 = (TextView) findViewById(R.id.title_2_2);
//        tvTitleDesc1_2 = (TextView) findViewById(R.id.title_desc_1_2);
//        tvTitleDesc2_2 = (TextView) findViewById(R.id.title_desc_2_2);
//        tvQuestion_2 = (TextView) findViewById(R.id.question_2);
//        tvQuestionDesc_2 = (TextView) findViewById(R.id.question_desc_2);
//        infoText_2 = (WebView) findViewById(R.id.info_text_2);
//        title1Image1_2 = (ImageView) findViewById(R.id.title_1_image_1_2);
//        title1Image2_2 = (ImageView) findViewById(R.id.title_1_image_2_2);
//        title1Image3_2 = (ImageView) findViewById(R.id.title_1_image_3_2);
//        title2Image1_2 = (ImageView) findViewById(R.id.title_2_image_1_2);
//        title2Image2_2 = (ImageView) findViewById(R.id.title_2_image_2_2);
//        title2Image3_2 = (ImageView) findViewById(R.id.title_2_image_3_2);
//        questionImage1_2 = (ImageView) findViewById(R.id.question_image_1_2);
//        questionImage2_2 = (ImageView) findViewById(R.id.question_image_2_2);
//        questionImage3_2 = (ImageView) findViewById(R.id.question_image_3_2);
//        closeImage1_2 = (ImageView) findViewById(R.id.image_close_1_2);
//        closeImage2_2 = (ImageView) findViewById(R.id.image_close_2_2);
//
//        pageCont2.setVisibility(View.VISIBLE);
//    }
//
//    protected void initPageCont3() {
//        pageCont3 = (LinearLayout) findViewById(R.id.page_cont_3);
//        unhideCont_3 = (LinearLayout) findViewById(R.id.unhide_cont_3);
//        titleCont1_3 = (LinearLayout) findViewById(R.id.title_cont_1_3);
//        titleCont2_3 = (LinearLayout) findViewById(R.id.title_cont_2_3);
//        titleImagesCont1_3 = (LinearLayout) findViewById(R.id.title_images_cont_1_3);
//        titleImagesCont2_3 = (LinearLayout) findViewById(R.id.title_images_cont_2_3);
//        questionCont_3 = (LinearLayout) findViewById(R.id.question_cont_3);
//        questionImagesCont_3 = (LinearLayout) findViewById(R.id.question_images_cont_3);
//        spinnerCont_3 = (LinearLayout) findViewById(R.id.spinner_cont_3);
//        infoCont_3 = (LinearLayout) findViewById(R.id.info_cont_3);
//        tableCont_3 = (FrameLayout) findViewById(R.id.table_cont_3);
//        rvAnswers_3 = (RecyclerView) findViewById(R.id.answers_recyclerview_3);
//        rvScale_3 = (RecyclerView) findViewById(R.id.scale_recyclerview_3);
//        spinnerAnswers_3 = (Spinner) findViewById(R.id.answers_spinner_3);
//        tableLayout_3 = (AdaptiveTableLayout) findViewById(R.id.table_question_layout_3);
//        tvUnhide_3 = (TextView) findViewById(R.id.unhide_title_3);
//        tvTitle1_3 = (TextView) findViewById(R.id.title_1_3);
//        tvTitle2_3 = (TextView) findViewById(R.id.title_2_3);
//        tvTitleDesc1_3 = (TextView) findViewById(R.id.title_desc_1_3);
//        tvTitleDesc2_3 = (TextView) findViewById(R.id.title_desc_2_3);
//        tvQuestion_3 = (TextView) findViewById(R.id.question_3);
//        tvQuestionDesc_3 = (TextView) findViewById(R.id.question_desc_3);
//        infoText_3 = (WebView) findViewById(R.id.info_text_3);
//        title1Image1_3 = (ImageView) findViewById(R.id.title_1_image_1_3);
//        title1Image2_3 = (ImageView) findViewById(R.id.title_1_image_2_3);
//        title1Image3_3 = (ImageView) findViewById(R.id.title_1_image_3_3);
//        title2Image1_3 = (ImageView) findViewById(R.id.title_2_image_1_3);
//        title2Image2_3 = (ImageView) findViewById(R.id.title_2_image_2_3);
//        title2Image3_3 = (ImageView) findViewById(R.id.title_2_image_3_3);
//        questionImage1_3 = (ImageView) findViewById(R.id.question_image_1_3);
//        questionImage2_3 = (ImageView) findViewById(R.id.question_image_2_3);
//        questionImage3_3 = (ImageView) findViewById(R.id.question_image_3_3);
//        closeImage1_3 = (ImageView) findViewById(R.id.image_close_1_3);
//        closeImage2_3 = (ImageView) findViewById(R.id.image_close_2_3);
//
//        pageCont3.setVisibility(View.VISIBLE);
//    }
//
//    protected void initPageCont4() {
//        pageCont4 = (LinearLayout) findViewById(R.id.page_cont_4);
//        unhideCont_4 = (LinearLayout) findViewById(R.id.unhide_cont_4);
//        titleCont1_4 = (LinearLayout) findViewById(R.id.title_cont_1_4);
//        titleCont2_4 = (LinearLayout) findViewById(R.id.title_cont_2_4);
//        titleImagesCont1_4 = (LinearLayout) findViewById(R.id.title_images_cont_1_4);
//        titleImagesCont2_4 = (LinearLayout) findViewById(R.id.title_images_cont_2_4);
//        questionCont_4 = (LinearLayout) findViewById(R.id.question_cont_4);
//        questionImagesCont_4 = (LinearLayout) findViewById(R.id.question_images_cont_4);
//        spinnerCont_4 = (LinearLayout) findViewById(R.id.spinner_cont_4);
//        infoCont_4 = (LinearLayout) findViewById(R.id.info_cont_4);
//        tableCont_4 = (FrameLayout) findViewById(R.id.table_cont_4);
//        rvAnswers_4 = (RecyclerView) findViewById(R.id.answers_recyclerview_4);
//        rvScale_4 = (RecyclerView) findViewById(R.id.scale_recyclerview_4);
//        spinnerAnswers_4 = (Spinner) findViewById(R.id.answers_spinner_4);
//        tableLayout_4 = (AdaptiveTableLayout) findViewById(R.id.table_question_layout_4);
//        tvUnhide_4 = (TextView) findViewById(R.id.unhide_title_4);
//        tvTitle1_4 = (TextView) findViewById(R.id.title_1_4);
//        tvTitle2_4 = (TextView) findViewById(R.id.title_2_4);
//        tvTitleDesc1_4 = (TextView) findViewById(R.id.title_desc_1_4);
//        tvTitleDesc2_4 = (TextView) findViewById(R.id.title_desc_2_4);
//        tvQuestion_4 = (TextView) findViewById(R.id.question_4);
//        tvQuestionDesc_4 = (TextView) findViewById(R.id.question_desc_4);
//        infoText_4 = (WebView) findViewById(R.id.info_text_4);
//        title1Image1_4 = (ImageView) findViewById(R.id.title_1_image_1_4);
//        title1Image2_4 = (ImageView) findViewById(R.id.title_1_image_2_4);
//        title1Image3_4 = (ImageView) findViewById(R.id.title_1_image_3_4);
//        title2Image1_4 = (ImageView) findViewById(R.id.title_2_image_1_4);
//        title2Image2_4 = (ImageView) findViewById(R.id.title_2_image_2_4);
//        title2Image3_4 = (ImageView) findViewById(R.id.title_2_image_3_4);
//        questionImage1_4 = (ImageView) findViewById(R.id.question_image_1_4);
//        questionImage2_4 = (ImageView) findViewById(R.id.question_image_2_4);
//        questionImage3_4 = (ImageView) findViewById(R.id.question_image_3_4);
//        closeImage1_4 = (ImageView) findViewById(R.id.image_close_1_4);
//        closeImage2_4 = (ImageView) findViewById(R.id.image_close_2_4);
//
//        pageCont4.setVisibility(View.VISIBLE);
//    }
//
//    protected void initPageCont5() {
//        pageCont5 = (LinearLayout) findViewById(R.id.page_cont_5);
//        unhideCont_5 = (LinearLayout) findViewById(R.id.unhide_cont_5);
//        titleCont1_5 = (LinearLayout) findViewById(R.id.title_cont_1_5);
//        titleCont2_5 = (LinearLayout) findViewById(R.id.title_cont_2_5);
//        titleImagesCont1_5 = (LinearLayout) findViewById(R.id.title_images_cont_1_5);
//        titleImagesCont2_5 = (LinearLayout) findViewById(R.id.title_images_cont_2_5);
//        questionCont_5 = (LinearLayout) findViewById(R.id.question_cont_5);
//        questionImagesCont_5 = (LinearLayout) findViewById(R.id.question_images_cont_5);
//        spinnerCont_5 = (LinearLayout) findViewById(R.id.spinner_cont_5);
//        infoCont_5 = (LinearLayout) findViewById(R.id.info_cont_5);
//        tableCont_5 = (FrameLayout) findViewById(R.id.table_cont_5);
//        rvAnswers_5 = (RecyclerView) findViewById(R.id.answers_recyclerview_5);
//        rvScale_5 = (RecyclerView) findViewById(R.id.scale_recyclerview_5);
//        spinnerAnswers_5 = (Spinner) findViewById(R.id.answers_spinner_5);
//        tableLayout_5 = (AdaptiveTableLayout) findViewById(R.id.table_question_layout_5);
//        tvUnhide_5 = (TextView) findViewById(R.id.unhide_title_5);
//        tvTitle1_5 = (TextView) findViewById(R.id.title_1_5);
//        tvTitle2_5 = (TextView) findViewById(R.id.title_2_5);
//        tvTitleDesc1_5 = (TextView) findViewById(R.id.title_desc_1_5);
//        tvTitleDesc2_5 = (TextView) findViewById(R.id.title_desc_2_5);
//        tvQuestion_5 = (TextView) findViewById(R.id.question_5);
//        tvQuestionDesc_5 = (TextView) findViewById(R.id.question_desc_5);
//        infoText_5 = (WebView) findViewById(R.id.info_text_5);
//        title1Image1_5 = (ImageView) findViewById(R.id.title_1_image_1_5);
//        title1Image2_5 = (ImageView) findViewById(R.id.title_1_image_2_5);
//        title1Image3_5 = (ImageView) findViewById(R.id.title_1_image_3_5);
//        title2Image1_5 = (ImageView) findViewById(R.id.title_2_image_1_5);
//        title2Image2_5 = (ImageView) findViewById(R.id.title_2_image_2_5);
//        title2Image3_5 = (ImageView) findViewById(R.id.title_2_image_3_5);
//        questionImage1_5 = (ImageView) findViewById(R.id.question_image_1_5);
//        questionImage2_5 = (ImageView) findViewById(R.id.question_image_2_5);
//        questionImage3_5 = (ImageView) findViewById(R.id.question_image_3_5);
//        closeImage1_5 = (ImageView) findViewById(R.id.image_close_1_5);
//        closeImage2_5 = (ImageView) findViewById(R.id.image_close_2_5);
//
//        pageCont5.setVisibility(View.VISIBLE);
//    }

}
