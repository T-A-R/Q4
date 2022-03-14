package pro.quizer.quizer3.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import org.mariuszgromada.math.mxparser.*;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.database.models.CurrentQuestionnaireR;
import pro.quizer.quizer3.database.models.ElementPassedR;
import pro.quizer.quizer3.model.Operators;
import pro.quizer.quizer3.model.config.ElementModelNew;

public class ExpressionUtils {

    MainActivity activity;

    public ExpressionUtils(MainActivity activity) {
        this.activity = activity;
    }

    public List<String> findExpressions(String startString) {
        List<String> expressions = new ArrayList<>();

        for (int i = 0; i < startString.length() - 4; i++) {
            if (startString.charAt(i) == '<' && startString.charAt(i + 1) == '#') {
                for (int k = i + 2; k < startString.length() - 1; k++) {
                    if (startString.charAt(k) == '#' && startString.charAt(k + 1) == '>') {
                        expressions.add(startString.substring(i + 3, k - 1));
                        i = k + 2;
                        if (i > startString.length() - 4) {
                            return expressions;
                        }
                        break;
                    }
                }
            }
        }

        return expressions;
    }

    public String decodeExpression(String expression) {

        StringBuilder decodedExpression = new StringBuilder();
        int length = expression.length();

        if (expression == null) return ""; // NULL EXPRESSION. WTF?

        if (!expression.contains("$")) return expression; // JUST TEXT

        for (int i = 0; i < length; i++) {

            if (expression.charAt(i) == '{') { // IF FOUND
                List<String> parts = new ArrayList<>();
                for (int k = i; k < length; k++) {
                    if (expression.charAt(k) == '{') {
                        int nextSymbol = findSymbol('}', expression.substring(k + 1));
                        if (nextSymbol != -1) {
                            parts.add(expression.substring(k + 1, k + nextSymbol));
                            k += nextSymbol;

                            if (parts.size() == 1) {
                                int operatorPosition = -1;
                                operatorPosition = findSymbol('?', expression);
                                if (operatorPosition == -1) return expression; // CANT FIND "?"
                                if (expression.charAt(operatorPosition) == ':') parts.add("");
                            }
                        } else break;
                    }
                }
                if (parts.size() < 2 || parts.size() > 3) return expression; // WRONG "IF" EXPRESSION

                if (checkIfExpression(parts.get(0))) return decodeExpression(parts.get(1));
                else if (parts.size() == 3) return decodeExpression(parts.get(2));
                else return "";

            } else if (expression.charAt(i) == '$') { // PRINT FOUND
                int relativeId;
                for (int k = i; k < length; k++) {
                    if (expression.charAt(k) == '.') {
                        int nextSymbol = findSymbol('.', expression.substring(k + 1));
                        if (nextSymbol == -1) return expression; // RELATIVE_ID PARSE ERROR
                        try {
                            relativeId = Integer.parseInt(expression.substring(k + 1, k + nextSymbol));
                            if (expression.charAt(k + nextSymbol + 1) == 't') { // TITLE
                                ElementModelNew elementModelNew = activity.getMap(false).get(relativeId);
                                String title = elementModelNew != null ? elementModelNew.getOptions().getTitle() : expression.substring(i);
                                decodedExpression.append(title);
                            } else if (expression.charAt(k + nextSymbol + 1) == 'v') { // VALUE
                                ElementPassedR element = activity.getMainDao().getElementPassedR(activity.getToken(), relativeId);
//                                String value = element != null ? element.getValue() : expression.substring(i);
                                String value = element != null ? element.getValue() : "";
                                decodedExpression.append(value);
                            } else if (expression.charAt(k + nextSymbol + 1) == 'c') { // CHECKED
                                Boolean isChecked = activity.getMainDao().getElementPassedR(activity.getToken(), relativeId) != null;
                                return isChecked.toString();
                            } else {
                                return expression;
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Log.d("T-L.StringUtils", "PRINT DECODE ERROR");
                            return expression;
                        }

                        i = k + nextSymbol + 6;
                        if (i < length) decodedExpression.append(expression.substring(i, length)); // JUNK TEXT AFTER VALUE TEXT IN EXPRESSION

                        return decodedExpression.toString();
                    }
                }
            } else decodedExpression.append(expression.charAt(i));
        }

        return expression;
    }

    public int findSymbol(Character symbol, String text) {
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == symbol) return i + 1; // RETURN NEXT POSITION !!!
        }

