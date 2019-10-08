package pro.quizer.quizer3.database;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import pro.quizer.quizer3.database.models.ElementContentsR;

import static pro.quizer.quizer3.MainActivity.TAG;

public class ElementContentsRConverter {
    @TypeConverter
    public static ElementContentsR fromSting(String value) {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        ElementContentsR elementContentsR = null;

        try {
            elementContentsR = gsonBuilder.create().fromJson(value, ElementContentsR.class);
        } catch (final Exception pE) {
            Log.d(TAG, "ElementContentsRConverter: error converting json to CategoryR");
        }

        return elementContentsR;
    }
    @TypeConverter
    public static String fromElementContentsR(ElementContentsR elementContentsR) {
        Gson gson = new Gson();
        String json = gson.toJson(elementContentsR);
        return json;
    }
}
