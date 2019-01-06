package pro.quizer.quizerexit.utils;

import java.util.HashMap;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.utils.Evaluator.TreeBooleanEvaluator;

public final class ConditionUtils {

    private static final String ELEMENT_INDEX = "$e.";
    private static final String START_STRING = "<# " + ELEMENT_INDEX;
    private static final String END_STRING = " #>";
    private static final String FORMAT_TITLE_DIVIDER = ".";
    private static final String DISPLAY_CONDITION_DIVIDER = "###";
    private static final String IF_ = "if ";
    public static final String FALSE = "F";
    public static final String TRUE = "T";
    public static final String SPACE = " ";

    private static String[] getArrayElement(final String pCondition) {
        return pCondition.split("\\" + FORMAT_TITLE_DIVIDER);
    }

    public static String formatTitle(final BaseActivity pBaseActivity, final String pTitle, final HashMap<Integer, ElementModel> pModel) {
        if (StringUtils.isEmpty(pTitle) || (StringUtils.isNotEmpty(pTitle) && !pTitle.contains(START_STRING))) {
            return pTitle;
        }

        final String condition = pTitle.substring(pTitle.indexOf(START_STRING) + START_STRING.length(), pTitle.indexOf(END_STRING));
        final String[] array = getArrayElement(condition);

        final Integer index = Integer.parseInt(array[0]);
        final String field = array[1];

        final ElementModel element = pModel.get(index);

        if (element == null) {
            return pTitle;
        }

        final String replaceString = getReplaceString(field, element, pBaseActivity);

        return formatTitle(pBaseActivity, pTitle.replace(START_STRING + condition + END_STRING, replaceString), pModel);
    }

    public static boolean isCanShow(final String pShowCondition, final HashMap<Integer, ElementModel> pModel, final BaseActivity pBaseActivity) {
        if (StringUtils.isEmpty(pShowCondition)) {
            return true;
        }

        final String[] conditionArray = pShowCondition.split(DISPLAY_CONDITION_DIVIDER);

        boolean isNeedShow = true;
        boolean isNeedNotShow = false;

        final TreeBooleanEvaluator evaluator = new TreeBooleanEvaluator();

        for (final String conditionElement : conditionArray) {
            String condition = conditionElement.replace(IF_, Constants.Strings.EMPTY);

            if (condition.contains(DisplayConditionType.SHOW)) {
                condition = condition.replace(DisplayConditionType.SHOW, Constants.Strings.EMPTY);
                isNeedShow = TreeBooleanEvaluator.evaluateBoolean(evaluator, formatCondition(condition, pModel, pBaseActivity));

            } else if (condition.contains(DisplayConditionType.NOT_SHOW)) {
                condition = condition.replace(DisplayConditionType.NOT_SHOW, Constants.Strings.EMPTY);
                isNeedNotShow = TreeBooleanEvaluator.evaluateBoolean(evaluator, formatCondition(condition, pModel, pBaseActivity));
            }
        }

        if (isNeedNotShow) {
            return false;
        }

        return isNeedShow;
    }

    private static String formatCondition(final String pExpression, final HashMap<Integer, ElementModel> pModel, final BaseActivity pBaseActivity) {
        if (StringUtils.isEmpty(pExpression) || (StringUtils.isNotEmpty(pExpression) && !pExpression.contains(ELEMENT_INDEX))) {
            return pExpression;
        }

        final String condition = pExpression.substring(pExpression.indexOf(ELEMENT_INDEX) + ELEMENT_INDEX.length(), pExpression.indexOf(SPACE));
        final String[] array = getArrayElement(condition);

        final Integer index = Integer.parseInt(array[0]);
        final String field = array[1];

        final ElementModel element = pModel.get(index);

        if (element == null) {
            return pExpression;
        }

        final String replaceString = getReplaceString(field, element, pBaseActivity);

        return formatCondition(pExpression.replace(ELEMENT_INDEX + condition, replaceString), pModel, pBaseActivity);
    }

    private static String getReplaceString(final String field, final ElementModel element, final BaseActivity pBaseActivity) {
        switch (field) {
            case ConditionType.TITLE:
                return element.getOptions().getTitle(pBaseActivity);
            case ConditionType.VALUE:
                return element.isFullySelected() ? element.getTextAnswer() : Constants.Strings.EMPTY;
            case ConditionType.CHECKED:
                return element.isFullySelected() ? TRUE : FALSE;
            default:
                return Constants.Strings.EMPTY;
        }
    }
}