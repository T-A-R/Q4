package com.example.quizer3;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.database.QuizerDao;
import pro.quizer.quizer3.database.models.ElementPassedR;
import pro.quizer.quizer3.model.config.ElementModelNew;
import pro.quizer.quizer3.model.config.OptionsModelNew;
import pro.quizer.quizer3.utils.ExpressionUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class QuizerTests {

    private MainActivity mMainActivity;
    private QuizerDao mQuizerDao;
    private OptionsModelNew mOptionsModelNew;
    private ElementPassedR mElementWithId2;
    private ElementPassedR mElementWithId3;
    private ElementPassedR mElementWithId19;
    private ElementPassedR mElementWithId20;
    private ElementPassedR mElementWithId21;
    private Map<Integer, ElementModelNew> mMap;
    private ElementModelNew mElementModelNew;

    @Before
    public void setUp() throws Exception {

        mMainActivity = Mockito.mock(MainActivity.class); // Делаем куклу активности
        mQuizerDao = Mockito.mock(QuizerDao.class); // Делаем куклу базы данных

        // Анкета
        mMap = new HashMap<>();
        // Опции элемента c ID=3
        mOptionsModelNew = new OptionsModelNew(); mOptionsModelNew.setTitle("Title элемента с ID=3");
        // Элемент в анкете
        mElementModelNew = new ElementModelNew(); mElementModelNew.setOptions(mOptionsModelNew);
        // Добавляем в анкету элемент
        mMap.put(3,mElementModelNew);

        // Пройденные элементы
        mElementWithId2 = new ElementPassedR(); mElementWithId2.setRelative_id(2); mElementWithId2.setValue("Ответ на вопрос с ID=2");
        mElementWithId3 = new ElementPassedR(); mElementWithId3.setRelative_id(3); mElementWithId3.setValue("Ответ на вопрос с ID=3");
        mElementWithId19 = new ElementPassedR(); mElementWithId19.setRelative_id(19); mElementWithId19.setValue("Ответ на вопрос с ID=19");
        mElementWithId20 = new ElementPassedR(); mElementWithId20.setRelative_id(20); mElementWithId20.setValue("Ответ на вопрос с ID=20");
        mElementWithId21 = new ElementPassedR(); mElementWithId21.setRelative_id(21); mElementWithId21.setValue("Ответ на вопрос с ID=21");

        // Опции элемента c ID=3
        mOptionsModelNew = new OptionsModelNew(); mOptionsModelNew.setTitle("Title элемента с ID=3");
    }

    @Test
    public void testExpression() {
        String text = "ВОПРОС. Проверка выражения , <# {($e.2.checked || !$e.3.checked) && !(!($e.19.checked && !$e.20.checked) || $e.21.checked)} ? {ПРОШЕЛ УСЛОВИЕ $e.2.vaule} : {НЕ ПРОШЕЛ УСЛОВИЕ (пояснение к тексту 2) } #> здесь продолжается вопрос <# текст3 $e.3.title текст4 (пояснение к тексту 4) #> тоже продолжается текст <# здесь за каким-то хреном просто влепили текст в теги, но хрен с ним выводим всё равно #>";

        String answerTRUE = "ВОПРОС. Проверка выражения , ПРОШЕЛ УСЛОВИЕ Ответ на вопрос с ID=2 здесь продолжается вопрос текст3 Title элемента с ID=3 текст4 (пояснение к тексту 4) тоже продолжается текст здесь за каким-то хреном просто влепили текст в теги, но хрен с ним выводим всё равно";
        String answerFALSE = "ВОПРОС. Проверка выражения , НЕ ПРОШЕЛ УСЛОВИЕ (пояснение к тексту 2)  здесь продолжается вопрос текст3 Title элемента с ID=3 текст4 (пояснение к тексту 4) тоже продолжается текст здесь за каким-то хреном просто влепили текст в теги, но хрен с ним выводим всё равно";

        when(mMainActivity.getToken()).thenReturn("1234567890"); // Запрос на токен
        when(mMainActivity.getMainDao()).thenReturn(mQuizerDao); // Запрос на базу данных

        when(mMainActivity.getMainDao().getElementPassedR("1234567890", 2)).thenReturn(mElementWithId2); // Запросы пройденных элементов
        when(mMainActivity.getMainDao().getElementPassedR("1234567890", 3)).thenReturn(mElementWithId3);
        when(mMainActivity.getMainDao().getElementPassedR("1234567890", 19)).thenReturn(mElementWithId19);
        when(mMainActivity.getMainDao().getElementPassedR("1234567890", 20)).thenReturn(mElementWithId20);
        when(mMainActivity.getMainDao().getElementPassedR("1234567890", 21)).thenReturn(mElementWithId21);

        when(mMainActivity.getMap(false)).thenReturn((HashMap<Integer, ElementModelNew>) mMap); // Запрос анкеты

        assertEquals(getConvertedString(text) , answerTRUE); // Если после выполнения пребразований мы получили ожидаемую строку то тест пройден
//        assertEquals(getConvertedString(text) , answerFALSE); // Если после выполнения пребразований мы получили ожидаемую строку то тест пройден

    }

    private String getConvertedString(String s) {
        String endString = s;
        ExpressionUtils expressionUtils = new ExpressionUtils(mMainActivity);
        List<String> expressions;

        expressions = expressionUtils.findExpressions(s);

        if (expressions.size() > 0) {
            for (String expression : expressions) {
                endString = endString.replace("<# " + expression + " #>", expressionUtils.decodeExpression(expression));
            }

            return endString;
        } else return s;
    }

}
