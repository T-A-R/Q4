package pro.quizer.quizerexit;

import org.junit.Test;

import pro.quizer.quizerexit.utils.ConditionUtils;
import pro.quizer.quizerexit.utils.Evaluator.TreeBooleanEvaluator;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class BooleanEvaluationTest {

    @Test
    public void testEvaluation() {
        final TreeBooleanEvaluator evaluator = new TreeBooleanEvaluator();

        assertEquals(true, TreeBooleanEvaluator.evaluateBoolean(evaluator, "T"));
        assertEquals((true), TreeBooleanEvaluator.evaluateBoolean(evaluator, "(T)"));
        assertEquals((true), TreeBooleanEvaluator.evaluateBoolean(evaluator, "((T))"));
        assertEquals((true), TreeBooleanEvaluator.evaluateBoolean(evaluator, "( T )"));
        assertEquals(true && false, TreeBooleanEvaluator.evaluateBoolean(evaluator, "T && F"));
        assertEquals((true && true) || (false && true), TreeBooleanEvaluator.evaluateBoolean(evaluator, "(T&&T)||(F&&T)"));
        assertEquals(true && (false || (false && true)), TreeBooleanEvaluator.evaluateBoolean(evaluator, "T && ( F || ( F && T ) )"));
        assertEquals((true && false), TreeBooleanEvaluator.evaluateBoolean(evaluator, "(T && F)"));
        assertEquals(false && true, TreeBooleanEvaluator.evaluateBoolean(evaluator, "( T && F )"));
        assertEquals(true || false, TreeBooleanEvaluator.evaluateBoolean(evaluator, "T || F"));
        assertEquals(false || true, TreeBooleanEvaluator.evaluateBoolean(evaluator, "F || T"));
        assertEquals(true && true || false, TreeBooleanEvaluator.evaluateBoolean(evaluator, "T && T || F"));
        assertEquals(true && (true || false), TreeBooleanEvaluator.evaluateBoolean(evaluator, "T && (T || F)"));
        assertEquals((true && true) || (false || true), TreeBooleanEvaluator.evaluateBoolean(evaluator, "(T && T) || (F || T)"));
        assertEquals((false) || (true), TreeBooleanEvaluator.evaluateBoolean(evaluator, "(F) || (T)"));
        assertEquals(false || true, TreeBooleanEvaluator.evaluateBoolean(evaluator, "F || T"));
        assertEquals(((false && true) && true) || true, TreeBooleanEvaluator.evaluateBoolean(evaluator, "((F && T) && T) || T"));
        assertEquals(((false && true) && true) || false, TreeBooleanEvaluator.evaluateBoolean(evaluator, "((F && T) && T) || F"));
    }

    @Test
    public void testIsCanShow() {
//        ConditionUtils.isCanShowScreen("if (выражение) {show} else if (выражение) {jump:185}", null);
//        ConditionUtils.isCanShowScreen("if (выражение) {not_show}", null);
//        ConditionUtils.isCanShowScreen("if (выражение) {show}", null);
    }
}