package pro.quizer.quizerexit.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import pro.quizer.quizerexit.database.model.ActivationModelR;
import pro.quizer.quizerexit.database.model.AppLogsR;
import pro.quizer.quizerexit.database.model.CategoryR;
import pro.quizer.quizerexit.database.model.ElementDatabaseModelR;
import pro.quizer.quizerexit.database.model.ItemR;
import pro.quizer.quizerexit.database.model.QuestionnaireDatabaseModelR;
import pro.quizer.quizerexit.database.model.UserModelR;

@Database(entities = {ActivationModelR.class, ElementDatabaseModelR.class, ItemR.class, CategoryR.class, QuestionnaireDatabaseModelR.class, UserModelR.class, AppLogsR.class}, version = 3)
public abstract class QuizerDatabase extends RoomDatabase {
    public abstract QuizerDao getQuizerDao();
}
