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

//        Log.d("T-L.ExpressionUtils", "===============================");
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
//                    Log.d("T-L.ExpressionUtils", "char: " + temp.charAt(k));
                        if (temp.charAt(k) == '(' || temp.charAt(k) == ')' || temp.charAt(k) == '&' || temp.charAt(k) == '|') {
//                        Log.d("T-L.ExpressionUtils", "break! ");
                            break;
                        } else if (temp.charAt(k) == '<') {
                            between = temp.substring(0, temp.charAt(k - 1) == '=' ? k : k + 1);
                            end = k;
//                        Log.d("T-L.ExpressionUtils", "between: " + between);
                            break;
                        }
                    }
                    if (between != null) {
                        newExpression = expression.replace(between, between.substring(0, between.length() - 1) + "&&" + between.substring(1));
//                    Log.d("T-L.ExpressionUtils", "expression: " + newExpression);
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
//                    Log.d("T-L.ExpressionUtils", "char: " + temp.charAt(k));
                        if (temp.charAt(k) == '(' || temp.charAt(k) == ')' || temp.charAt(k) == '&' || temp.charAt(k) == '|') {
//                        Log.d("T-L.ExpressionUtils", "break! ");
                            break;
                        } else if (temp.charAt(k) == '>') {
                            between = temp.substring(0, temp.charAt(k - 1) == '=' ? k : k + 1);
                            end = k;
//                        Log.d("T-L.ExpressionUtils", "between: " + between);
                            break;
                        }
                    }
                    if (between != null) {
                        newExpression = expression.replace(between, between.substring(0, between.length() - 1) + "&&" + between.substring(1));
//                    Log.d("T-L.ExpressionUtils", "expression: " + newExpression);
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
                Log.d("T-A-R.ExpressionUtils", "FOUND UIK EXPRESSION: " + idString);
                String oldPart = "!!!!!!!!!!!!!!!!!!";
                for (int n = 0; n < idString.length() - 1; n++) {
                    if (!Character.isDigit(idString.charAt(n + 1))) {
                        idString = idString.substring(0, i + 1);
                        oldPart = "$uik==" + idString;
                        Log.d("T-A-R.ExpressionUtils", ">>> UIK: " + idString + " N: " + n);
                        break;
                    }
                    if((n == idString.length() - 2) && Character.isDigit(idString.charAt(n + 1))) {
                        oldPart = "$uik==" + idString;
                        Log.d("T-A-R.ExpressionUtils", ">>> UIK: " + idString + " N: " + n);
                        break;
                    }
                }

                CurrentQuestionnaireR quiz = activity.getCurrentQuestionnaire();
                quiz.setRegistered_uik("108"); //TODO ДЛЯ ТЕСТОВ! УБРАТЬ!
                if(quiz != null && quiz.getRegistered_uik() != null && !quiz.getRegistered_uik().isEmpty() && quiz.getRegistered_uik().equals(idString)) {
                    newExpression = newExpression.replace(oldPart, "1.0");
                } else {
                    newExpression = newExpression.replace(oldPart, "0.0");
                }
            }
        }

//        newExpression = "2<20 && 20< 17/2";
//        Log.d("T-L.ExpressionUtils", "checkHiddenExpression 1: " + );
        Expression e = new Expression(newExpression);
//        Log.d("T-L.ExpressionUtils", "checkHiddenExpression: " + newExpression);
//        Log.d("T-L.ExpressionUtils", "result: " + e.calculate() + "/" + getBooleanResult(e.calculate()));
        return getBooleanResult(e.calculate());
    }

    private boolean getBooleanResult(Double result) {
        return result == 1.0;
    }

