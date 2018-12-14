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

import pro.quizer.quizerexit.NavigationCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.ElementType;
import pro.quizer.quizerexit.model.config.AttributesModel;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.utils.DateUtils;

public class ElementFragment extends BaseFragment {

    public static final String BUNDLE_CURRENT_QUESTION = "BUNDLE_CURRENT_QUESTION";
    public static final String BUNDLE_CALLBACK = "BUNDLE_CALLBACK";

    TextView mElementText;
    TextView mElementDescriptionText;
    private AttributesModel mAttributes;
    private ElementModel mCurrentElement;
    private FragmentManager mFragmentManger;
    private NavigationCallback mCallback;
    private long mStartTime;

    public static Fragment newInstance(@NonNull final ElementModel pElement, final NavigationCallback pCallback) {
        final ElementFragment fragment = new ElementFragment();

        final Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_CURRENT_QUESTION, pElement);
        bundle.putSerializable(BUNDLE_CALLBACK, pCallback);
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

            initView(view);
        } else {
            showToast(getString(R.string.internal_app_error) + "1001");
        }
    }

    private void initView(final View pView) {
        mElementText = pView.findViewById(R.id.element_text);
        mElementDescriptionText = pView.findViewById(R.id.element_description_text);
        mElementText.setText(mAttributes.getTitle());
        mElementDescriptionText.setText(mAttributes.getDescription());

        mCurrentElement.setShowing(true);
        mCurrentElement.setStartTime(mStartTime);

        switch (mCurrentElement.getType()) {
            case ElementType.QUESTION:
                mFragmentManger.beginTransaction()
                        .add(R.id.content_element, QuestionFragment.newInstance(mCurrentElement, mCallback))
                        .commit();

                break;
            default:
                showToast("Неизвестный тип элемента");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}