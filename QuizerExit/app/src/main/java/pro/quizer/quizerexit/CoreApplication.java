package pro.quizer.quizerexit;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

public class CoreApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}