//    public boolean checkHiddenExpression2(String expression) {
//        expression = expression.replaceAll(" ", "");
//
//        List<PartOfHiddenExpression> partsList = new ArrayList<>();
//        int indexOfPartEnd = 0;
//
//        for (int i = 0; i < expression.length(); i++) {
//            if (i == expression.indexOf(Operators.LOE, i)) {
//                partsList.add(new PartOfHiddenExpression(expression.substring(indexOfPartEnd, i), Operators.LOE));
//                indexOfPartEnd = i + 2;
//            } else if (i == expression.indexOf(Operators.MOE, i)) {
//                partsList.add(new PartOfHiddenExpression(expression.substring(indexOfPartEnd, i), Operators.MOE));
//                indexOfPartEnd = i + 2;
//            } else if (i == expression.indexOf(Operators.LESS, i)) {
//                partsList.add(new PartOfHiddenExpression(expression.substring(indexOfPartEnd, i), Operators.LESS));
//                indexOfPartEnd = i + 1;
//            } else if (i == expression.indexOf(Operators.MORE, i)) {
//                partsList.add(new PartOfHiddenExpression(expression.substring(indexOfPartEnd, i), Operators.MORE));
//                indexOfPartEnd = i + 1;
//            } else if (i == expression.indexOf(Operators.EQ, i)) {
//                partsList.add(new PartOfHiddenExpression(expression.substring(indexOfPartEnd, i), Operators.EQ));
//                indexOfPartEnd = i + 2;
//            } else if (i == expression.indexOf(Operators.NOT, i)) {
//                partsList.add(new PartOfHiddenExpression(expression.substring(indexOfPartEnd, i), Operators.NOT));
//                indexOfPartEnd = i + 2;
//            }
//        }
//
//        partsList.add(new PartOfHiddenExpression(expression.substring(indexOfPartEnd), null));
//
//        for (int index = 0; index < partsList.size(); index++) {
//            for (int i = 0; i < partsList.get(index).part.length(); i++) {
//                if (i == partsList.get(index).part.indexOf("$e.", i)) {
//                    String idString = partsList.get(index).part.substring(i + 3, partsList.get(index).part.indexOf(".value", i + 3));
//                    Integer id = null;
//                    Integer value = null;
//                    try {
//                        id = Integer.valueOf(idString);
//                    } catch (NumberFormatException e) {
//                        e.printStackTrace();
//                        Log.d("T-L.ExpressionUtils", "CANT GET ELEMENT: " + idString);
//                        return false;
//                    }
//                    if (id == null) return false;
//                    else {
//                        ElementPassedR element = activity.getMainDao().getElementPassedR(activity.getToken(), id);
//                        if (element != null) {
//                            try {
//                                value = Integer.parseInt(element.getValue());
//                            } catch (NumberFormatException e) {
//                                e.printStackTrace();
//                                value = 0;
//                            }
//                        } else {
//                            Log.d("T-L.ExpressionUtils", "CANT GET ELEMENT: " + id);
//                            return false;
//                        }
//                        if (value != null) {
//                            String oldPart = "$e." + idString + ".value";
//                            String newPart = partsList.get(index).part.replace(oldPart, value.toString());
//                            partsList.set(index, new PartOfHiddenExpression(newPart, partsList.get(index).operator));
//                        } else {
//                            Log.d("T-L.ExpressionUtils", "CANT GET VALUE OF: " + element.getValue());
//                            return false;
//                        }
//                    }
//                }
//            }
//
//        }
//
//        for (int index = 0; index < partsList.size(); index++) {
//            double value = eval(partsList.get(index).part);
//            String newString = "" + value;
//            partsList.set(index, new PartOfHiddenExpression(newString, partsList.get(index).operator));
//        }
//
//        boolean check = checkHiddenOperator(partsList);
//        return check;
//    }
//
//    private boolean checkHiddenOperator(List<PartOfHiddenExpression> partsList) {
//        for (int index = 0; index < partsList.size() - 1; index++) {
//            Double left = null;
//            Double right = null;
//            try {
//                left = Double.valueOf(partsList.get(index).part);
//                right = Double.valueOf(partsList.get(index + 1).part);
//            } catch (NumberFormatException e) {
//                e.printStackTrace();
//                return false;
//            }
//            if (left == null || right == null) return false;
//            if (partsList.get(index).operator == null) return false;
//            switch (partsList.get(index).operator) {
//                case Operators.LOE:
//                    if (!(left <= right)) return false;
//                    break;
//                case Operators.MOE:
//                    if (!(left >= right)) return false;
//                    break;
//                case Operators.LESS:
//                    if (!(left < right)) return false;
//                    break;
//                case Operators.MORE:
//                    if (!(left > right)) return false;
//                    break;
//                case Operators.EQ:
//                    if (!left.equals(right)) return false;
//                    break;
//                case Operators.NOT:
//                    if (left.equals(right)) return false;
//                    break;
//                case "null":
//                    return false;
//            }
//        }
//
//        return true;
//    }
//
//    public static double eval(final String str) {
//        return new Object() {
//            int pos = -1, ch;
//
//            void nextChar() {
//                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
//            }
//
//            boolean eat(int charToEat) {
//                while (ch == ' ') nextChar();
//                if (ch == charToEat) {
//                    nextChar();
//                    return true;
//                }
//                return false;
//            }
//
//            double parse() {
//                nextChar();
//                double x = parseExpression();
//                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
//                return x;
//            }
//
//            double parseExpression() {
//                double x = parseTerm();
//                for (; ; ) {
//                    if (eat('+')) x += parseTerm(); // addition
//                    else if (eat('-')) x -= parseTerm(); // subtraction
//                    else return x;
//                }
//            }
//
//            double parseTerm() {
//                double x = parseFactor();
//                for (; ; ) {
//                    if (eat('*')) x *= parseFactor(); // multiplication
//                    else if (eat('/')) x /= parseFactor(); // division
//                    else return x;
//                }
//            }
//
//            double parseFactor() {
//                if (eat('+')) return parseFactor(); // unary plus
//                if (eat('-')) return -parseFactor(); // unary minus
//
//                double x;
//                int startPos = this.pos;
//                if (eat('(')) { // parentheses
//                    x = parseExpression();
//                    eat(')');
//                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
//                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
//                    x = Double.parseDouble(str.substring(startPos, this.pos));
//                } else if (ch >= 'a' && ch <= 'z') { // functions
//                    while (ch >= 'a' && ch <= 'z') nextChar();
//                    String func = str.substring(startPos, this.pos);
//                    x = parseFactor();
//                    if (func.equals("sqrt")) x = Math.sqrt(x);
//                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
//                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
//                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
//                    else throw new RuntimeException("Unknown function: " + func);
//                } else {
//                    throw new RuntimeException("Unexpected: " + (char) ch);
//                }
//
//                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation
//
//                return x;
//            }
//        }.parse();
//    }
//
//    public class PartOfHiddenExpression {
//        String part;
//        String operator;
//
//        public PartOfHiddenExpression(String part, String operator) {
//            this.part = part;
//            this.operator = operator;
//        }
//    }
}
