package pro.quizer.quizerexit.view.resizeble;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.fragment.SettingsFragment;
import pro.quizer.quizerexit.model.FontSizeModel;

public class ResizableViewUtils {

    public static int SMALL = 16;
    public static int MEDIUM = 18;
    public static int LARGE = 20;

    public static void initTextSize(final TextView pTextView, final Context pContext, final int pDefFontSize) {
        final FontSizeModel fontSizeModel = SettingsFragment.FONT_SIZE_MODELS.get(((BaseActivity) pContext).getFontSizePosition());

        pTextView.setTextSize(pDefFontSize * fontSizeModel.getScale());
    }

    public static void initViewHeight(final ViewGroup pView, final Context pContext) {
        final int answerMargin = ((BaseActivity) pContext).getAnswerMargin();

        pView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, answerMargin));
    }
}
