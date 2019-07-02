package pro.quizer.quizerexit.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.NavigationCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.ElementSubtype;
import pro.quizer.quizerexit.model.ElementType;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.OptionsModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.utils.CollectionUtils;
import pro.quizer.quizerexit.utils.DateUtils;

public class ElementFragment extends BaseFragment {

    public static final String BUNDLE_IS_FROM_DIALOG = "BUNDLE_IS_FROM_DIALOG";
    public static final String BUNDLE_IS_BUTTON_VISIBLE = "BUNDLE_IS_BUTTON_VISIBLE";
    public static final String BUNDLE_USER = "BUNDLE_USER";
    public static final String BUNDLE_CURRENT_QUESTION = "BUNDLE_CURRENT_QUESTION";
    public static final String BUNDLE_MAP = "BUNDLE_MAP";
    public static final String BUNDLE_CALLBACK = "BUNDLE_CALLBACK";
    public static final String BUNDLE_LOGIN_ADMIN = "BUNDLE_LOGIN_ADMIN";
    public static final String BUNDLE_TOKEN = "BUNDLE_TOKEN";
    public static final String BUNDLE_USER_ID = "BUNDLE_USER_ID";
    public static final String BUNDLE_USER_LOGIN = "BUNDLE_USER_LOGIN";
    public static final String BUNDLE_IS_PHOTO_QUESTIONNAIRE = "BUNDLE_IS_PHOTO_QUESTIONNAIRE";
    public static final String BUNDLE_PROJECT_ID = "BUNDLE_PROJECT_ID";
    public static final String BUNDLE_VIEW_ID = "BUNDLE_VIEW_ID";

    private OptionsModel mAttributes;
    private HashMap<Integer, ElementModel> mMap;
    private ElementModel mCurrentElement;
    private FragmentManager mFragmentManger;
    private NavigationCallback mCallback;
    private UserModel mUser;
    private boolean mIsButtonsVisible = true;
    private long mStartTime;
    private boolean mIsFromDialog = false;

    private String mUserLogin = Constants.Strings.UNKNOWN;
    private String mLoginAdmin = Constants.Strings.UNKNOWN;
    private String mToken = Constants.Strings.UNKNOWN;
    private int mProjectId = 0;
    private int mUserId = 0;
    private boolean mIsPhotoQuestionnaire;
    private int mViewId;

    private NavigationCallback mNavigationCallback = new NavigationCallback() {

        @Override
        public void onForward(final int pNextRelativeId, final View forwardView) {
            mCurrentElement.setEndTime(DateUtils.getCurrentTimeMillis());

            mCallback.onForward(pNextRelativeId, forwardView);
        }

        @Override
        public void onBack() {
            mCurrentElement.setEndTime(DateUtils.getCurrentTimeMillis());

            mCallback.onBack();
        }

        @Override
        public void onExit() {
            mCurrentElement.setEndTime(DateUtils.getCurrentTimeMillis());

            mCallback.onExit();
        }

        @Override
        public void onShowFragment(final ElementModel pCurrentElement) {
            mCallback.onShowFragment(pCurrentElement);
        }

        @Override
        public void onHideFragment(final ElementModel pCurrentElement) {
            mCallback.onHideFragment(pCurrentElement);
        }

    };

    public static ElementFragment newInstance(
            final boolean pIsFromDialog,
            final boolean isButtonsVisible,
            final int pViewId,
            @NonNull final ElementModel pElement,
            final NavigationCallback pCallback,
            final String pToken,
            final String pLoginAdmin,
            final int pUserId,
            final String pUserLogin,
            final boolean pIsPhotoQuestionnaire,
            final int pProjectId,
            final UserModel user,
            final HashMap<Integer, ElementModel> pMap) {
        final ElementFragment fragment = new ElementFragment();

        final Bundle bundle = new Bundle();
        bundle.putBoolean(BUNDLE_IS_FROM_DIALOG, pIsFromDialog);
        bundle.putBoolean(BUNDLE_IS_BUTTON_VISIBLE, isButtonsVisible);
        bundle.putSerializable(BUNDLE_USER, user);
        bundle.putSerializable(BUNDLE_CURRENT_QUESTION, pElement);
        bundle.putSerializable(BUNDLE_MAP, pMap);
        bundle.putSerializable(BUNDLE_CALLBACK, pCallback);
        bundle.putString(BUNDLE_TOKEN, pToken);
        bundle.putInt(BUNDLE_USER_ID, pUserId);
        bundle.putString(BUNDLE_LOGIN_ADMIN, pLoginAdmin);
        bundle.putString(BUNDLE_USER_LOGIN, pUserLogin);
        bundle.putBoolean(BUNDLE_IS_PHOTO_QUESTIONNAIRE, pIsPhotoQuestionnaire);
        bundle.putInt(BUNDLE_PROJECT_ID, pProjectId);
        bundle.putInt(BUNDLE_VIEW_ID, pViewId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        mStartTime = DateUtils.getCurrentTimeMillis();

        return inflater.inflate(R.layout.fragment_element, container, false);
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
            mIsFromDialog = bundle.getBoolean(BUNDLE_IS_FROM_DIALOG, false);
            mIsButtonsVisible = bundle.getBoolean(BUNDLE_IS_BUTTON_VISIBLE, true);
            mUser = (UserModel) bundle.getSerializable(BUNDLE_USER);
            mCurrentElement = (ElementModel) bundle.getSerializable(BUNDLE_CURRENT_QUESTION);
            mMap = (HashMap<Integer, ElementModel>) bundle.getSerializable(BUNDLE_MAP);
            mCallback = (NavigationCallback) bundle.getSerializable(BUNDLE_CALLBACK);
            mAttributes = mCurrentElement.getOptions();
            mIsPhotoQuestionnaire = bundle.getBoolean(BUNDLE_IS_PHOTO_QUESTIONNAIRE);
            mUserLogin = bundle.getString(BUNDLE_USER_LOGIN);
            mProjectId = bundle.getInt(BUNDLE_PROJECT_ID);
            mViewId = bundle.getInt(BUNDLE_VIEW_ID);
            mLoginAdmin = bundle.getString(BUNDLE_LOGIN_ADMIN);
            mToken = bundle.getString(BUNDLE_TOKEN);
            mUserId = bundle.getInt(BUNDLE_USER_ID);

            initView();
        } else {
            showToast(getString(R.string.NOTIFICATION_INTERNAL_APP_ERROR) + "1001");
        }
    }

