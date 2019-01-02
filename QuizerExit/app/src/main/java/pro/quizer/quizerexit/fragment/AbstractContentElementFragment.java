package pro.quizer.quizerexit.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import pro.quizer.quizerexit.R;

public abstract class AbstractContentElementFragment extends BaseFragment {

    private View mBackButton;
    private View mExitButton;
    private View mForwardButton;
    private Runnable mBackRunnable;
    private Runnable mExitRunnable;
    private Runnable mForwardRunnable;

    private final View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            mBackRunnable.run();
        }
    };

    private final View.OnClickListener mExitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            mExitRunnable.run();
        }
    };

    private final View.OnClickListener mForwardClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            mForwardRunnable.run();
        }
    };

    @Override
    public void onViewCreated(@NonNull final View pView, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(pView, savedInstanceState);

        mExitButton = pView.findViewById(R.id.exit_btn);
        mBackButton = pView.findViewById(R.id.back_btn);
        mForwardButton = pView.findViewById(R.id.forward_btn);
        mBackRunnable = getBackRunnable();
        mExitRunnable = getExitRunnable();
        mForwardRunnable = getForwardRunnable();

        mBackButton.setOnClickListener(mBackClickListener);
        mExitButton.setOnClickListener(mExitClickListener);
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

    protected abstract Runnable getExitRunnable();
}