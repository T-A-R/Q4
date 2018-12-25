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
import android.widget.TextView;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.NavigationCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.AttributeType;
import pro.quizer.quizerexit.model.ElementType;
import pro.quizer.quizerexit.model.config.AttributesModel;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.utils.DateUtils;

public class ElementFragment extends BaseFragment {

    public static final String BUNDLE_CURRENT_QUESTION = "BUNDLE_CURRENT_QUESTION";
    public static final String BUNDLE_CALLBACK = "BUNDLE_CALLBACK";
    public static final String BUNDLE_LOGIN_ADMIN = "BUNDLE_LOGIN_ADMIN";
    public static final String BUNDLE_TOKEN = "BUNDLE_TOKEN";
    public static final String BUNDLE_USER_ID = "BUNDLE_USER_ID";
    public static final String BUNDLE_USER_LOGIN = "BUNDLE_USER_LOGIN";
    public static final String BUNDLE_PROJECT_ID = "BUNDLE_PROJECT_ID";

    TextView mElementText;
    TextView mElementDescriptionText;
    private AttributesModel mAttributes;
    private ElementModel mCurrentElement;
    private FragmentManager mFragmentManger;
    private NavigationCallback mCallback;
    private long mStartTime;

    private String mUserLogin = Constants.Strings.UNKNOWN;
    private String mLoginAdmin = Constants.Strings.UNKNOWN;
    private String mToken = Constants.Strings.UNKNOWN;
    private int mProjectId = 0;
    private int mUserId = 0;

    public static Fragment newInstance(
            @NonNull final ElementModel pElement,
            final NavigationCallback pCallback,
            final String pToken,
            final String pLoginAdmin,
            final int pUserId,
            final String pUserLogin,
            final int pProjectId) {
        final ElementFragment fragment = new ElementFragment();

        final Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_CURRENT_QUESTION, pElement);
        bundle.putSerializable(BUNDLE_CALLBACK, pCallback);
        bundle.putString(BUNDLE_TOKEN, pToken);
        bundle.putInt(BUNDLE_USER_ID, pUserId);
        bundle.putString(BUNDLE_LOGIN_ADMIN, pLoginAdmin);
        bundle.putString(BUNDLE_USER_LOGIN, pUserLogin);
        bundle.putInt(BUNDLE_PROJECT_ID, pProjectId);
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
            mUserLogin = bundle.getString(BUNDLE_USER_LOGIN);
            mProjectId = bundle.getInt(BUNDLE_PROJECT_ID);
            mLoginAdmin = bundle.getString(BUNDLE_LOGIN_ADMIN);
            mToken = bundle.getString(BUNDLE_TOKEN);
            mUserId = bundle.getInt(BUNDLE_USER_ID);

            initView(view);
        } else {
            showToast(getString(R.string.internal_app_error) + "1001");
        }
    }

    private void initView(final View pView) {
        mCallback.onShowFragment(mCurrentElement);

        if (mCurrentElement.getOptions().isTakePhoto()) {
            shotPicture(mLoginAdmin, mToken, mCurrentElement.getRelativeID(), mUserId, mProjectId, mUserLogin);
        }

        mElementText = pView.findViewById(R.id.element_text);
        mElementDescriptionText = pView.findViewById(R.id.element_description_text);
        mElementText.setText(mAttributes.getTitle());
        mElementDescriptionText.setText(mAttributes.getDescription());

        mCurrentElement.setShowing(true);
        mCurrentElement.setStartTime(mStartTime);

        switch (mCurrentElement.getType()) {
            case ElementType.QUESTION:
                switch (mCurrentElement.getOptions().getType()) {
                    case AttributeType.LIST:
                        mFragmentManger.beginTransaction()
                                .add(R.id.content_element, QuestionListFragment.newInstance(mCurrentElement, mCallback))
                                .commit();

                        break;
                    case AttributeType.TABLE:
                        mFragmentManger.beginTransaction()
                                .add(R.id.content_element, QuestionTableFragment.newInstance(mCurrentElement, mCallback))
                                .commit();

                        break;
                    default:
                        showToast("Неизвестный тип элемента");

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