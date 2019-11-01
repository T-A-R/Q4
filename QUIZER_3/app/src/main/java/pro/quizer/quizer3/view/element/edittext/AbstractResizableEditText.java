package pro.quizer.quizer3.view.element.edittext;

import android.content.Context;
import android.util.AttributeSet;

import pro.quizer.quizer3.utils.ResizableViewUtils;


public abstract class AbstractResizableEditText extends ExtendedEditText {

    public AbstractResizableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ResizableViewUtils.initTextSize(this, context, getDefaultFontSize());
    }

    public AbstractResizableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        ResizableViewUtils.initTextSize(this, context, getDefaultFontSize());

    }

    public AbstractResizableEditText(Context context) {
        super(context);
        ResizableViewUtils.initTextSize(this, context, getDefaultFontSize());
    }

    public abstract int getDefaultFontSize();
}
