package pro.quizer.quizerexit.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import pro.quizer.quizerexit.R;

public abstract class AbstractContentElementFragment extends BaseFragment {

    private View mBackButton;
    private View mForwardButton;
    private Runnable mBackRunnable;
    private Runnable mForwardRunnable;

    private View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mBackRunnable.run();
        }
    };

    private View.OnClickListener mForwardClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mForwardRunnable.run();
        }
    };

    @Override
    public void onViewCreated(@NonNull View pView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(pView, savedInstanceState);

        mBackButton = pView.findViewById(R.id.back_btn);
        mForwardButton = pView.findViewById(R.id.forward_btn);
        mBackRunnable = getBackRunnable();
        mForwardRunnable = getForwardRunnable();

        mBackButton.setOnClickListener(mBackClickListener);

        mForwardButton.setOnClickListener(mForwardClickListener);
    }

    public void processForward() {
        mForwardButton.performClick();
    }

    public void processBack() {
        mBackButton.performClick();
    }

    protected abstract Runnable getForwardRunnable();

    protected abstract Runnable getBackRunnable();
}