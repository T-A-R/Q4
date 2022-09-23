package pro.quizer.quizer3.database;

import android.util.Log;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ListIntConverter {
    @TypeConverter
    public static List<Integer> fromString(String value) {
        Type listType = new TypeToken<List<Integer>>() {}.getType();
        Log.d("T-A-R.ListStringConv", "fromString: " + value);
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<Integer> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
