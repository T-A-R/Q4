package pro.quizer.quizerexit.view.resizeble.customswitch;

import android.content.Context;
import android.util.AttributeSet;

import pro.quizer.quizerexit.view.resizeble.ResizableViewUtils;

import static pro.quizer.quizerexit.view.resizable.ResizableConstants.SMALL;
import static pro.quizer.quizerexit.view.resizable.ResizableConstants.SMALL;

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
