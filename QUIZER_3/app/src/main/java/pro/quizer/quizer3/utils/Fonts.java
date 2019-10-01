package pro.quizer.quizer3.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

public class Fonts {

    static private Typeface futuraPtBook;
    static private Typeface futuraPtMedium;

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
