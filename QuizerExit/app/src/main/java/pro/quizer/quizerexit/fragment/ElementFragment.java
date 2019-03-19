package pro.quizer.quizerexit.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.NavigationCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.ElementSubtype;
import pro.quizer.quizerexit.model.ElementType;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.OptionsModel;
import pro.quizer.quizerexit.utils.CollectionUtils;
import pro.quizer.quizerexit.utils.DateUtils;

public class ElementFragment extends BaseFragment {

    public static final String BUNDLE_CURRENT_QUESTION = "BUNDLE_CURRENT_QUESTION";
    public static final String BUNDLE_CALLBACK = "BUNDLE_CALLBACK";
    public static final String BUNDLE_LOGIN_ADMIN = "BUNDLE_LOGIN_ADMIN";
    public static final String BUNDLE_TOKEN = "BUNDLE_TOKEN";
    public static final String BUNDLE_USER_ID = "BUNDLE_USER_ID";
    public static final String BUNDLE_USER_LOGIN = "BUNDLE_USER_LOGIN";
    public static final String BUNDLE_IS_PHOTO_QUESTIONNAIRE = "BUNDLE_IS_PHOTO_QUESTIONNAIRE";
    public static final String BUNDLE_PROJECT_ID = "BUNDLE_PROJECT_ID";
    public static final String BUNDLE_VIEW_ID = "BUNDLE_VIEW_ID";

    private OptionsModel mAttributes;
    private ElementModel mCurrentElement;
    private FragmentManager mFragmentManger;
    private NavigationCallback mCallback;
    private long mStartTime;

    private String mUserLogin = Constants.Strings.UNKNOWN;
    private String mLoginAdmin = Constants.Strings.UNKNOWN;
    private String mToken = Constants.Strings.UNKNOWN;
    private int mProjectId = 0;
    private int mUserId = 0;
    private boolean mIsPhotoQuestionnaire;
    private int mViewId;

    private NavigationCallback mNavigationCallback = new NavigationCallback() {

        @Override
        public void onForward(final int pNextRelativeId) {
            mCurrentElement.setEndTime(DateUtils.getCurrentTimeMillis());

            mCallback.onForward(pNextRelativeId);
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {

        }
    };

    public static Fragment newInstance(
            final int pViewId,
            @NonNull final ElementModel pElement,
            final NavigationCallback pCallback,
            final String pToken,
            final String pLoginAdmin,
            final int pUserId,
            final String pUserLogin,
            final boolean pIsPhotoQuestionnaire,
            final int pProjectId) {
        final ElementFragment fragment = new ElementFragment();

        final Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_CURRENT_QUESTION, pElement);
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
            mCurrentElement = (ElementModel) bundle.getSerializable(BUNDLE_CURRENT_QUESTION);
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
            showToast(getString(R.string.internal_app_error) + "1001");
        }
    }

    private boolean isScreenElement() {
        final String type = mCurrentElement.getType();
        final String subtype = mCurrentElement.getSubtype();

        if (ElementType.QUESTION.equals(type) && !ElementSubtype.FUNNEL.equals(subtype)) {
            return true;
        }

        if (ElementType.BOX.equals(type) && (ElementSubtype.ONESCREEN.equals(subtype) || ElementSubtype.TABLE.equals(subtype) || ElementSubtype.FUNNEL.equals(subtype))) {
            return true;
        }

        return false;
    }

    private void initView() {
        final List<ElementModel> subElements = mCurrentElement.getElements();
        final String elementType = mCurrentElement.getType();

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

        switch (elementType) {
            case ElementType.QUESTION:
                switch (mCurrentElement.getSubtype()) {
                    case ElementSubtype.LIST:
                        mCurrentElement.setQuestionShowing(true);

                        mFragmentManger.beginTransaction()
                                .add(mViewId, QuestionListFragment.newInstance(mCurrentElement, mNavigationCallback))
                                .commit();

                        break;
                    default:
                        showToast("Неизвестный тип элемента");

                        break;
                }

                break;
            case ElementType.BOX:
                switch (mCurrentElement.getSubtype()) {
                    case ElementSubtype.TABLE:
                        mFragmentManger.beginTransaction()
                                .add(mViewId, QuestionTableFragment.newInstance(mCurrentElement, mNavigationCallback))
                                .commit();

                        break;
                    case ElementSubtype.FUNNEL:
                        showToast("Funnel еще не готов.");

                        break;
                    case ElementSubtype.ONESCREEN:
                        mFragmentManger.beginTransaction()
                                .add(mViewId, InfoFragment.newInstance(mCurrentElement, mNavigationCallback))
                                .commit();

                        break;
                    default:
                        mFragmentManger.beginTransaction()
                                .add(mViewId, BoxFragment.newInstance(mCurrentElement, mNavigationCallback, mToken, mLoginAdmin, mUserId, mUserLogin, mIsPhotoQuestionnaire, mProjectId))
                                .commit();

                        break;
                }

                break;
            default:
                showToast("Неизвестный тип элемента");
        }
    }

    @Override
    public void onDestroyView() {
        mCallback.onHideFragment(mCurrentElement);
        super.onDestroyView();
    }
}