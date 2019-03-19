package pro.quizer.quizerexit.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import pro.quizer.quizerexit.R;

public class CustomRadioButton extends CustomCheckableButton {

    private Drawable mChecked;
    private Drawable mUnchecked;

    public CustomRadioButton(Context pContext) {
        super(pContext);
    }

    public CustomRadioButton(Context pContext, AttributeSet pAttrs) {
        super(pContext, pAttrs);
    }

    public CustomRadioButton(Context pContext, AttributeSet pAttrs, int pDefStyle) {
        super(pContext, pAttrs, pDefStyle);
    }

    @Override
    void initDrawables(final Context pContext) {
        mChecked = ContextCompat.getDrawable(pContext, R.drawable.radio_button_checked);
        mUnchecked = ContextCompat.getDrawable(pContext, R.drawable.radio_button_unchecked);
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