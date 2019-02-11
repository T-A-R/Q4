package pro.quizer.quizerexit.view.resizeble.textview;

import android.content.Context;
import android.util.AttributeSet;

import pro.quizer.quizerexit.view.resizeble.ResizableViewUtils;


public abstract class AbstractResizableTextView extends android.support.v7.widget.AppCompatTextView {

    public AbstractResizableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ResizableViewUtils.initTextSize(this, context, getDefaultFontSize());
    }

    public AbstractResizableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ResizableViewUtils.initTextSize(this, context, getDefaultFontSize());

    }

    public AbstractResizableTextView(Context context) {
        super(context);
        ResizableViewUtils.initTextSize(this, context, getDefaultFontSize());
    }

    public abstract int getDefaultFontSize();
}
