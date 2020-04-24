package pro.quizer.quizer3.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.model.FontSizeModel;

public class Fonts {

    static private Typeface futuraPtBook;
    static private Typeface futuraPtMedium;

    public static final List<FontSizeModel> FONT_SIZE_MODELS = new ArrayList<FontSizeModel>() {
        {
            add(new FontSizeModel("Очень маленький", 0.85F));
            add(new FontSizeModel("Маленький", 1.5F));
            add(new FontSizeModel("Средний", 2.0F));
            add(new FontSizeModel("Большой", 4.0F));
            add(new FontSizeModel("Очень большой", 5.0F));
        }
    };

    static public void init(Context context) {
        AssetManager assets = context.getAssets();
        futuraPtMedium  = Typeface.createFromAsset(assets, "futura_pt_medium.ttf");
        futuraPtBook  = Typeface.createFromAsset(assets, "futura_pt_book.ttf");
    }

    static public Typeface getFuturaPtMedium() {
        return futuraPtMedium;
    }

    static public Typeface getFuturaPtBook() {
        return futuraPtBook;
    }
}
