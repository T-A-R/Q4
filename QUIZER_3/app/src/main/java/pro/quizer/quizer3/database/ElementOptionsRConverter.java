package pro.quizer.quizer3.database;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import pro.quizer.quizer3.database.models.ElementOptionsR;

import static pro.quizer.quizer3.MainActivity.TAG;

public class ElementOptionsRConverter {
    @TypeConverter
    public static ElementOptionsR fromSting(String value) {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        ElementOptionsR elementOptionsR = null;

        try {
            elementOptionsR = gsonBuilder.create().fromJson(value, ElementOptionsR.class);
        } catch (final Exception pE) {
            Log.d(TAG, "ElementContentsRConverter: error converting json to CategoryR");
        }

        return elementOptionsR;
    }
    @TypeConverter
    public static String fromElementOptionsR(ElementOptionsR elementOptionsR) {
        Gson gson = new Gson();
        String json = gson.toJson(elementOptionsR);
        return json;
    }
}
