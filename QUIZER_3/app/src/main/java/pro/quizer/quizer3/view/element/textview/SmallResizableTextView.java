package pro.quizer.quizer3.view.element.textview;

import android.content.Context;
import android.util.AttributeSet;

import static pro.quizer.quizer3.view.element.ResizableConstants.SMALL;

public class SmallResizableTextView extends AbstractResizableTextView {

    public SmallResizableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SmallResizableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SmallResizableTextView(Context context) {
        super(context);
    }

    @Override
    public int getDefaultFontSize() {
        return SMALL;
    }
}
