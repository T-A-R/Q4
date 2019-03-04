package pro.quizer.quizerexit.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pro.quizer.quizerexit.NavigationCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.adapter.InfoElementAdapter;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.OptionsModel;

public class InfoFragment extends AbstractContentElementFragment {

    public static final String BUNDLE_CURRENT_QUESTION = "BUNDLE_CURRENT_QUESTION";
    public static final String BUNDLE_CALLBACK = "BUNDLE_CALLBACK";

    private ElementModel mCurrentElement;
    private OptionsModel mAttributes;
    private NavigationCallback mCallback;

    private RecyclerView mRecyclerView;
    private InfoElementAdapter mAdapter;

    public static Fragment newInstance(@NonNull final ElementModel pElement, final NavigationCallback pCallback) {
        final Fragment fragment = new InfoFragment();

        final Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_CURRENT_QUESTION, pElement);
        bundle.putSerializable(BUNDLE_CALLBACK, pCallback);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Bundle bundle = getArguments();

        if (bundle != null) {
            mCurrentElement = (ElementModel) bundle.getSerializable(BUNDLE_CURRENT_QUESTION);
            mCallback = (NavigationCallback) bundle.getSerializable(BUNDLE_CALLBACK);
            mAttributes = mCurrentElement.getOptions();

            initView(view);
            initView(view);
        } else {
            showToast(getString(R.string.internal_app_error) + "1002");
        }
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
                mCallback.onForward(mCurrentElement.getOptions().getJump());
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

    private void initView(final View pView) {
        mRecyclerView = pView.findViewById(R.id.info_recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.setItemViewCacheSize(100);
//        mRecyclerView.setDrawingCacheEnabled(true);
//        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
//        mRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 100);

        mAdapter = new InfoElementAdapter(getContext(), mCurrentElement.getElements());
        mRecyclerView.setAdapter(mAdapter);
    }
}