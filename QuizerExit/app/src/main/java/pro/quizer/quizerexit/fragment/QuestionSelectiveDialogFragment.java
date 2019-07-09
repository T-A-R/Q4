package pro.quizer.quizerexit.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.HashMap;
import java.util.List;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.DialogCallback;
import pro.quizer.quizerexit.NavigationCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.ElementSubtype;
import pro.quizer.quizerexit.model.ElementType;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.OptionsModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.utils.CollectionUtils;
import pro.quizer.quizerexit.utils.DateUtils;

public class QuestionSelectiveDialogFragment extends BaseFragment {

    public static final String BUNDLE_IS_FROM_DIALOG = "BUNDLE_IS_FROM_DIALOG";
    public static final String BUNDLE_IS_BUTTON_VISIBLE = "BUNDLE_IS_BUTTON_VISIBLE";
    public static final String BUNDLE_USER = "BUNDLE_USER";
    public static final String BUNDLE_CURRENT_QUESTION = "BUNDLE_CURRENT_QUESTION";
    public static final String BUNDLE_MAP = "BUNDLE_MAP";
    public static final String BUNDLE_DIALOG_CALLBACK = "BUNDLE_DIALOG_CALLBACK";
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
    private DialogCallback mDialogCallback;
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

    public static QuestionSelectiveDialogFragment newInstance(
            final boolean pIsFromDialog,
            final boolean isButtonsVisible,
            final int pViewId,
            @NonNull final ElementModel pElement,
            final NavigationCallback pCallback,
            final DialogCallback pDialogCallback,
            final String pToken,
            final String pLoginAdmin,
            final int pUserId,
            final String pUserLogin,
            final boolean pIsPhotoQuestionnaire,
            final int pProjectId,
            final UserModel user,
            final HashMap<Integer, ElementModel> pMap) {
        final QuestionSelectiveDialogFragment fragment = new QuestionSelectiveDialogFragment();

        final Bundle bundle = new Bundle();
        bundle.putBoolean(BUNDLE_IS_FROM_DIALOG, pIsFromDialog);
        bundle.putBoolean(BUNDLE_IS_BUTTON_VISIBLE, isButtonsVisible);
        bundle.putSerializable(BUNDLE_USER, user);
        bundle.putSerializable(BUNDLE_CURRENT_QUESTION, pElement);
//        bundle.putSerializable(BUNDLE_MAP, pMap);
        bundle.putSerializable(BUNDLE_CALLBACK, pCallback);
        bundle.putSerializable(BUNDLE_DIALOG_CALLBACK, pDialogCallback);
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
        return inflater.inflate(R.layout.alert_selective_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Bundle bundle = getArguments();

        if (bundle != null) {
            mIsFromDialog = bundle.getBoolean(BUNDLE_IS_FROM_DIALOG, false);
            mIsButtonsVisible = bundle.getBoolean(BUNDLE_IS_BUTTON_VISIBLE, true);
            mUser = (UserModel) bundle.getSerializable(BUNDLE_USER);
            mCurrentElement = (ElementModel) bundle.getSerializable(BUNDLE_CURRENT_QUESTION);
//            mMap = (HashMap<Integer, ElementModel>) bundle.getSerializable(BUNDLE_MAP);
            mMap = getBaseActivity().getMap();
            mCallback = (NavigationCallback) bundle.getSerializable(BUNDLE_CALLBACK);
            mDialogCallback = (DialogCallback) bundle.getSerializable(BUNDLE_DIALOG_CALLBACK);
            mAttributes = mCurrentElement.getOptions();
            mIsPhotoQuestionnaire = bundle.getBoolean(BUNDLE_IS_PHOTO_QUESTIONNAIRE);
            mUserLogin = bundle.getString(BUNDLE_USER_LOGIN);
            mProjectId = bundle.getInt(BUNDLE_PROJECT_ID);
            mViewId = bundle.getInt(BUNDLE_VIEW_ID);
            mLoginAdmin = bundle.getString(BUNDLE_LOGIN_ADMIN);
            mToken = bundle.getString(BUNDLE_TOKEN);
            mUserId = bundle.getInt(BUNDLE_USER_ID);

            initView(view);
        } else {
            showToast(getString(R.string.NOTIFICATION_INTERNAL_APP_ERROR) + "1001");
        }
    }

    private void initView(final View view) {
        final View okBtn = view.findViewById(R.id.dialog_ok_button);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogCallback.update();
                getBaseActivity().onBackPressed();
            }
        });

        final FragmentTransaction fragmentTransaction = getFragmentManager()
                .beginTransaction()
                .replace(R.id.dialog_content_element,
                        ElementFragment.newInstance(
                                true,
                                false,
                                R.id.dialog_content_element,
                                mCurrentElement,
                                mCallback,
                                mToken,
                                mLoginAdmin,
                                mUserId,
                                mUserLogin,
                                mIsPhotoQuestionnaire,
                                mProjectId,
                                mUser,
                                mMap));


        fragmentTransaction.commit();
    }
}