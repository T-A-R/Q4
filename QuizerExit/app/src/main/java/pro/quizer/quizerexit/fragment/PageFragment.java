package pro.quizer.quizerexit.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

import pro.quizer.quizerexit.NavigationCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.adapter.PageElementsAdapter;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.OptionsModel;
import pro.quizer.quizerexit.model.database.UserModel;

public class PageFragment extends AbstractContentElementFragment {

    public static final String BUNDLE_CURRENT_QUESTION = "BUNDLE_CURRENT_QUESTION";
    public static final String BUNDLE_CALLBACK = "BUNDLE_CALLBACK";
    public static final String BUNDLE_MAP = "BUNDLE_MAP";

    public static final String BUNDLE_TOKEN = "BUNDLE_TOKEN";
    public static final String BUNDLE_LOGIN_ADMIN = "BUNDLE_LOGIN_ADMIN";
    public static final String BUNDLE_USER_ID = "BUNDLE_USER_ID";
    public static final String BUNDLE_USER_LOGIN = "BUNDLE_USER_LOGIN";
    public static final String BUNDLE_IS_PHOTO_Q = "BUNDLE_IS_PHOTO_Q";
    public static final String BUNDLE_PROJECT_ID = "BUNDLE_PROJECT_ID";
    public static final String BUNDLE_USER = "BUNDLE_USER";

    private ElementModel mCurrentElement;
    private OptionsModel mAttributes;
    private HashMap<Integer, ElementModel> mMap;
    private NavigationCallback mCallback;

    private String mToken;
    private String mLoginAdmin;
    private int mUserId;
    private String mUserLogin;
    private boolean mIsPhotoQuestionnaire;
    private int mProjectId;
    private UserModel mUser;
    private FragmentManager fragmentManager;
    private boolean mIsButtonVisible;
    private PageElementsAdapter mAdapter;

    public static Fragment newInstance(
            final boolean isButtonVisible,
            @NonNull final ElementModel pElement,
            final NavigationCallback pCallback,
            final HashMap<Integer, ElementModel> pMap,
            final String pToken,
            final String pLoginAdmin,
            final int pUserId,
            final String pUserLogin,
            final boolean pIsPhotoQuestionnaire,
            final int pProjectId,
            final UserModel pUser) {
        final Fragment fragment = new PageFragment();

        final Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_CURRENT_QUESTION, pElement);
        bundle.putSerializable(BUNDLE_CALLBACK, pCallback);
//        bundle.putSerializable(BUNDLE_MAP, pMap);

        bundle.putSerializable(BUNDLE_TOKEN, pToken);
        bundle.putSerializable(BUNDLE_LOGIN_ADMIN, pLoginAdmin);
        bundle.putSerializable(BUNDLE_USER_ID, pUserId);
        bundle.putSerializable(BUNDLE_USER_LOGIN, pUserLogin);
        bundle.putSerializable(BUNDLE_IS_PHOTO_Q, pIsPhotoQuestionnaire);
        bundle.putSerializable(BUNDLE_PROJECT_ID, pProjectId);
        bundle.putBoolean(BUNDLE_IS_BUTTON_VISIBLE, isButtonVisible);
        bundle.putSerializable(BUNDLE_USER, pUser);


        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final BaseActivity baseActivity = (BaseActivity) getContext();
        final Bundle bundle = getArguments();

        if (bundle != null) {
            fragmentManager = baseActivity.getSupportFragmentManager();

            mCurrentElement = (ElementModel) bundle.getSerializable(BUNDLE_CURRENT_QUESTION);
            mCallback = (NavigationCallback) bundle.getSerializable(BUNDLE_CALLBACK);
            mMap = getBaseActivity().getMap();
//            mMap = (HashMap<Integer, ElementModel>) bundle.getSerializable(BUNDLE_MAP);
            mAttributes = mCurrentElement.getOptions();

            mToken = (String) bundle.getSerializable(BUNDLE_TOKEN);
            mLoginAdmin = (String) bundle.getSerializable(BUNDLE_LOGIN_ADMIN);
            mUserId = (int) bundle.getSerializable(BUNDLE_USER_ID);
            mUserLogin = (String) bundle.getSerializable(BUNDLE_USER_LOGIN);
            mIsPhotoQuestionnaire = (boolean) bundle.getSerializable(BUNDLE_IS_PHOTO_Q);
            mProjectId = (int) bundle.getSerializable(BUNDLE_PROJECT_ID);
            mUser = (UserModel) bundle.getSerializable(BUNDLE_USER);
            mIsButtonVisible = bundle.getBoolean(BUNDLE_IS_BUTTON_VISIBLE);

            initHeader(view);
            initView(view);

            handleButtonsVisibility();
        } else {
            showToast(getString(R.string.NOTIFICATION_INTERNAL_APP_ERROR) + "1002");
        }
    }

    @Override
    protected boolean isFromDialog() {
        return false;
    }

    @Override
    protected boolean isButtonVisible() {
        return mIsButtonVisible;
    }

    private final Runnable mBackRunnable = new Runnable() {
        @Override
        public void run() {
            mCallback.onBack();
        }
    };

    private final Runnable mExitRunnable = new Runnable() {
        @Override
        public void run() {
            mCallback.onExit();
        }
    };

    private final Runnable mForwardRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                mCallback.onForward(mAdapter.processNext(), getForwardButton());
            } catch (final Exception pE) {
                showToast(pE.getMessage());
            }
        }
    };

    @Override
    protected Runnable getForwardRunnable() {
        return mForwardRunnable;
    }

    @Override
    protected Runnable getBackRunnable() {
        return mBackRunnable;
    }

    @Override
    protected Runnable getExitRunnable() {
        return mExitRunnable;
    }

    @Override
    protected OptionsModel getOptions() {
        return mAttributes;
    }

    @Override
    protected HashMap<Integer, ElementModel> getMap() {
        return mMap;
    }

    @Override
    protected ElementModel getElementModel() {
        return mCurrentElement;
    }

    private void initView(final View pView) {
        RecyclerView mRecyclerView = pView.findViewById(R.id.page_recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new PageElementsAdapter(
                mCurrentElement,
                isButtonVisible(),
                (BaseActivity) getContext(),
                mCurrentElement.getElements(),
                mCallback,
                mToken,
                mLoginAdmin,
                mUserId,
                mUserLogin,
                mIsPhotoQuestionnaire,
                mProjectId,
                mUser,
                mMap,
                fragmentManager);
        mRecyclerView.setAdapter(mAdapter);
    }
}