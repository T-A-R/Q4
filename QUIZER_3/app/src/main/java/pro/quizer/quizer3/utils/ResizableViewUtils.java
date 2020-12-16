package pro.quizer.quizer3.utils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.view.element.ResizableConstants;
import pro.quizer.quizer3.model.FontSizeModel;

public class ResizableViewUtils {

    public static void initTextSize(final TextView pTextView, final Context pContext, final int pDefFontSize) {
        final FontSizeModel fontSizeModel = Fonts.FONT_SIZE_MODELS.get(((MainActivity) pContext).getFontSizePosition());
        float textSize = MainActivity.AVIA ? (float) (fontSizeModel.getScale() * 0.8) : fontSizeModel.getScale();

        switch (pDefFontSize) {
            case ResizableConstants.SMALL:
                pTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, pContext.getResources().getDimensionPixelSize(R.dimen._5sdp) * (MainActivity.AVIA ? (float) (textSize *1.1) : textSize));
                break;
            case ResizableConstants.MEDIUM:
                pTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, pContext.getResources().getDimensionPixelSize(R.dimen._6sdp) * textSize);
                break;
            case ResizableConstants.LARGE:
                pTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, pContext.getResources().getDimensionPixelSize(R.dimen._8sdp) * textSize);
                break;
        }
    }

    public static void initViewHeight(final ViewGroup pView, final Context pContext) {
        final int answerMargin = ((MainActivity) pContext).getAnswerMargin();

        pView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) UiUtils.dpToPx(pContext, answerMargin)));
    }
}
