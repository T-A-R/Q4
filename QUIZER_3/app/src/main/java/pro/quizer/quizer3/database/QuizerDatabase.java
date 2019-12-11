package pro.quizer.quizer3.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import pro.quizer.quizer3.database.models.ActivationModelR;
import pro.quizer.quizer3.database.models.AppLogsR;
import pro.quizer.quizer3.database.models.CrashLogs;
import pro.quizer.quizer3.database.models.CurrentQuestionnaireR;
import pro.quizer.quizer3.database.models.ElementContentsR;
import pro.quizer.quizer3.database.models.ElementDatabaseModelR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.ElementOptionsR;
import pro.quizer.quizer3.database.models.ElementPassedR;
import pro.quizer.quizer3.database.models.ElementStatusImageR;
import pro.quizer.quizer3.database.models.PrevElementsR;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
import pro.quizer.quizer3.database.models.SmsItemR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.database.models.WarningsR;

@Database(entities = {ActivationModelR.class,
        ElementDatabaseModelR.class,
        QuestionnaireDatabaseModelR.class,
        UserModelR.class,
        AppLogsR.class,
        CrashLogs.class,
        WarningsR.class,
        ElementItemR.class,
        ElementOptionsR.class,
        ElementContentsR.class,
        ElementStatusImageR.class,
        CurrentQuestionnaireR.class,
        ElementPassedR.class,
        PrevElementsR.class,
        SmsItemR.class}, version = 6)
public abstract class QuizerDatabase extends RoomDatabase {
    public abstract QuizerDao getQuizerDao();
}
