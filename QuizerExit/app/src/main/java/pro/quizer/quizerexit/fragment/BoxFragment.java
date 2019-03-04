package pro.quizer.quizerexit.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.NavigationCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.OptionsModel;

public class BoxFragment extends AbstractContentElementFragment {

    public static final String BUNDLE_CURRENT_QUESTION = "BUNDLE_CURRENT_QUESTION";
    public static final String BUNDLE_CALLBACK = "BUNDLE_CALLBACK";
    public static final String BUNDLE_LOGIN_ADMIN = "BUNDLE_LOGIN_ADMIN";
    public static final String BUNDLE_TOKEN = "BUNDLE_TOKEN";
    public static final String BUNDLE_USER_ID = "BUNDLE_USER_ID";
    public static final String BUNDLE_USER_LOGIN = "BUNDLE_USER_LOGIN";
    public static final String BUNDLE_IS_PHOTO_QUESTIONNAIRE = "BUNDLE_IS_PHOTO_QUESTIONNAIRE";
    public static final String BUNDLE_PROJECT_ID = "BUNDLE_PROJECT_ID";

    private OptionsModel mAttributes;
    private ElementModel mCurrentElement;
    private FragmentManager mFragmentManger;
    private NavigationCallback mCallback;
    private String mUserLogin = Constants.Strings.UNKNOWN;
    private String mLoginAdmin = Constants.Strings.UNKNOWN;
    private String mToken = Constants.Strings.UNKNOWN;
    private int mProjectId = 0;
    private int mUserId = 0;
    private boolean mIsPhotoQuestionnaire;

    public static Fragment newInstance(
            @NonNull final ElementModel pElement,
            final NavigationCallback pCallback,
            final String pToken,
            final String pLoginAdmin,
            final int pUserId,
            final String pUserLogin,
            final boolean pIsPhotoQuestionnaire,
            final int pProjectId) {
        final BoxFragment fragment = new BoxFragment();

        final Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_CURRENT_QUESTION, pElement);
        bundle.putSerializable(BUNDLE_CALLBACK, pCallback);
        bundle.putString(BUNDLE_TOKEN, pToken);
        bundle.putInt(BUNDLE_USER_ID, pUserId);
        bundle.putString(BUNDLE_LOGIN_ADMIN, pLoginAdmin);
        bundle.putString(BUNDLE_USER_LOGIN, pUserLogin);
        bundle.putBoolean(BUNDLE_IS_PHOTO_QUESTIONNAIRE, pIsPhotoQuestionnaire);
        bundle.putInt(BUNDLE_PROJECT_ID, pProjectId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_box, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Bundle bundle = getArguments();
        final Activity activity = getActivity();

        if (activity != null) {
            mFragmentManger = ((FragmentActivity) activity).getSupportFragmentManager();
        }

        if (bundle != null) {
            mCurrentElement = (ElementModel) bundle.getSerializable(BUNDLE_CURRENT_QUESTION);
            mCallback = (NavigationCallback) bundle.getSerializable(BUNDLE_CALLBACK);
            mAttributes = mCurrentElement.getOptions();
            mIsPhotoQuestionnaire = bundle.getBoolean(BUNDLE_IS_PHOTO_QUESTIONNAIRE);
            mUserLogin = bundle.getString(BUNDLE_USER_LOGIN);
            mProjectId = bundle.getInt(BUNDLE_PROJECT_ID);
            mLoginAdmin = bundle.getString(BUNDLE_LOGIN_ADMIN);
            mToken = bundle.getString(BUNDLE_TOKEN);
            mUserId = bundle.getInt(BUNDLE_USER_ID);

            initHeader(view);
            initView();
        } else {
            showToast(getString(R.string.internal_app_error) + "1001");
        }
    }

    @Override
    protected Runnable getForwardRunnable() {
        return null;
    }

    @Override
    protected Runnable getBackRunnable() {
        return null;
    }

    @Override
    protected Runnable getExitRunnable() {
        return null;
    }

    @Override
    protected OptionsModel getOptions() {
        return mAttributes;
    }

    private void initView() {
        mFragmentManger.beginTransaction()
                .add(R.id.content_element_box,
                        ElementFragment.newInstance(
                                R.id.content_element_box,
                                mCurrentElement.getElements().get(0),
                                mCallback,
                                mToken,
                                mLoginAdmin,
                                mUserId,
                                mUserLogin,
                                mIsPhotoQuestionnaire,
                                mProjectId))
                .commit();
    }
}