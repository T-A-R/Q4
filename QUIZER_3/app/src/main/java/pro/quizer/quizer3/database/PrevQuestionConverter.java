package pro.quizer.quizer3.database;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.database.models.ElementContentsR;
import pro.quizer.quizer3.database.models.PrevElementsR;

public class PrevQuestionConverter {
    @TypeConverter
    public static List<PrevElementsR> fromSting(String value) {
        Type listType = new TypeToken<ArrayList<PrevElementsR>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }
    @TypeConverter
    public static String fromElementContentsR(List<PrevElementsR> elementContentsR) {
        Gson gson = new Gson();
        String json = gson.toJson(elementContentsR);
        return json;
    }
}
