package pro.quizer.quizer3.view.element.textview;

import android.content.Context;
import android.util.AttributeSet;

import static pro.quizer.quizer3.view.element.ResizableConstants.MEDIUM;

public class MediumResizableTextView extends AbstractResizableTextView {

    public MediumResizableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MediumResizableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MediumResizableTextView(Context context) {
        super(context);
    }

    @Override
    public int getDefaultFontSize() {
        return MEDIUM;
    }
}
