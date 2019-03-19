package pro.quizer.quizerexit.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import pro.quizer.quizerexit.R;

public class CustomCheckBox extends CustomCheckableButton {

    private Drawable mChecked;
    private Drawable mUnchecked;

    public CustomCheckBox(Context pContext) {
        super(pContext);
    }

    public CustomCheckBox(Context pContext, AttributeSet pAttrs) {
        super(pContext, pAttrs);
    }

    public CustomCheckBox(Context pContext, AttributeSet pAttrs, int pDefStyle) {
        super(pContext, pAttrs, pDefStyle);
    }

    @Override
    void initDrawables(final Context pContext) {
        mChecked = ContextCompat.getDrawable(pContext, R.drawable.checkbox_checked);
        mUnchecked = ContextCompat.getDrawable(pContext, R.drawable.checkbox_unchecked);
    }

    @Override
    Drawable getCheckedDrawable() {
        return mChecked;
    }

    @Override
    Drawable getUnCheckedDrawable() {
        return mUnchecked;
    }
}