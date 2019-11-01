package pro.quizer.quizer3.view.element.customswitch;

import android.content.Context;
import android.util.AttributeSet;

import static pro.quizer.quizer3.view.element.ResizableConstants.SMALL;

public class ResizableSwitch extends AbstractResizableSwitch {

    public ResizableSwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ResizableSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizableSwitch(Context context) {
        super(context);
    }

    @Override
    public int getDefaultFontSize() {
        return SMALL;
    }
}
