package pro.quizer.quizer3.utils;

import android.util.Log;

import java.util.HashMap;
import java.util.List;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.database.models.CurrentQuestionnaireR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.ElementPassedR;
import pro.quizer.quizer3.model.config.ElementModel;
import pro.quizer.quizer3.model.config.ElementModelNew;
import pro.quizer.quizer3.utils.Evaluator.TreeBooleanEvaluator;

import static pro.quizer.quizer3.MainActivity.TAG;

public final class ConditionUtils {

    public static final int CAN_SHOW = Integer.MIN_VALUE;
    public static final int CANT_SHOW = Integer.MAX_VALUE;

    private static final String ELEMENT_INDEX = "$e.";
    private static final String START_STRING = "<#";
    private static final String END_STRING = "#>";
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

    public static String formatTitle(final MainActivity pBaseActivity, final String pTitle, final HashMap<Integer, ElementModelNew> pMap) {
        if (StringUtils.isEmpty(pTitle) || (StringUtils.isNotEmpty(pTitle) && !pTitle.contains(START_STRING))) {
            return pTitle;
        }

        final String condition = pTitle.substring(pTitle.indexOf(START_STRING) + START_STRING.length(), pTitle.indexOf(END_STRING));
        final String conditionWithoutSpaces = condition.replace(SPACE, Constants.Strings.EMPTY);
        final String conditionWithoutElementIndex = conditionWithoutSpaces.replace(ELEMENT_INDEX, Constants.Strings.EMPTY);
        final String[] array = getArrayElement(conditionWithoutElementIndex);

        final Integer index = Integer.parseInt(array[0]);
        final String field = array[1];

        final ElementModelNew element = pMap.get(index);

        if (element == null) {
            return pTitle;
        }

        List<ElementPassedR> elementsPassed = null;
        try {
            final CurrentQuestionnaireR questionnaireR = pBaseActivity.getMainDao().getCurrentQuestionnaireR();
            final String token = questionnaireR.getToken();
            elementsPassed = pBaseActivity.getMainDao().getAllElementsPassedR(token);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ElementPassedR elementItemR = null;
        if (elementsPassed != null && elementsPassed.size() > 0) {
            for (ElementPassedR elementPassedR : elementsPassed) {
                if (elementPassedR.getRelative_id().equals(index)) {
                    elementItemR = elementPassedR;
                }
            }
        }

        final String replaceString = getReplaceString(field, element, elementItemR, pBaseActivity, pMap);

        return formatTitle(pBaseActivity, pTitle.replace(START_STRING + condition + END_STRING, replaceString), pMap);
    }

    public static int evaluateCondition(final String pShowCondition, final HashMap<Integer, ElementModelNew> pModel, final MainActivity pBaseActivity) {
        if (StringUtils.isEmpty(pShowCondition)) {
            Log.d(TAG, "evaluateCondition: 1");
            return CAN_SHOW;
        }

        final String[] conditionArray = pShowCondition.replaceAll(SPACE, Constants.Strings.EMPTY).split(DISPLAY_CONDITION_DIVIDER);

        final TreeBooleanEvaluator evaluator = new TreeBooleanEvaluator();

        Log.d(TAG, "************************************");
        for (int i = 0; i < conditionArray.length; i++) {
            Log.d(TAG, "evaluateCondition: " + i + " = " + conditionArray[i]);
        }
        Log.d(TAG, "************************************");

        for (final String conditionElement : conditionArray) {
            if (conditionElement.contains(IF)) {
                String condition = conditionElement.replace(IF, Constants.Strings.EMPTY);

                if (condition.contains(DisplayConditionType.SHOW)) {
                    condition = condition.replace(LEFT_BRACKET + DisplayConditionType.SHOW + RIGHT_BRACKET, Constants.Strings.EMPTY);
                    Log.d(TAG, "evaluateCondition: 2.1 " + formatCondition(condition, pModel, pBaseActivity));
                    final boolean isCanShow = TreeBooleanEvaluator.evaluateBoolean(evaluator, formatCondition(condition, pModel, pBaseActivity));
                    Log.d(TAG, "evaluateCondition: 2.2 " + isCanShow);
                    if (isCanShow) {
                        Log.d(TAG, "evaluateCondition: 2.3");
                        return CAN_SHOW;
                    }
                } else if (condition.contains(DisplayConditionType.JUMP)) {
                    int jump = Integer.valueOf(condition.substring(condition.indexOf(DisplayConditionType.JUMP) + DisplayConditionType.JUMP.length(), condition.indexOf(RIGHT_BRACKET)));

                    condition = condition.replace(LEFT_BRACKET + DisplayConditionType.JUMP + jump + RIGHT_BRACKET, Constants.Strings.EMPTY);
                    final boolean isNeedJump = TreeBooleanEvaluator.evaluateBoolean(evaluator, formatCondition(condition, pModel, pBaseActivity));

                    if (isNeedJump) {
                        Log.d(TAG, "evaluateCondition: 3");
                        return jump;
                    }
                } else if (condition.contains(DisplayConditionType.HIDE)) {
                    condition = condition.replace(LEFT_BRACKET + DisplayConditionType.HIDE + RIGHT_BRACKET, Constants.Strings.EMPTY);
                    final boolean isCantShow = TreeBooleanEvaluator.evaluateBoolean(evaluator, formatCondition(condition, pModel, pBaseActivity));

                    if (isCantShow) {
                        Log.d(TAG, "evaluateCondition: 4");
                        return CANT_SHOW;
                    }
                }
            } else if (conditionElement.contains(DisplayConditionType.SHOW)) {
                Log.d(TAG, "evaluateCondition: 5");
                return CAN_SHOW;
            } else if (conditionElement.contains(DisplayConditionType.JUMP)) {
                Log.d(TAG, "evaluateCondition: 6");
                return Integer.valueOf(conditionElement.substring(conditionElement.indexOf(DisplayConditionType.JUMP) + DisplayConditionType.JUMP.length(), conditionElement.indexOf(RIGHT_BRACKET)));
            } else if (conditionElement.contains(DisplayConditionType.HIDE)) {
                Log.d(TAG, "evaluateCondition: 7");
                return CANT_SHOW;
            }
        }
        Log.d(TAG, "evaluateCondition: 8");
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

    private static String formatCondition(final String pExpression, final HashMap<Integer, ElementModelNew> pMap, final MainActivity pBaseActivity) {
        if (StringUtils.isEmpty(pExpression) || (StringUtils.isNotEmpty(pExpression) && !pExpression.contains(ELEMENT_INDEX))) {
            return pExpression;
        }

        final String condition = getConditionString(pExpression);
        final String[] array = getArrayElement(condition);

        final Integer index = Integer.parseInt(array[0]);
        final String field = array[1];

        final ElementModelNew element = pMap.get(index);

        if (element == null) {
            return pExpression;
        }

        List<ElementPassedR> elementsPassed = null;
        try {
            final CurrentQuestionnaireR questionnaireR = pBaseActivity.getMainDao().getCurrentQuestionnaireR();
            final String token = questionnaireR.getToken();
            elementsPassed = pBaseActivity.getMainDao().getAllElementsPassedR(token);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "!@!@!@!@!@!@ formatCondition 1: " + elementsPassed.size());

        ElementPassedR elementItemR = null;
        if (elementsPassed != null && elementsPassed.size() > 0) {
            for (ElementPassedR elementPassedR : elementsPassed) {
                if (elementPassedR.getRelative_id().equals(index)) {
                    elementItemR = elementPassedR;
                }
            }
        }


        Log.d(TAG, "!@!@!@!@!@!@ formatCondition 2: " + index + " " + elementItemR);

        final String replaceString = getReplaceString(field, element, elementItemR, pBaseActivity, pMap);

        return formatCondition(pExpression.replace(ELEMENT_INDEX + condition, replaceString), pMap, pBaseActivity);
    }

    private static String getReplaceString(final String field, final ElementModelNew element, ElementPassedR elementPassedR, final MainActivity pBaseActivity, final HashMap<Integer, ElementModelNew> pMap) {
        switch (field) {
            case ConditionType.TITLE:
                return element.getOptions().getTitle(pBaseActivity, pMap);
            case ConditionType.VALUE:
                return elementPassedR != null ? elementPassedR.getValue() : Constants.Strings.EMPTY;
            case ConditionType.CHECKED:
                return elementPassedR != null ? TRUE : FALSE;
            default:
                return Constants.Strings.EMPTY;
        }
    }
}