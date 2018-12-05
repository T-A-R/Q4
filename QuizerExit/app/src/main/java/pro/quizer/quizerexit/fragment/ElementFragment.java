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

public class ElementFragment extends BaseFragment implements NavigationCallback {

    public static final String BUNDLE_CURRENT_QUESTION = "BUNDLE_CURRENT_QUESTION";

    TextView mElementText;
    private AttributesModel mAttributes;
    private ElementModel mCurrentElement;
    private FragmentManager mFragmentManger;

    private View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View pView) {

        }
    };

    private View.OnClickListener mForwardClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View pView) {

        }
    };

    public static Fragment newInstance(@NonNull final ElementModel pElement) {
        final ElementFragment fragment = new ElementFragment();

        final Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_CURRENT_QUESTION, pElement);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
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
            mAttributes = mCurrentElement.getAttributes();

            initView(view);
        } else {
            showToast(getString(R.string.internal_app_error) + "1001");
        }
    }

    private void initView(final View pView) {
        final View mBackButton = pView.findViewById(R.id.back_btn);
        final View mForwardButton = pView.findViewById(R.id.forward_btn);
        mBackButton.setOnClickListener(mBackClickListener);
        mForwardButton.setOnClickListener(mForwardClickListener);

        mElementText = pView.findViewById(R.id.element_text);
        mElementText.setText(mAttributes.getTitle());

        switch (mCurrentElement.getType()) {
            case ElementType.QUESTION:
                mFragmentManger.beginTransaction()
                        .add(R.id.content_element, QuestionFragment.newInstance(mCurrentElement, this))
                        .commit();

                break;
            default:
                showToast("Неизвестный тип элемента");
        }
    }

    @Override
    public void onForward(final ElementModel pElementModel) {
//        final StringBuilder sb = new StringBuilder();
//
//        for (int index = 0; index < pSubElements.size(); index++) {
//            final ElementModel model = pSubElements.get(index);
//
//            sb.append(model.getAttributes().getTitle()).append("\n");
//        }
//
//        showToastMessage(sb.toString());
//
//        showNextElement(pNextElement);
    }

    @Override
    public void onBack(ElementModel pElementModel) {

    }
}