package pro.quizer.quizerexit.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.adapter.ContentElementsAdapter;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.OptionsModel;
import pro.quizer.quizerexit.utils.StringUtils;
import pro.quizer.quizerexit.utils.UiUtils;
import pro.quizer.quizerexit.view.SizeLimitScrollView;

import static pro.quizer.quizerexit.fragment.BoxFragment.BUNDLE_IS_BUTTON_VISIBLE;

public abstract class AbstractContentElementFragment extends BaseFragment {

    public static final double QUESTION_PANEL_VALUE = 0.5;

    public static final String BUNDLE_IS_BUTTON_VISIBLE = "BUNDLE_IS_BUTTON_VISIBLE";
    public static final String BUNDLE_IS_FROM_DIALOG = "BUNDLE_IS_FROM_DIALOG";

    private View mBackButton;
    private View mExitButton;
    private View mForwardButton;
    private View mButtonsPanel;
    private Runnable mBackRunnable;
    private Runnable mExitRunnable;
    private Runnable mForwardRunnable;
    private BaseActivity mBaseActivity;

    boolean mIsHidden = false;
    View mHeaderFrame;
    ImageView mHideIcon;
    View mElementHeader;
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

        mButtonsPanel = pView.findViewById(R.id.button_panel);
        mBaseActivity = (BaseActivity) getContext();
        mHeaderFrame = pView.findViewById(R.id.header_frame);
        mScrollView = pView.findViewById(R.id.question_panel);
        mHideIcon = pView.findViewById(R.id.hide_icon);
        mElementHeader = pView.findViewById(R.id.element_header);
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

        if (mHideIcon != null) {
            mHideIcon.setOnClickListener(mShowHideClickListener);
        }

        if (mDots != null) {
            mDots.setOnClickListener(mShowHideClickListener);
        }

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

    public void handleButtonsVisibility() {
        if (mButtonsPanel != null) {
            mButtonsPanel.setVisibility(isButtonVisible() ? View.VISIBLE : View.GONE);
        }
    }

    public View getForwardButton() {
        return mForwardButton;
    }

    public void initHeader(final View pView) {
        final OptionsModel optionsModel = getOptions();

        final int questionPanelMaxSize = (int) (UiUtils.getDisplayHeight(getContext()) * QUESTION_PANEL_VALUE);
        mScrollView.setMaxHeight(questionPanelMaxSize);

        final String title = optionsModel.getTitle((BaseActivity) getContext(), getMap());
        final String description = optionsModel.getDescription();
        final List<ElementModel> contents = getElementModel().getContents();

        if (contents != null && !contents.isEmpty()) {
            mContentsRecyclerView.setLayoutManager(new GridLayoutManager(mBaseActivity, 2));
            mContentsRecyclerView.setHasFixedSize(true);
            ContentElementsAdapter mAdapter = new ContentElementsAdapter(getElementModel(), getElementModel(), mBaseActivity, contents);
            mContentsRecyclerView.setAdapter(mAdapter);
            mContentsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mContentsRecyclerView.setVisibility(View.GONE);
        }

        UiUtils.setTextOrHide(mElementText, title);
        UiUtils.setTextOrHide(mElementDescriptionText, description);

        if (mElementHeader != null) {
            if (StringUtils.isEmpty(title) && StringUtils.isEmpty(description)) {
                mElementHeader.setVisibility(View.GONE);
            } else {
                mElementHeader.setVisibility(View.VISIBLE);

                if (isFromDialog()) {
                    mElementText.setTextColor(Color.BLACK);
                    mElementHeader.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        }
    }

    public void processForward() {
        mForwardButton.performClick();
    }

    public void processBack() {
        mBackButton.performClick();
    }

    protected abstract boolean isFromDialog();

    protected abstract boolean isButtonVisible();

    protected abstract Runnable getForwardRunnable();

    protected abstract Runnable getBackRunnable();

    protected abstract Runnable getExitRunnable();

    protected abstract OptionsModel getOptions();

    protected abstract HashMap<Integer, ElementModel> getMap();

    protected abstract ElementModel getElementModel();
}