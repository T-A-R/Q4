package pro.quizer.quizer3.view.element.button;

import android.content.Context;
import android.util.AttributeSet;

import static pro.quizer.quizer3.view.element.ResizableConstants.LARGE;

public class ResizableButton extends AbstractResizableButton {

    public ResizableButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ResizableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizableButton(Context context) {
        super(context);
    }

    @Override
    public int getDefaultFontSize() {
        return LARGE;
    }
}
