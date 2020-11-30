package com.example.quizer3;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Tap;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.example.quizer3.CustomMatchers.*;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4ClassRunner.class)
public class NavigationTestJava {

    @Rule
    public GrantPermissionRule permissionRule1 = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
    @Rule
    public GrantPermissionRule permissionRule2 = GrantPermissionRule.grant(android.Manifest.permission.CALL_PHONE);
    @Rule
    public GrantPermissionRule permissionRule3 = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION);
    @Rule
    public GrantPermissionRule permissionRule4 = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_NETWORK_STATE);
    @Rule
    public GrantPermissionRule permissionRule5 = GrantPermissionRule.grant(android.Manifest.permission.INTERNET);
    @Rule
    public GrantPermissionRule permissionRule6 = GrantPermissionRule.grant(Manifest.permission.CAMERA);
    @Rule
    public GrantPermissionRule permissionRule7 = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    @Rule
    public GrantPermissionRule permissionRule8 = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);
    @Rule
    public GrantPermissionRule permissionRule9 = GrantPermissionRule.grant(Manifest.permission.SEND_SMS);
    @Rule
    public GrantPermissionRule permissionRule10 = GrantPermissionRule.grant(Manifest.permission.RECORD_AUDIO);
    @Rule
    public GrantPermissionRule permissionRule11 = GrantPermissionRule.grant(Manifest.permission.VIBRATE);
    @Rule
    public GrantPermissionRule permissionRule12 = GrantPermissionRule.grant(Manifest.permission.READ_PHONE_STATE);

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule(MainActivity.class);

    @Test
    public void testMovieFragmentsNavigation() throws Throwable {

        String key = "plus";
        String wrongKey = "minus";
        String login = "tartest";
        String login2 = "tartest2";
        String password = "1";
        String wrongPassword = "2";
        String answer1 = "Ответ";
        String projectName = "TEST CARDS ( НЕ МЕНЯТЬ ПРОЕКТ! )";
        String yes = "Да";
        String no = "Нет";

        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);

        allowPermissionsIfNeeded();

        // Ввод ключа
        if (!viewIsDisplayed(R.id.cont_key_fragment)) {
            // Нажатие на версию
            onView(withId(R.id.version_view)).check(matches(isDisplayed()));
            onView(withId(R.id.version_view)).perform(click()).perform(click()).perform(click()).perform(click()).perform(click());

            // Сервисное меню. Очистка Базы данных
            onView(withId(R.id.service_cont)).check(matches(isDisplayed()));
            onView(withId(R.id.clear_db)).check(matches(isDisplayed()));
            onView(withId(R.id.clear_db)).perform(scrollTo()).perform(click());

            // Диалог подтверждения очистки базы данных
            onView(withText(R.string.clear_db_title)).inRoot(isDialog()).check(matches(isDisplayed()));
            onView(withText(yes)).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
        }
        onView(withId(R.id.cont_key_fragment)).check(matches(isDisplayed()));
        onView(withId(R.id.et_activation)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_send_activation)).check(matches(isDisplayed()));

        onView(withId(R.id.btn_send_activation)).perform(click());
        onView(withText(R.string.empty_key)).inRoot(withDecorView(not(is(activityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        Thread.sleep(3000);
        onView(withId(R.id.et_activation)).perform(typeText(wrongKey));
        onView(withId(R.id.btn_send_activation)).perform(click());
        onView(withText(R.string.wrong_key)).inRoot(withDecorView(not(is(activityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        onView(withId(R.id.et_activation)).perform(clearText(), typeText(key));

        onView(withId(R.id.btn_send_activation)).perform(click());

        // Авторизация
        onView(withId(R.id.cont_auth_fragment)).check(matches(isDisplayed()));
        onView(withId(R.id.login_spinner)).check(matches(isDisplayed()));
        onView(withId(R.id.auth_password_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_send_auth)).check(matches(isDisplayed()));

        onView(withId(R.id.login_spinner)).perform(clearText(), typeText(login));
        onView(withId(R.id.auth_password_edit_text)).perform(typeText(wrongPassword));
        onView(withId(R.id.btn_send_auth)).perform(click());
        onView(withSubstring("Ошибка при ответе сервера")).inRoot(withDecorView(not(is(activityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        Thread.sleep(1000);
        onView(withId(R.id.auth_password_edit_text)).perform(clearText(), typeText(password));
        onView(withId(R.id.btn_send_auth)).perform(click());

        // Главный экран
        onView(withId(R.id.cont_home_fragment)).check(matches(isDisplayed()));

        onView(withId(R.id.config_name)).check(matches(isDisplayed()));
        onView(withId(R.id.config_name)).check(matches(withText(projectName)));

        onView(withId(R.id.btn_start)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_info)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_quotas)).check(matches(isDisplayed()));

        Thread.sleep(1000);

        // Боковое меню
        onView(withId(R.id.toolbar_view_options)).check(matches(isDisplayed()));
        onView(withId(R.id.toolbar_view_options)).perform(click());
        onView(withId(R.id.drawer_menu_cont)).perform(swipeLeft());
        Thread.sleep(1000);
        onView(withId(R.id.drawer_cont)).perform(DrawerActions.open());
        onView(withText(R.string.drawer_home)).check(matches(isDisplayed()));
        onView(withBackground(R.drawable.home_white)).check(matches(isDisplayed()));
        onView(withText(R.string.drawer_sync)).check(matches(isDisplayed()));
        onView(withBackground(R.drawable.sync_white)).check(matches(isDisplayed()));
        onView(withText(R.string.drawer_settings)).check(matches(isDisplayed()));
        onView(withBackground(R.drawable.settings_white)).check(matches(isDisplayed()));
        onView(withText(R.string.drawer_quotas)).check(matches(isDisplayed()));
        onView(withBackground(R.drawable.checkbox_white)).check(matches(isDisplayed()));
        onView(withText(R.string.drawer_about)).check(matches(isDisplayed()));
        onView(withBackground(R.drawable.about_white)).check(matches(isDisplayed()));
        onView(withText(R.string.drawer_exit)).check(matches(isDisplayed()));
        onView(withBackground(R.drawable.logout_white)).check(matches(isDisplayed()));

        onView(withId(R.id.drawer_menu_cont)).perform(swipeLeft());

        // Выход по нажатию системной кнопки "Назад"
        Espresso.pressBack();

        // Диалог подтверждения выхода
        onView(withText(R.string.dialog_close_app_title)).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText(no)).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());

        // Смена пользователя через боковое меню
        onView(withId(R.id.drawer_cont)).perform(DrawerActions.open());
        onView(withText(R.string.drawer_exit)).perform(click());
        onView(withText(R.string.dialog_change_user)).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText(yes)).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());

        // Вход под вторым логином
        onView(withId(R.id.login_spinner)).perform(clearText(), typeText(login2));
        onView(withId(R.id.auth_password_edit_text)).perform(typeText(password));
        onView(withId(R.id.btn_send_auth)).perform(click());

        onView(withId(R.id.drawer_cont)).perform(DrawerActions.open());
        onView(withText(R.string.drawer_exit)).perform(click());
        onView(withText(R.string.dialog_change_user)).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText(yes)).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());

        // Выбор первого логина из списка
        int[] location = getViewLocation(activityRule.getActivity().findViewById(R.id.login_spinner));
        int x = location[2] - 20;
        int y = location[3] - 30;
        onView(withId(R.id.login_spinner)).perform(clickXY(x, y));
        onData(allOf(is(instanceOf(String.class)), is(login))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.auth_password_edit_text)).perform(clearText(), typeText(password));
        onView(withId(R.id.btn_send_auth)).perform(click());

        // Тест бокового меню
        onView(withId(R.id.drawer_cont)).perform(DrawerActions.open());
        onView(withText(R.string.drawer_sync)).perform(click());
        onView(withId(R.id.toolbar_view_title)).check(matches(withText(R.string.sync_screen)));
        onView(withText(login)).check(matches(isDisplayed()));
        onView(withSubstring("Наличие незавершённой анкеты в очереди на продолжение")).check(matches(isDisplayed()));
        onView(withSubstring("Количество неотправленных анкет")).check(matches(isDisplayed()));
        onView(withSubstring("Количество неотправленных аудио файлов")).check(matches(isDisplayed()));
        onView(withSubstring("Количество неотправленных фото файлов")).check(matches(isDisplayed()));

        onView(withId(R.id.drawer_cont)).perform(DrawerActions.open());
        onView(withText(R.string.drawer_settings)).perform(click());
        onView(withId(R.id.toolbar_view_title)).check(matches(withText(R.string.settings_screen)));
        onView(withText(R.string.view_config_title)).check(matches(isDisplayed()));
        onView(withSubstring("ID:")).check(matches(isDisplayed()));
        onView(withSubstring("Дата:")).check(matches(isDisplayed()));
        onView(withText(R.string.view_display_settings_speed)).check(matches(isDisplayed()));
        onView(withText(R.string.view_switch_speed)).check(matches(isDisplayed()));
        onView(withText(R.string.view_switch_memory)).check(matches(isDisplayed()));
        onView(withText(R.string.view_display_settings_title)).check(matches(isDisplayed()));
        onView(withText(R.string.view_switch_auto_zoom)).check(matches(isDisplayed()));
        onView(withText(R.string.view_switch_dark_mode)).check(matches(isDisplayed()));
        onView(withId(R.id.update_config)).perform(scrollTo()).check(matches(withText(R.string.button_get_config)));
        onView(withId(R.id.delete_user)).perform(scrollTo()).check(matches(withText(R.string.button_delete_user)));

        onView(withId(R.id.drawer_cont)).perform(DrawerActions.open());
        onView(withText(R.string.drawer_quotas)).perform(click());
        onView(withId(R.id.toolbar_view_title)).check(matches(withText(R.string.quotas_screen)));
        onView(withText(R.string.view_switch_hide_completed_quotas)).check(matches(isDisplayed()));
        onView(withText(R.string.view_button_info)).check(matches(isDisplayed()));
        onView(withText(R.string.view_button_hide_details)).check(matches(isDisplayed()));
        onView(withText(R.string.view_button_refresh)).check(matches(isDisplayed()));

        onView(withId(R.id.drawer_cont)).perform(DrawerActions.open());
        onView(withText(R.string.drawer_about)).perform(click());
        onView(withId(R.id.toolbar_view_title)).check(matches(withText(R.string.about_screen)));
        onView(withText(R.string._7_909_214_48_33)).check(matches(isDisplayed()));
        onView(withText(R.string.e_mail_sales_quizer_pro)).check(matches(isDisplayed()));
        onView(withSubstring("Версия:")).check(matches(isDisplayed()));

        onView(withId(R.id.drawer_cont)).perform(DrawerActions.open());
        onView(withText(R.string.drawer_home)).perform(click());
        onView(withId(R.id.toolbar_view_title)).check(matches(withText(R.string.home_screen)));

        // Квоты
        onView(withId(R.id.btn_quotas)).perform(click());
        onView(withId(R.id.toolbar_view_title)).check(matches(withText(R.string.quotas_screen)));
        onView(withId(R.id.drawer_cont)).perform(DrawerActions.open());
        onView(withText(R.string.drawer_home)).perform(click());
        onView(withId(R.id.toolbar_view_title)).check(matches(withText(R.string.home_screen)));

        // Настройки
        onView(withId(R.id.drawer_cont)).perform(DrawerActions.open());
        onView(withText(R.string.drawer_settings)).perform(click());
        onView(withId(R.id.toolbar_view_title)).check(matches(withText(R.string.settings_screen)));
        onView(withId(R.id.auto_zoom_switch)).check(matches(withText(R.string.view_switch_auto_zoom))).perform(click());
        onView(isRoot()).perform(pressKey(KeyEvent.KEYCODE_VOLUME_DOWN));
        onView(isRoot()).perform(pressKey(KeyEvent.KEYCODE_VOLUME_DOWN));
        onView(isRoot()).perform(pressKey(KeyEvent.KEYCODE_VOLUME_DOWN));
        onView(isRoot()).perform(pressKey(KeyEvent.KEYCODE_VOLUME_DOWN));
        int[] locationSettingsIdTitle = getViewLocation(activityRule.getActivity().findViewById(R.id.settings_id));
        int smallest = locationSettingsIdTitle[3];
        onView(isRoot()).perform(pressKey(KeyEvent.KEYCODE_VOLUME_UP));
        locationSettingsIdTitle = getViewLocation(activityRule.getActivity().findViewById(R.id.settings_id));
        int small = locationSettingsIdTitle[3];
        assertTrue(small > smallest);
        onView(isRoot()).perform(pressKey(KeyEvent.KEYCODE_VOLUME_UP));
        locationSettingsIdTitle = getViewLocation(activityRule.getActivity().findViewById(R.id.settings_id));
        int medium = locationSettingsIdTitle[3];
        assertTrue(medium > small);
        onView(isRoot()).perform(pressKey(KeyEvent.KEYCODE_VOLUME_UP));
        locationSettingsIdTitle = getViewLocation(activityRule.getActivity().findViewById(R.id.settings_id));
        int big = locationSettingsIdTitle[3];
        assertTrue(big > medium);
        onView(isRoot()).perform(pressKey(KeyEvent.KEYCODE_VOLUME_UP));
        locationSettingsIdTitle = getViewLocation(activityRule.getActivity().findViewById(R.id.settings_id));
        int biggest = locationSettingsIdTitle[3];
        assertTrue(biggest > big);



        onView(withId(R.id.auto_zoom_switch)).perform(scrollTo()).check(matches(isDisplayed())).perform(click());

        runOnUiThread(() -> getInstrumentation().callActivityOnPause(activityRule.getActivity()));
        Thread.sleep(2000);
        runOnUiThread(() -> getInstrumentation().callActivityOnResume(activityRule.getActivity()));

//        // Инфо
//        onView(withId(R.id.btn_info)).perform(click());
//        Thread.sleep(1000);
//        onView(withId(R.id.cont)).check(matches(isDisplayed()));
//        onView(withId(R.id.login_title)).check(matches(withText("Данные в рамках логина:")));
//        onView(withId(R.id.device_title)).check(matches(withText("Данные по устройству: (логин: tartest)")));
//
//        // Назад на Главный экран
//        Espresso.pressBack();
//        Thread.sleep(1000);
//
//        // Начинаем новую анкету
//        if (viewIsDisplayed(R.id.btn_delete)) {
//            onView(withId(R.id.btn_delete)).check(matches(isDisplayed()));
//            onView(withId(R.id.btn_delete)).perform(click());
//            onView(withText(yes)).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
//            Thread.sleep(1000);
//        }
//
//        onView(withId(R.id.btn_start)).check(matches(isDisplayed()));
//        onView(withId(R.id.btn_start)).perform(click());
//        Thread.sleep(1000);
//        // Экран Анкеты
//        onView(withId(R.id.cont_element_fragment)).check(matches(isDisplayed()));
//        onView(withId(R.id.toolbar_view_card)).check(matches(isDisplayed()));
//        onView(withId(R.id.toolbar_view_card)).perform(click());
//
//        // Карточка
//        onView(withId(R.id.card_cont)).check(matches(isDisplayed()));
//        onView(withId(R.id.view_close)).check(matches(isDisplayed()));
//        onView(withId(R.id.card_list)).check(matches(isDisplayed()));
//
//        onData(anything()).inAdapterView(withId(R.id.card_list)).atPosition(0).perform(click());
//
//        // Окно ввода текста
//        if (viewIsDisplayed(R.id.input_cont)) {
//            onView(withId(R.id.input_answer)).check(matches(isDisplayed()));
//            onView(withId(R.id.view_ok)).check(matches(isDisplayed()));
//            onView(withId(R.id.input_answer)).perform(replaceText(answer1));
//            onView(withId(R.id.view_ok)).perform(click());
//        }
//
//        Thread.sleep(1000);
//
//        onData(anything()).inAdapterView(withId(R.id.card_list)).atPosition(0).onChildView(withId(R.id.card_input)).check(matches(withText(answer1)));
//        onData(anything()).inAdapterView(withId(R.id.card_list)).atPosition(0).onChildView(withId(R.id.checker))
//                .check(matches(withImageVectorDrawable(R.drawable.radio_button_checked)));
//
//        onView(withId(R.id.view_close)).perform(click());
//
//        // Экран Анкеты
//        Thread.sleep(1000);
//        onView(withId(R.id.answers_recyclerview)).check(matches(atPosition(0, hasDescendant(withText(answer1)))));
//
//        onView(withId(R.id.exit_btn)).check(matches(isDisplayed()));
//        onView(withId(R.id.exit_btn)).perform(click());
//
//        // Диалог выхода
//        onView(withText(yes)).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());

        Thread.sleep(3000);
    }

    public static boolean viewIsDisplayed(int viewId) {
        final boolean[] isDisplayed = {true};
        onView(withId(viewId)).withFailureHandler((error, viewMatcher) -> isDisplayed[0] = false).check(matches(isDisplayed()));
        return isDisplayed[0];
    }

    private void allowPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= 23) {
            UiDevice device = UiDevice.getInstance(getInstrumentation());
            UiObject allowPermissions = device.findObject(new UiSelector().text("ALLOW"));
            if (allowPermissions.exists()) {
                try {
                    allowPermissions.click();
                } catch (UiObjectNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static ViewAction clickXY(final int x, final int y) {
        return new GeneralClickAction(
                Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {

                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);

                        final float screenX = screenPos[0] + x;
                        final float screenY = screenPos[1] + y;
                        float[] coordinates = {screenX, screenY};

                        return coordinates;
                    }
                },
                Press.FINGER);
    }

    private int[] getViewLocation(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int[] locationFull = new int[4];
        locationFull[0] = location[0];
        locationFull[1] = location[1];
        locationFull[2] = view.getWidth();
        locationFull[3] = view.getHeight();

        return locationFull;
    }
}
