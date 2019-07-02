package pro.quizer.quizerexit.view.resizeble.textview;

import android.content.Context;
import android.util.AttributeSet;

import static pro.quizer.quizerexit.view.resizable.ResizableConstants.LARGE;

public class LargeResizableTextView extends AbstractResizableTextView {

    public LargeResizableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LargeResizableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LargeResizableTextView(Context context) {
        super(context);
    }

    @Override
    public int getDefaultFontSize() {
        return LARGE;
    }
}
