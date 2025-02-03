package pro.quizer.quizer3.view.element.editspinner;

import android.content.Context;
import android.util.AttributeSet;

import static pro.quizer.quizer3.view.element.ResizableConstants.SMALL;

public class ResizableEditSpinner extends AbstractResizableEditSpinner {

    public ResizableEditSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ResizableEditSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizableEditSpinner(Context context) {
        super(context);
    }

    @Override
    public int getDefaultFontSize() {
        return SMALL;
    }
}
