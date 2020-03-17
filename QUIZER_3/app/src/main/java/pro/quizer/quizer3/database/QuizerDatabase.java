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
import pro.quizer.quizer3.database.models.OptionsR;
import pro.quizer.quizer3.database.models.PrevElementsR;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
import pro.quizer.quizer3.database.models.TokensCounterR;
import pro.quizer.quizer3.database.models.SettingsR;
import pro.quizer.quizer3.database.models.SmsItemR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.database.models.WarningsR;

@Database(entities = {
        AppLogsR.class,
        SmsItemR.class,
        OptionsR.class,
        CrashLogs.class,
        WarningsR.class,
        SettingsR.class,
        UserModelR.class,
        ElementItemR.class,
        TokensCounterR.class,
        PrevElementsR.class,
        ElementPassedR.class,
        ElementOptionsR.class,
        ElementContentsR.class,
        ActivationModelR.class,
        ElementStatusImageR.class,
        CurrentQuestionnaireR.class,
        ElementDatabaseModelR.class,
        QuestionnaireDatabaseModelR.class}, version = 36)
public abstract class QuizerDatabase extends RoomDatabase {
    public abstract QuizerDao getQuizerDao();
}
