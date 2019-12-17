package pro.quizer.quizer3.database;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.database.models.ElementContentsR;

import static pro.quizer.quizer3.MainActivity.TAG;

public class ElementContentsRConverter {
    @TypeConverter
    public static List<ElementContentsR> fromSting(String value) {
        Type listType = new TypeToken<ArrayList<ElementContentsR>>() {}.getType();
//        final GsonBuilder gsonBuilder = new GsonBuilder();
//        ElementContentsR elementContentsR = null;
//
//        try {
//            elementContentsR = gsonBuilder.create().fromJson(value, ElementContentsR.class);
//        } catch (final Exception pE) {
//            Log.d(TAG, "ElementContentsRConverter: error converting json to CategoryR");
//        }
//
//        return elementContentsR;
//        Log.d(TAG, "???????????????: " + value);
        return new Gson().fromJson(value, listType);
    }
    @TypeConverter
    public static String fromElementContentsR(List<ElementContentsR> elementContentsR) {
        Gson gson = new Gson();
        String json = gson.toJson(elementContentsR);
        return json;
    }
}
