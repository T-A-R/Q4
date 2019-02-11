package pro.quizer.quizerexit.view.resizeble.editspinner;

import android.content.Context;
import android.util.AttributeSet;

import com.reginald.editspinner.EditSpinner;

import pro.quizer.quizerexit.view.resizeble.ResizableViewUtils;


public abstract class AbstractResizableEditSpinner extends EditSpinner {

    public AbstractResizableEditSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ResizableViewUtils.initTextSize(this, context, getDefaultFontSize());
    }

    public AbstractResizableEditSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        ResizableViewUtils.initTextSize(this, context, getDefaultFontSize());

    }

    public AbstractResizableEditSpinner(Context context) {
        super(context);
        ResizableViewUtils.initTextSize(this, context, getDefaultFontSize());
    }

    public abstract int getDefaultFontSize();
}
