package pro.quizer.quizerexit.view.resizeble.edittext;

import android.content.Context;
import android.util.AttributeSet;

import pro.quizer.quizerexit.view.resizeble.ResizableViewUtils;
import pro.quizer.quizerexit.view.resizeble.button.AbstractResizableButton;

import static pro.quizer.quizerexit.view.resizable.ResizableConstants.SMALL;

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
