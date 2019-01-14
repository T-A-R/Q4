package pro.quizer.quizerexit.utils;

import java.util.HashMap;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.utils.Evaluator.TreeBooleanEvaluator;

public final class ConditionUtils {

    public static final int CAN_SHOW = -1;
    public static final int CANT_SHOW = -2;

    private static final String ELEMENT_INDEX = "$e.";
    private static final String START_STRING = "<# " + ELEMENT_INDEX;
    private static final String END_STRING = " #>";
    private static final String FORMAT_DIVIDER_DOT = ".";
    private static final String DISPLAY_CONDITION_DIVIDER = "else";
    private static final String IF = "if";
    public static final String FALSE = "F";
    public static final String TRUE = "T";
    public static final String SPACE = " ";
    public static final String RIGHT_BRACKET = "}";
    public static final String LEFT_BRACKET = "{";

    private static String[] getArrayElement(final String pCondition) {
        return pCondition.split("\\" + FORMAT_DIVIDER_DOT);
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

    public static int evaluateCondition(final String pShowCondition, final HashMap<Integer, ElementModel> pModel, final BaseActivity pBaseActivity) {
        if (StringUtils.isEmpty(pShowCondition)) {
            return CAN_SHOW;
        }

        final String[] conditionArray = pShowCondition.replaceAll(SPACE, Constants.Strings.EMPTY).split(DISPLAY_CONDITION_DIVIDER);

        final TreeBooleanEvaluator evaluator = new TreeBooleanEvaluator();

        for (final String conditionElement : conditionArray) {
            if (conditionElement.contains(IF)) {
                String condition = conditionElement.replace(IF, Constants.Strings.EMPTY);

                if (condition.contains(DisplayConditionType.SHOW)) {
                    condition = condition.replace(LEFT_BRACKET + DisplayConditionType.SHOW + RIGHT_BRACKET, Constants.Strings.EMPTY);
                    final boolean isCanShow = TreeBooleanEvaluator.evaluateBoolean(evaluator, formatCondition(condition, pModel, pBaseActivity));

                    if (isCanShow) {
                        return CAN_SHOW;
                    }
                } else if (condition.contains(DisplayConditionType.JUMP)) {
                    int jump = Integer.valueOf(condition.substring(condition.indexOf(DisplayConditionType.JUMP) + DisplayConditionType.JUMP.length(), condition.indexOf(RIGHT_BRACKET)));

                    condition = condition.replace(LEFT_BRACKET + DisplayConditionType.JUMP + jump + RIGHT_BRACKET, Constants.Strings.EMPTY);
                    final boolean isNeedJump = TreeBooleanEvaluator.evaluateBoolean(evaluator, formatCondition(condition, pModel, pBaseActivity));

                    if (isNeedJump) {
                        return jump;
                    }
                } else if (condition.contains(DisplayConditionType.HIDE)) {
                    condition = condition.replace(LEFT_BRACKET + DisplayConditionType.HIDE + RIGHT_BRACKET, Constants.Strings.EMPTY);
                    final boolean isCantShow = TreeBooleanEvaluator.evaluateBoolean(evaluator, formatCondition(condition, pModel, pBaseActivity));

                    if (isCantShow) {
                        return CANT_SHOW;
                    }
                }
            } else if (conditionElement.contains(DisplayConditionType.SHOW)) {
                return CAN_SHOW;
            } else if (conditionElement.contains(DisplayConditionType.JUMP)){
                return Integer.valueOf(conditionElement.substring(conditionElement.indexOf(DisplayConditionType.JUMP) + DisplayConditionType.JUMP.length(), conditionElement.indexOf(RIGHT_BRACKET)));
            } else if (conditionElement.contains(DisplayConditionType.HIDE)) {
                return CANT_SHOW;
            }
        }

        return CAN_SHOW;
    }

    private static String getConditionString(final String pString) {
        final int startConditionIndex = pString.indexOf(ELEMENT_INDEX) + ELEMENT_INDEX.length();
        String condition = Constants.Strings.EMPTY;

        for (int i = startConditionIndex; i < pString.length(); i++) {
            final char currentChar = pString.charAt(i);
            if (Character.isLetter(currentChar) || Character.isDigit(currentChar) || FORMAT_DIVIDER_DOT.equals(String.valueOf(currentChar))) {
                condition += currentChar;
            } else {
                return condition;
            }
        }

        return condition;
    }

    private static String formatCondition(final String pExpression, final HashMap<Integer, ElementModel> pModel, final BaseActivity pBaseActivity) {
        if (StringUtils.isEmpty(pExpression) || (StringUtils.isNotEmpty(pExpression) && !pExpression.contains(ELEMENT_INDEX))) {
            return pExpression;
        }

        final String condition = getConditionString(pExpression);
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