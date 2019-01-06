package pro.quizer.quizerexit.utils;

import java.util.HashMap;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.config.ElementModel;

public final class ConditionUtils {

    private static final String START_STRING = "<# $e.";
    private static final String END_STRING = " #>";
    private static final String DIVIDER = ".";

    public static String formatTitle(final BaseActivity pBaseActivity, final String pTitle, final HashMap<Integer, ElementModel> pModel) {
        if (StringUtils.isEmpty(pTitle) || (StringUtils.isNotEmpty(pTitle) && !pTitle.contains(START_STRING))) {
            return pTitle;
        }

        final String condition = pTitle.substring(pTitle.indexOf(START_STRING) + START_STRING.length(), pTitle.indexOf(END_STRING));
        final String[] array = condition.split("\\" + DIVIDER);

        final Integer index = Integer.parseInt(array[0]);
        final String field = array[1];

        final ElementModel element = pModel.get(index);

        final String replaceString;

        switch (field) {
            case ConditionType.TITLE:
                replaceString = element.getOptions().getTitle(pBaseActivity);

                break;
            case ConditionType.VALUE:
                replaceString = element.getTextAnswer();

                break;
            default:
                replaceString = Constants.Strings.EMPTY;

                break;
        }

        return formatTitle(pBaseActivity, pTitle.replace(START_STRING + condition + END_STRING, replaceString), pModel);
    }

}