package com.example.quizer3;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.utils.ExpressionUtils;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void testExpression() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        String text = "ВОПРОС. Проверка выражения , <# {($e.2.checked || !$e.3.checked) && !(!($e.19.checked && !$e.20.checked) || $e.21.checked)} ? {ПРОШЕЛ УСЛОВИЕ $e.2.vaule} : {НЕ ПРОШЕЛ УСЛОВИЕ (пояснение к тексту 2) } #> здесь продолжается вопрос <# текст3 $e.3.title текст4 (пояснение к тексту 4) #> тоже продолжается текст <# здесь за каким-то хреном просто влепили текст в теги, но хрен с ним выводим всё равно #>";
        ExpressionUtils expressionUtils = new ExpressionUtils((MainActivity) appContext);
        Log.d("T-L.ExampleInstrumented", "testExpression: ");

        assertEquals("pro.quizer.quizer3", appContext.getPackageName());
    }

}
