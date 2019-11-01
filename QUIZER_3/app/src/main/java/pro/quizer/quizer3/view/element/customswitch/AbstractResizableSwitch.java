package pro.quizer.quizer3.view.element.customswitch;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Switch;

import pro.quizer.quizer3.utils.ResizableViewUtils;


public abstract class AbstractResizableSwitch extends Switch {

    public AbstractResizableSwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ResizableViewUtils.initTextSize(this, context, getDefaultFontSize());
    }

    public AbstractResizableSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        ResizableViewUtils.initTextSize(this, context, getDefaultFontSize());

    }

    public AbstractResizableSwitch(Context context) {
        super(context);
        ResizableViewUtils.initTextSize(this, context, getDefaultFontSize());
    }

    public abstract int getDefaultFontSize();
}
