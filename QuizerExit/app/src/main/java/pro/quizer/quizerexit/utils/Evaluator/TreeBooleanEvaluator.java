package pro.quizer.quizerexit.utils.Evaluator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pro.quizer.quizerexit.Constants;

public class TreeBooleanEvaluator extends AbstractEvaluator<String> {
    /**
     * The logical AND operator.
     */
    private static final Operator AND = new Operator("&&", 2, Operator.Associativity.LEFT, 2);
    /**
     * The logical OR operator.
     */
    private static final Operator OR = new Operator("||", 2, Operator.Associativity.LEFT, 1);

    private static final Parameters PARAMETERS;
    public static final String TRUE = "true";
    private static final String _TRUE = "=true";
    private static final String _FALSE = "=false";

    static {
        // Create the evaluator's parameters
        PARAMETERS = new Parameters();
        // Add the supported operators
        PARAMETERS.add(AND);
        PARAMETERS.add(OR);
        // Add the parentheses
        PARAMETERS.addExpressionBracket(BracketPair.PARENTHESES);
    }

    public TreeBooleanEvaluator() {
        super(PARAMETERS);
    }

    @Override
    protected String toValue(final String literal, final Object evaluationContext) {
        return literal;
    }

    private boolean getValue(final String literal) {
        if ("T".equals(literal) || literal.endsWith(_TRUE)) return true;
        else if ("F".equals(literal) || literal.endsWith(_FALSE)) return false;
        throw new IllegalArgumentException("Unknown literal : " + literal);
    }

    @Override
    protected String evaluate(final Operator operator, final Iterator<String> operands, final Object evaluationContext) {
        final List<String> tree = (List<String>) evaluationContext;
        final String o1 = operands.next();
        final String o2 = operands.next();
        final Boolean result;
        if (operator == OR) {
            result = getValue(o1) || getValue(o2);
        } else if (operator == AND) {
            result = getValue(o1) && getValue(o2);
        } else {
            throw new IllegalArgumentException();
        }
        final String eval = "(" + o1 + " " + operator.getSymbol() + " " + o2 + ")=" + result;
        tree.add(eval);
        return eval;
    }

    public static boolean evaluateBoolean(final TreeBooleanEvaluator evaluator, final String expression) {
        final String expression1 = "( " + expression + " ) && T";
        final List<String> sequence = new ArrayList<>();

        evaluator.evaluate(expression1, sequence);
        String result = Constants.Strings.EMPTY;

        for (final String string : sequence) {
            result = string;
        }

        return result.endsWith(_TRUE);
    }
}