        return -1; // NOT FOUND!
    }

    public boolean checkIfExpression(String expression) {
        Boolean result = null;
        expression = expression.replaceAll(" ", "");

        for (int i = 0; i < expression.length(); i++) {
            List<Boolean> hasOpen = new ArrayList<>();
            if (expression.charAt(i) == '$') {
                String localResult = decodeExpression(expression.substring(i));
                if (result == null) {
                    if (i == 0) result = Boolean.parseBoolean(localResult);
                    else result = !Boolean.parseBoolean(localResult);
                } else {
                    if (expression.charAt(i - 1) == '!') {
                        if (expression.charAt(i - 2) == '&') {
                            result = result && !Boolean.parseBoolean(localResult);
                        } else if (expression.charAt(i - 2) == '|') {
                            result = result || !Boolean.parseBoolean(localResult);
                        }
                    } else {
                        if (expression.charAt(i - 1) == '&') {
                            result = result && Boolean.parseBoolean(localResult);
                        } else if (expression.charAt(i - 1) == '|') {
                            result = result || Boolean.parseBoolean(localResult);
                        }
                    }
                }
            } else if (expression.charAt(i) == '(') {
                for (int k = i + 1; k < expression.length(); k++) {
                    if (expression.charAt(k) == ')') {
                        if (hasOpen.size() == 0) {
                            Boolean localResult = checkIfExpression(expression.substring(i + 1, k));

                            if (result == null) {
                                if (i == 0) result = localResult;
                                else result = !localResult;
                            } else {
                                if (expression.charAt(i - 1) == '!') {
                                    if (expression.charAt(i - 2) == '&') {
                                        result = result && !localResult;
                                    } else if (expression.charAt(i - 2) == '|') {
                                        result = result || !localResult;
                                    }
                                } else {
                                    if (expression.charAt(i - 1) == '&') {
                                        result = result && localResult;
                                    } else if (expression.charAt(i - 1) == '|') {
                                        result = result || localResult;
                                    }
                                }
                            }

                            i = k;
                            break;
                        } else {
                            hasOpen.remove(hasOpen.size() - 1);
                        }
                    } else if (expression.charAt(k) == '(') {
                        hasOpen.add(true);
                    }
                }
            }
        }

        return result == null ? false : result;
    }

    public boolean checkHiddenExpression(String expression) {

//        expression = "2<20<=170/2";
//        expression = "($e.3.checked && 5<$e.2.value<=17*2) || 21<=$e.2.value + 23 <40";
//        expression = "100-$e.2.value <($e.3.value+15-$e.4.value)/2<=$e.5.value && ($e.6.checked || !$e.2.checked)";
//        expression = "100-$e.2.value =$e.3.value";
//        expression = "$uik == 108";

//        Log.d("T-L.ExpressionUtils", "START: " + expression);
        Log.d("T-A-R.ExpressionUtils", "checkHidden Expression: " + expression);

        expression = expression.replaceAll(" ", "");
        String newExpression = expression;

        boolean normalDirection = true;
        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == '<') break;
            if (expression.charAt(i) == '>') {
                normalDirection = false;
                break;
            }
        }

        if (normalDirection) {
            for (int i = 0; i < expression.length(); i++) {
                if (expression.charAt(i) == '<' && i < expression.length() - 3) {
                    String temp = expression.substring(expression.charAt(i + 1) == '=' ? i + 1 : i);
                    String between = null;
                    int end = 0;
                    for (int k = 1; k < temp.length(); k++) {
                        if (temp.charAt(k) == '(' || temp.charAt(k) == ')' || temp.charAt(k) == '&' || temp.charAt(k) == '|') {
                            break;
                        } else if (temp.charAt(k) == '<') {
                            between = temp.substring(0, temp.charAt(k - 1) == '=' ? k : k + 1);
                            end = k;
                            break;
                        }
                    }
                    if (between != null) {
                        newExpression = expression.replace(between, between.substring(0, between.length() - 1) + "&&" + between.substring(1));
                        i += end;
                    }
                }
            }
        } else {
            for (int i = 0; i < expression.length(); i++) {
                if (expression.charAt(i) == '>' && i < expression.length() - 3) {
                    String temp = expression.substring(expression.charAt(i + 1) == '=' ? i + 1 : i);
                    String between = null;
                    int end = 0;
                    for (int k = 1; k < temp.length(); k++) {
                        if (temp.charAt(k) == '(' || temp.charAt(k) == ')' || temp.charAt(k) == '&' || temp.charAt(k) == '|') {
                            break;
                        } else if (temp.charAt(k) == '>') {
                            between = temp.substring(0, temp.charAt(k - 1) == '=' ? k : k + 1);
                            end = k;
                            break;
                        }
                    }
                    if (between != null) {
                        newExpression = expression.replace(between, between.substring(0, between.length() - 1) + "&&" + between.substring(1));
                        i += end;
                    }
                }
            }

        }

        newExpression = newExpression.replaceAll("!", "~"); // Замена символа отрицания для парсера. (В парсере ! - это факториал)
        expression = newExpression;

        Log.d("T-A-R.", "checkHiddenExpression !!!!!!!!!!!: " + newExpression);

        for (int i = 0; i < expression.length(); i++) {
            if (i == expression.indexOf("$e.", i)) {
                int indexOfPoint = expression.indexOf(".", i + 3);
                String idString = expression.substring(i + 3, indexOfPoint);
                int id;
                int value;
                try {
                    id = Integer.parseInt(idString);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Log.d("T-L.ExpressionUtils", "CANT GET ELEMENT: " + idString);
                    return false;
                }

                String oldPart;
                ElementPassedR element = activity.getMainDao().getElementPassedR(activity.getToken(), id);
                switch (expression.charAt(indexOfPoint + 1)) {
                    case 'v':
                        if (element != null) {
                            try {
                                value = Integer.parseInt(element.getValue());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                return false;
                            }
                        } else {
                            Log.d("T-L.ExpressionUtils", "CANT GET ELEMENT: " + id);
                            return false;
                        }
                        oldPart = "$e." + idString + ".value";
                        newExpression = newExpression.replace(oldPart, Integer.toString(value));
                        break;
                    case 'c':
                        oldPart = "$e." + idString + ".checked";
                        newExpression = newExpression.replace(oldPart, element != null ? "1.0" : "0.0"); // 1.0 == TRUE ; 0.0 == FALSE
                        break;

                }
            } else if (i == expression.indexOf("$ui", i)) {
                String idString = expression.substring(i + 6);
                String oldPart = "!!!!!!!!!!!!!!!!!!";
                for (int n = 0; n < idString.length() - 1; n++) {
                    if (!Character.isDigit(idString.charAt(n + 1))) {
                        idString = idString.substring(0, i + 1);
                        oldPart = "$uik==" + idString;
                        break;
                    }
                    if((n == idString.length() - 2) && Character.isDigit(idString.charAt(n + 1))) {
                        oldPart = "$uik==" + idString;
                        break;
                    }
                }

                CurrentQuestionnaireR quiz = activity.getCurrentQuestionnaire();
//                quiz.setRegistered_uik("108"); //TODO ДЛЯ ТЕСТОВ! УБРАТЬ!
                if(quiz != null && quiz.getRegistered_uik() != null && !quiz.getRegistered_uik().isEmpty() && quiz.getRegistered_uik().equals(idString)) {
                    newExpression = newExpression.replace(oldPart, "1.0");
                } else {
                    newExpression = newExpression.replace(oldPart, "0.0");
                }
            }
        }

        Expression e = new Expression(newExpression);

        return getBooleanResult(e.calculate());
    }

    private boolean getBooleanResult(Double result) {
        return result == 1.0;
    }

}
