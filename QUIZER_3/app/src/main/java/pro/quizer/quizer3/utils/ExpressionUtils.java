package pro.quizer.quizer3.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.MainActivity;

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
                                String title = activity.getMap(false).get(relativeId).getOptions().getTitle();
                                decodedExpression.append(title);
                            } else if (expression.charAt(k + nextSymbol + 1) == 'v') { // VALUE
                                String value = activity.getMainDao().getElementPassedR(activity.getToken(), relativeId).getValue();
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
}
