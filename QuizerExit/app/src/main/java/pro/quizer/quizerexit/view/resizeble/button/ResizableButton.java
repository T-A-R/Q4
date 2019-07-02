package pro.quizer.quizerexit.view.resizeble.button;

import android.content.Context;
import android.util.AttributeSet;

import pro.quizer.quizerexit.view.resizeble.ResizableViewUtils;

import static pro.quizer.quizerexit.view.resizable.ResizableConstants.LARGE;
import static pro.quizer.quizerexit.view.resizable.ResizableConstants.MEDIUM;
import static pro.quizer.quizerexit.view.resizable.ResizableConstants.SMALL;

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
