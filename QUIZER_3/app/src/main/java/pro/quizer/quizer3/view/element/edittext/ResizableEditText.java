package pro.quizer.quizer3.view.element.edittext;

import android.content.Context;
import android.util.AttributeSet;

import static pro.quizer.quizer3.view.element.ResizableConstants.SMALL;

public class ResizableEditText extends AbstractResizableEditText {

    public ResizableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ResizableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizableEditText(Context context) {
        super(context);
    }

    @Override
    public int getDefaultFontSize() {
        return SMALL;
    }
}
