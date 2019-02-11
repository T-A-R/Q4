package pro.quizer.quizerexit.view.resizeble.edittext;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import pro.quizer.quizerexit.view.resizeble.ResizableViewUtils;


public abstract class AbstractResizableEditText extends EditText {

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
