package pro.quizer.quizerexit.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import pro.quizer.quizerexit.database.model.CategoryR;
import pro.quizer.quizerexit.database.model.ItemR;

@Database(entities = {ItemR.class, CategoryR.class}, version = 1)
public abstract class QuizerDatabase extends RoomDatabase {
    public abstract QuizerDao getQuizerDao();
}
