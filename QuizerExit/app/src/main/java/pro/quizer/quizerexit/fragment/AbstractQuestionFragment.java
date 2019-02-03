package pro.quizer.quizerexit.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.Collections;
import java.util.List;

import pro.quizer.quizerexit.IAdapter;
import pro.quizer.quizerexit.NavigationCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.config.OptionsModel;
import pro.quizer.quizerexit.model.config.ElementModel;

public abstract class AbstractQuestionFragment extends AbstractContentElementFragment {

    public static final String BUNDLE_CURRENT_QUESTION = "BUNDLE_CURRENT_QUESTION";
    public static final String BUNDLE_CALLBACK = "BUNDLE_CALLBACK";

    private ElementModel mCurrentElement;
    private OptionsModel mAttributes;
    private NavigationCallback mCallback;

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
                mCallback.onForward(getAdapter().processNext());
            } catch (final Exception pE) {
                showToast(pE.getMessage());
            }
        }
    };

    abstract IAdapter getAdapter();

    abstract void createAdapter(final ElementModel pCurrentElement, final List<ElementModel> subElements, final int minAnswers, final int maxAnswers, final Runnable refreshRecyclerViewRunnable);

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
            mCurrentElement = (ElementModel) bundle.getSerializable(BUNDLE_CURRENT_QUESTION);
            mCallback = (NavigationCallback) bundle.getSerializable(BUNDLE_CALLBACK);
            mAttributes = mCurrentElement.getOptions();


            // current
            initView(view);
        } else {
            showToast(getString(R.string.internal_app_error) + "1001");
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

    private void initView(final View pView) {
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
            shuffleAnswers(subElements);
        }

        createAdapter(mCurrentElement, subElements, minAnswers, maxAnswers, mRefreshRecyclerViewRunnable);
    }

    private void shuffleAnswers(final List<ElementModel> pList) {
        Collections.shuffle(pList);

        for (int i = 0; i < pList.size(); i++) {
            final ElementModel answer = pList.get(i);
            final OptionsModel attributes = answer.getOptions();
            final int realOrder = attributes.getOrder() - 1;

            if (attributes.isFixedOrder() && realOrder != i) {
                pList.remove(answer);
                pList.add(attributes.getOrder() - 1, answer);

                i = 0;
            }
        }
    }
}