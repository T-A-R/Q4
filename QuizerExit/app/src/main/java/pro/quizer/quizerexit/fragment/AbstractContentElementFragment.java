package pro.quizer.quizerexit.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.adapter.ContentElementsAdapter;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.OptionsModel;
import pro.quizer.quizerexit.utils.StringUtils;
import pro.quizer.quizerexit.utils.UiUtils;
import pro.quizer.quizerexit.view.SizeLimitScrollView;

public abstract class AbstractContentElementFragment extends BaseFragment {

    public static final double QUESTION_PANEL_VALUE = 0.5;

    private View mBackButton;
    private View mExitButton;
    private View mForwardButton;
    private Runnable mBackRunnable;
    private Runnable mExitRunnable;
    private Runnable mForwardRunnable;

    boolean mIsHidden = false;
    View mHeaderFrame;
    ImageView mHideIcon;
    TextView mElementText;
    TextView mElementDescriptionText;
    RecyclerView mContentsRecyclerView;
    TextView mDots;

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
    private SizeLimitScrollView mScrollView;
    private View.OnClickListener mShowHideClickListener;

    @Override
    public void onViewCreated(@NonNull final View pView, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(pView, savedInstanceState);

        mHeaderFrame = pView.findViewById(R.id.header_frame);
        mScrollView = pView.findViewById(R.id.question_panel);
        mHideIcon = pView.findViewById(R.id.hide_icon);
        mElementText = pView.findViewById(R.id.element_text);
        mElementDescriptionText = pView.findViewById(R.id.element_description_text);
        mContentsRecyclerView = pView.findViewById(R.id.contents_recycler_view);
        mDots = pView.findViewById(R.id.dots_line);

        mShowHideClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsHidden) {
                    mHideIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_close));
                    mScrollView.setVisibility(View.VISIBLE);
                    mDots.setVisibility(View.GONE);
                } else {
                    mHideIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_open));
                    mScrollView.setVisibility(View.GONE);
                    mDots.setVisibility(View.VISIBLE);
                }

                mIsHidden = !mIsHidden;
            }
        };

        mHideIcon.setOnClickListener(mShowHideClickListener);
        mDots.setOnClickListener(mShowHideClickListener);

        mExitButton = pView.findViewById(R.id.exit_btn);
        mBackButton = pView.findViewById(R.id.back_btn);
        mForwardButton = pView.findViewById(R.id.forward_btn);
        mBackRunnable = getBackRunnable();
        mExitRunnable = getExitRunnable();
        mForwardRunnable = getForwardRunnable();

        if (mBackButton != null) {
            mBackButton.setOnClickListener(mBackClickListener);
        }

        if (mExitButton != null) {
            mExitButton.setOnClickListener(mExitClickListener);
        }

        if (mForwardButton != null) {
            mForwardButton.setOnClickListener(mForwardClickListener);
        }
    }

    public void initHeader(final View pView) {
        final OptionsModel optionsModel = getOptions();

        final int questionPanelMaxSize = (int) (UiUtils.getDisplayHeight(getContext()) * QUESTION_PANEL_VALUE);
        mScrollView.setMaxHeight(questionPanelMaxSize);

        final String title = optionsModel.getTitle((BaseActivity) getContext());
        final String description = optionsModel.getDescription();
        final List<ElementModel> contents = getElementModel().getContents();

        if (contents != null && !contents.isEmpty()) {
            mContentsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            mContentsRecyclerView.setHasFixedSize(true);
            ContentElementsAdapter mAdapter = new ContentElementsAdapter(getContext(), contents);
            mContentsRecyclerView.setAdapter(mAdapter);
            mContentsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mContentsRecyclerView.setVisibility(View.GONE);
        }

        UiUtils.setTextOrHide(mElementText, title);
        UiUtils.setTextOrHide(mElementDescriptionText, description);

        if (mHeaderFrame != null) {
            if (StringUtils.isEmpty(title) && StringUtils.isEmpty(description)) {
                mHeaderFrame.setVisibility(View.GONE);
            } else {
                mHeaderFrame.setVisibility(View.VISIBLE);
            }
        }
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

    protected abstract OptionsModel getOptions();

    protected abstract ElementModel getElementModel();
}