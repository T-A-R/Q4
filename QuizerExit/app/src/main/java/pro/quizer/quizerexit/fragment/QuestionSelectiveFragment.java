package pro.quizer.quizerexit.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

import pro.quizer.quizerexit.DialogCallback;
import pro.quizer.quizerexit.NavigationCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.OptionsOpenType;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.OptionsModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.utils.UiUtils;

public class QuestionSelectiveFragment extends AbstractContentElementFragment {

    public static final String BUNDLE_CURRENT_QUESTION = "BUNDLE_CURRENT_QUESTION";
    public static final String BUNDLE_CALLBACK = "BUNDLE_CALLBACK";
    public static final String BUNDLE_MAP = "BUNDLE_MAP";

    public static final String BUNDLE_TOKEN = "BUNDLE_TOKEN";
    public static final String BUNDLE_LOGIN_ADMIN = "BUNDLE_LOGIN_ADMIN";
    public static final String BUNDLE_USER_ID = "BUNDLE_USER_ID";
    public static final String BUNDLE_USER_LOGIN = "BUNDLE_USER_LOGIN";
    public static final String BUNDLE_IS_PHOTO_QUESTIONNAIRE = "BUNDLE_IS_PHOTO_QUESTIONNAIRE";
    public static final String BUNDLE_PROJECT_ID = "BUNDLE_PROJECT_ID";
    public static final String BUNDLE_USER = "BUNDLE_USER";

    private FragmentManager mFragmentManager;
    private ElementModel mCurrentElement;
    private OptionsModel mAttributes;
    private HashMap<Integer, ElementModel> mMap;
    private NavigationCallback mCallback;
    private DialogCallback mDialogCallback = new DialogCallback() {
        @Override
        public void update() {
            updateAnswers();
        }
    };
    private BaseActivity baseActivity;
    private boolean mIsButtonVisible;
    private String mToken;
    private String mLoginAdmin;
    private int mUserId;
    private String mUserLogin;
    private boolean mIsPhotoQuestionnaire;
    private int mProjectId;
    private UserModel mUser;

    public static Fragment newInstance(final boolean isButtonVisible,
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
        final QuestionSelectiveFragment fragment = new QuestionSelectiveFragment();

        final Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_CURRENT_QUESTION, pElement);
        bundle.putSerializable(BUNDLE_CALLBACK, pCallback);
        bundle.putBoolean(BUNDLE_IS_BUTTON_VISIBLE, isButtonVisible);
//        bundle.putSerializable(BUNDLE_MAP, pMap);
        bundle.putSerializable(BUNDLE_TOKEN, pToken);
        bundle.putSerializable(BUNDLE_LOGIN_ADMIN, pLoginAdmin);
        bundle.putSerializable(BUNDLE_USER_ID, pUserId);
        bundle.putSerializable(BUNDLE_USER_LOGIN, pUserLogin);
        bundle.putSerializable(BUNDLE_IS_PHOTO_QUESTIONNAIRE, pIsPhotoQuestionnaire);
        bundle.putSerializable(BUNDLE_PROJECT_ID, pProjectId);
        bundle.putSerializable(BUNDLE_USER, pUser);

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_selective, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Bundle bundle = getArguments();

        if (bundle != null) {
            mFragmentManager = getFragmentManager();
            mCurrentElement = (ElementModel) bundle.getSerializable(BUNDLE_CURRENT_QUESTION);
            mCallback = (NavigationCallback) bundle.getSerializable(BUNDLE_CALLBACK);
//            mMap = (HashMap<Integer, ElementModel>) bundle.getSerializable(BUNDLE_MAP);
            mMap = getBaseActivity().getMap();
            mAttributes = mCurrentElement.getOptions();
            baseActivity = (BaseActivity) getContext();
            mToken = (String) bundle.getSerializable(BUNDLE_TOKEN);
            mLoginAdmin = (String) bundle.getSerializable(BUNDLE_LOGIN_ADMIN);
            mUserId = (int) bundle.getSerializable(BUNDLE_USER_ID);
            mUserLogin = (String) bundle.getSerializable(BUNDLE_USER_LOGIN);
            mIsPhotoQuestionnaire = (boolean) bundle.getSerializable(BUNDLE_IS_PHOTO_QUESTIONNAIRE);
            mProjectId = (int) bundle.getSerializable(BUNDLE_PROJECT_ID);
            mUser = (UserModel) bundle.getSerializable(BUNDLE_USER);
            mIsButtonVisible = bundle.getBoolean(BUNDLE_IS_BUTTON_VISIBLE);

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
                mCallback.onForward(mCurrentElement.getOptions().getJump(), getForwardButton());
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

    private String formatAnswers() {
        final StringBuilder stringBuilder = new StringBuilder();

        for (final ElementModel model : mCurrentElement.getElements()) {
            if (model.isFullySelected()) {
                final OptionsModel options =model.getOptions();
                final String title = options.getTitle(baseActivity, mMap);
                final String openText = model.getTextAnswer();

                stringBuilder.append(title);

                if (!OptionsOpenType.CHECKBOX.equals(options.getOpenType())) {
                    stringBuilder.append(" [").append(openText).append("]");
                }

                stringBuilder.append(" ");
            }
        }

        return stringBuilder.toString();
    }

    private void updateAnswers() {
        final View view = getView();

        if (view != null) {
            final TextView answer = view.findViewById(R.id.selective_answer_answer);
            answer.setText(formatAnswers());
        }
    }

    private void initView(final View pView) {
        final TextView header = pView.findViewById(R.id.selective_answer_header);
        final TextView description = pView.findViewById(R.id.selective_answer_description);
        final View frame = pView.findViewById(R.id.selective_answer_frame);
        final String headerString = mCurrentElement.getOptions().getTitle(baseActivity, mMap);

        updateAnswers();

        header.setText(headerString);
        UiUtils.setTextOrHide(description, mCurrentElement.getOptions().getDescription());
        frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentTransaction fragmentTransaction = mFragmentManager
                        .beginTransaction()
                        .add(android.R.id.content,
                                QuestionSelectiveDialogFragment.newInstance(
                                        true,
                                        false,
                                        android.R.id.content,
                                        mCurrentElement,
                                        mCallback,
                                        mDialogCallback,
                                        mToken,
                                        mLoginAdmin,
                                        mUserId,
                                        mUserLogin,
                                        mIsPhotoQuestionnaire,
                                        mProjectId,
                                        mUser,
                                        mMap));

                    fragmentTransaction
                            .addToBackStack(QuestionSelectiveDialogFragment.class.getName());

                fragmentTransaction.commit();
            }
        });
//        new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme).setTitle("test").setMessage("sdafsdgsd").create().show();
//        RecyclerView mRecyclerView = pView.findViewById(R.id.info_recycler_view);
//
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        mRecyclerView.setHasFixedSize(true);
//        ContentElementsAdapter mAdapter = new ContentElementsAdapter((BaseActivity) getContext(), mCurrentElement.getElements());
//        mRecyclerView.setAdapter(mAdapter);
    }
}