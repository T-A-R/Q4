package pro.quizer.quizerexit.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import java.io.Serializable;

import pro.quizer.quizerexit.R;

public abstract class CustomCheckableButton extends RelativeLayout implements Serializable {

    AppCompatImageView mCheckBoxImage;
    private Context mContext;
    private boolean mIsChecked = false;
    private boolean mIsEnabled = true;

    public void performClickProgramatically() {
        mOnClickListener.onClick(this);
    }

    private View.OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (setChecked(!isChecked(), true)) {
                if (mInternalOnClickListener != null) {
                    mInternalOnClickListener.onClick(CustomCheckableButton.this);
                }
            }
        }
    };

    private View.OnClickListener mInternalOnClickListener;

    public CustomCheckableButton(final Context pContext) {
        super(pContext);
        init();
    }

    public CustomCheckableButton(final Context pContext, final AttributeSet pAttrs) {
        super(pContext, pAttrs);
        init();
    }

    public CustomCheckableButton(final Context pContext, final AttributeSet pAttrs, final int pDefStyle) {
        super(pContext, pAttrs, pDefStyle);
        init();
    }

    private void inflate() {
        inflate(getContext(), R.layout.view_radio_button, this);

        initViews();
    }

    private void init() {
        inflate();
    }

    private void initViews() {
        mContext = getContext();

        mCheckBoxImage = findViewById(R.id.checkbox_image);

        setEnabled(mIsEnabled);

        mCheckBoxImage.setOnClickListener(mOnClickListener);

        initDrawables(mContext);
    }

    abstract void initDrawables(final Context pContext);

    abstract Drawable getCheckedDrawable();

    abstract Drawable getUnCheckedDrawable();

    @Override
    public void setOnClickListener(@Nullable OnClickListener onClickListener) {
        mInternalOnClickListener = onClickListener;
    }

    public abstract boolean isUnselectedWithTap();

    public void setChecked(final boolean pIsChecked) {
        setChecked(pIsChecked, false);
    }

    public boolean setChecked(final boolean pIsChecked, final boolean pIsTouchAction) {
        if (!mIsEnabled) {
            return false;
        }

        if (!pIsChecked && pIsTouchAction && !isUnselectedWithTap()) {
            return false;
        }

        mIsChecked = pIsChecked;

        setEnabled(true);

        mCheckBoxImage.setImageDrawable(pIsChecked ? getCheckedDrawable() : getUnCheckedDrawable());

        return true;
    }

    public void setEnabled(final boolean pIsEnabled) {
        mIsEnabled = pIsEnabled;

        mCheckBoxImage.setColorFilter(ContextCompat.getColor(mContext, mIsEnabled ? (mIsChecked ? R.color.checkbox_selected_color : R.color.checkbox_enabled_color) : R.color.checkbox_disabled_color), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    public boolean isChecked() {
        return mIsChecked;
    }

}