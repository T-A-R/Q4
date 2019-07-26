package pro.quizer.quizerexit.database;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import pro.quizer.quizerexit.database.model.CategoryR;;

import static pro.quizer.quizerexit.activity.BaseActivity.TAG;

public class CategoryConverter {
    @TypeConverter
    public static CategoryR fromSting(String value) {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        CategoryR categoryR = null;

        try {
            categoryR = gsonBuilder.create().fromJson(value, CategoryR.class);
        } catch (final Exception pE) {
            Log.d(TAG, "CategoryConverter: error converting json to CategoryR");
        }

        return categoryR;
    }
    @TypeConverter
    public static String fromCategory(CategoryR category) {
        Gson gson = new Gson();
        String json = gson.toJson(category);
        return json;
    }
}