    private boolean isScreenElement() {
        final String type = mCurrentElement.getType();
        final String subtype = mCurrentElement.getSubtype();

        if (ElementType.QUESTION.equals(type) && !ElementSubtype.FUNNEL.equals(subtype)) {
            return true;
        }

        if (ElementType.BOX.equals(type) && (ElementSubtype.INFO.equals(subtype) || ElementSubtype.TABLE.equals(subtype) || ElementSubtype.FUNNEL.equals(subtype))) {
            return true;
        }

        return false;
    }

    private void initView() {
        final List<ElementModel> subElements = mCurrentElement.getElements();
        final String elementType = mCurrentElement.getType();
        final String elementSubType = mCurrentElement.getSubtype();

        if (isScreenElement()) {
            mCurrentElement.setScreenShowing(true);
            mCurrentElement.setStartTime(mStartTime);

            if (mIsPhotoQuestionnaire && mCurrentElement.getOptions().isTakePhoto()) {
                shotPicture(mLoginAdmin, mToken, mCurrentElement.getRelativeID(), mUserId, mProjectId, mUserLogin);
            }
        }

        if (mAttributes.isRotation()) {
            CollectionUtils.shuffleElements(mCurrentElement, subElements);
        }

        mCallback.onShowFragment(mCurrentElement);

        if (ElementType.QUESTION.equals(elementType)) {
            if (ElementSubtype.SELECT.equals(elementSubType) && !mIsFromDialog) {
                mCurrentElement.setQuestionShowing(true);

                mFragmentManger.beginTransaction()
                        .add(mViewId, QuestionSelectiveFragment.newInstance(
                                mIsButtonsVisible,
                                mCurrentElement,
                                mNavigationCallback,
                                mMap,
                                mToken,
                                mLoginAdmin,
                                mUserId,
                                mUserLogin,
                                mIsPhotoQuestionnaire,
                                mProjectId,
                                mUser))
                        .commit();
            } else if (ElementSubtype.SCALE.equals(elementSubType)
                    || ElementSubtype.LIST.equals(elementSubType)
                    || (ElementSubtype.SELECT.equals(elementSubType) && mIsFromDialog)) {
                mCurrentElement.setQuestionShowing(true);

                mFragmentManger.beginTransaction()
                        .add(mViewId, QuestionListFragment.newInstance(mIsFromDialog, mIsButtonsVisible, mUser, mCurrentElement, mNavigationCallback, mMap))
                        .commit();
            } else {
                showToast("Неизвестный тип элемента");
            }
        } else if (ElementType.INFO.equals(elementType)) {
            mFragmentManger.beginTransaction()
                    .add(mViewId, InfoFragment.newInstance(
                            mIsButtonsVisible,
                            mCurrentElement,
                            mNavigationCallback,
                            mMap))
                    .commit();
        } else if (ElementType.BOX.equals(elementType)) {
            switch (elementSubType) {
                case ElementSubtype.PAGE:
                    mFragmentManger.beginTransaction()
                            .add(mViewId, PageFragment.newInstance(
                                    mIsButtonsVisible,
                                    mCurrentElement,
                                    mNavigationCallback,
                                    mMap,
                                    mToken,
                                    mLoginAdmin,
                                    mUserId,
                                    mUserLogin,
                                    mIsPhotoQuestionnaire,
                                    mProjectId,
                                    mUser))
                            .commit();

                    break;
                case ElementSubtype.TABLE:
                    mFragmentManger.beginTransaction()
                            .add(mViewId, QuestionTableFragment.newInstance(
                                    mIsFromDialog,
                                    mIsButtonsVisible,
                                    mUser,
                                    mCurrentElement,
                                    mNavigationCallback,
                                    mMap))
                            .commit();

                    break;
                case ElementSubtype.FUNNEL:
                    showToast("Funnel еще не готов.");

                    break;
                default:
                    mFragmentManger.beginTransaction()
                            .add(mViewId, BoxFragment.newInstance(
                                    mIsButtonsVisible,
                                    mCurrentElement,
                                    mNavigationCallback,
                                    mToken,
                                    mLoginAdmin,
                                    mUserId,
                                    mUserLogin,
                                    mIsPhotoQuestionnaire,
                                    mProjectId,
                                    mUser,
                                    mMap))
                            .commit();

                    break;
            }
        } else {
            showToast("Неизвестный тип элемента");
        }
    }

    @Override
    public void onDestroyView() {
        mCallback.onHideFragment(mCurrentElement);
        super.onDestroyView();
    }
}