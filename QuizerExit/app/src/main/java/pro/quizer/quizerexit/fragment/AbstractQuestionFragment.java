package pro.quizer.quizerexit.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import java.util.HashMap;
import java.util.List;

import pro.quizer.quizerexit.IAdapter;
import pro.quizer.quizerexit.NavigationCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.OptionsModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.utils.CollectionUtils;

public abstract class AbstractQuestionFragment extends AbstractContentElementFragment {

    public static final String BUNDLE_MAP = "BUNDLE_MAP";
    public static final String BUNDLE_CURRENT_QUESTION = "BUNDLE_CURRENT_QUESTION";
    public static final String BUNDLE_CALLBACK = "BUNDLE_CALLBACK";
    public static final String BUNDLE_USER = "BUNDLE_USER";

    public UserModel mUser;
    public boolean mIsFromDialog;
    private HashMap<Integer, ElementModel> mMap;
    private ElementModel mCurrentElement;
    private OptionsModel mAttributes;
    private NavigationCallback mCallback;
    private BaseActivity mBaseActivity;

    public static Bundle getBundle(final  boolean pIsFromDialog, final boolean pIsVisibleButton, final UserModel user, @NonNull final ElementModel pElement, final NavigationCallback pCallback, final HashMap<Integer, ElementModel> pMap) {
        final Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_CURRENT_QUESTION, pElement);
        bundle.putSerializable(BUNDLE_MAP, pMap);
        bundle.putSerializable(BUNDLE_CALLBACK, pCallback);
        bundle.putBoolean(BUNDLE_IS_BUTTON_VISIBLE, pIsVisibleButton);
        bundle.putSerializable(BUNDLE_USER, user);
        bundle.putBoolean(BUNDLE_IS_FROM_DIALOG, pIsFromDialog);

        return bundle;
    }

    private Runnable mRefreshRecyclerViewRunnable = new Runnable() {
        @Override
        public void run() {
            updateAdapter();
        }
    };

    private Runnable mForwardRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                mCallback.onForward(getAdapter().processNext(), getForwardButton());
            } catch (final Exception pE) {
                showToast(pE.getMessage());
            }
        }
    };

    abstract IAdapter getAdapter();

    abstract void createAdapter(final HashMap<Integer, ElementModel> pMap, final ElementModel pCurrentElement, final List<ElementModel> subElements, final int minAnswers, final int maxAnswers, final Runnable refreshRecyclerViewRunnable);

    private Runnable mBackRunnable = new Runnable() {
        @Override
        public void run() {
            mCallback.onBack();
        }
    };

    private Runnable mExitRunnable = new Runnable() {
        @Override
        public void run() {
            mCallback.onExit();
        }
    };

    abstract void updateAdapter();

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Bundle bundle = getArguments();

        if (bundle != null) {
            mBaseActivity = getBaseActivity();
            mCurrentElement = (ElementModel) bundle.getSerializable(BUNDLE_CURRENT_QUESTION);
            mMap = (HashMap<Integer, ElementModel>) bundle.getSerializable(BUNDLE_MAP);
            mCallback = (NavigationCallback) bundle.getSerializable(BUNDLE_CALLBACK);
            mUser = (UserModel) bundle.getSerializable(BUNDLE_USER);
            mIsFromDialog = bundle.getBoolean(BUNDLE_IS_FROM_DIALOG);
            mAttributes = mCurrentElement.getOptions();
            initHeader(view);
            initView();
        } else {
            showToast(getString(R.string.NOTIFICATION_INTERNAL_APP_ERROR) + "1001");
        }
    }

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

    public void initView() {
        final List<ElementModel> subElements = mCurrentElement.getElements();

        final int minAnswers;
        final int maxAnswers;

        // множественный выбор
        if (mAttributes.isPolyanswer()) {
            maxAnswers = mAttributes.getMaxAnswers();
            minAnswers = mAttributes.getMinAnswers();
        } else {
            maxAnswers = 1;
            minAnswers = 1;
        }

        // рандомная сортировка дочерних элементов
        if (mAttributes.isRotation()) {
            CollectionUtils.shuffleElements(mCurrentElement, subElements);
        }

        createAdapter(mMap, mCurrentElement, subElements, minAnswers, maxAnswers, mRefreshRecyclerViewRunnable);
    }
}