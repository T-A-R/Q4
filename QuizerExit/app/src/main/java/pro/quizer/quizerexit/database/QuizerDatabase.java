package pro.quizer.quizerexit.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import pro.quizer.quizerexit.database.model.ActivationModelR;
import pro.quizer.quizerexit.database.model.AppLogsR;
import pro.quizer.quizerexit.database.model.CategoryR;
import pro.quizer.quizerexit.database.model.CrashLogs;
import pro.quizer.quizerexit.database.model.ElementDatabaseModelR;
import pro.quizer.quizerexit.database.model.ItemR;
import pro.quizer.quizerexit.database.model.OptionsR;
import pro.quizer.quizerexit.database.model.QuestionnaireDatabaseModelR;
import pro.quizer.quizerexit.database.model.SmsItemR;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.database.model.WarningsR;


@Database(entities = {ActivationModelR.class,
        ElementDatabaseModelR.class,
        ItemR.class,
        CategoryR.class,
        QuestionnaireDatabaseModelR.class,
        UserModelR.class,
        AppLogsR.class,
        CrashLogs.class,
        WarningsR.class,
        OptionsR.class,
        SmsItemR.class}, version = 11)
public abstract class QuizerDatabase extends RoomDatabase {
    public abstract QuizerDao getQuizerDao();
}
