package pro.quizer.quizer3.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import pro.quizer.quizer3.database.models.ActivationModelR;
import pro.quizer.quizer3.database.models.AddressR;
import pro.quizer.quizer3.database.models.AppLogsR;
import pro.quizer.quizer3.database.models.CrashLogs;
import pro.quizer.quizer3.database.models.SavedElementPassedR;
import pro.quizer.quizer3.database.models.CurrentQuestionnaireR;
import pro.quizer.quizer3.database.models.ElementContentsR;
import pro.quizer.quizer3.database.models.ElementDatabaseModelR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.ElementOptionsR;
import pro.quizer.quizer3.database.models.ElementPassedR;
import pro.quizer.quizer3.database.models.ElementStatusImageR;
import pro.quizer.quizer3.database.models.EncryptionTableR;
import pro.quizer.quizer3.database.models.InterStateR;
import pro.quizer.quizer3.database.models.OnlineQuotaR;
import pro.quizer.quizer3.database.models.OptionsR;
import pro.quizer.quizer3.database.models.PhotoAnswersR;
import pro.quizer.quizer3.database.models.PointR;
import pro.quizer.quizer3.database.models.PrevElementsR;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
import pro.quizer.quizer3.database.models.QuotaR;
import pro.quizer.quizer3.database.models.RegistrationR;
import pro.quizer.quizer3.database.models.RouteR;
import pro.quizer.quizer3.database.models.SelectedRoutesR;
import pro.quizer.quizer3.database.models.SmsAnswersR;
import pro.quizer.quizer3.database.models.SmsReportR;
import pro.quizer.quizer3.database.models.StatisticR;
import pro.quizer.quizer3.database.models.TokensCounterR;
import pro.quizer.quizer3.database.models.SettingsR;
import pro.quizer.quizer3.database.models.SmsItemR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.database.models.WarningsR;

@Database(entities = {
        PointR.class,
        QuotaR.class,
        RouteR.class,
        AddressR.class,
        AppLogsR.class,
        SmsItemR.class,
        OptionsR.class,
        CrashLogs.class,
        WarningsR.class,
        SettingsR.class,
        SmsReportR.class,
        UserModelR.class,
        StatisticR.class,
        SmsAnswersR.class,
        InterStateR.class,
        OnlineQuotaR.class,
        ElementItemR.class,
        RegistrationR.class,
        PrevElementsR.class,
        PhotoAnswersR.class,
        TokensCounterR.class,
        ElementPassedR.class,
        SelectedRoutesR.class,
        ElementOptionsR.class,
        EncryptionTableR.class,
        ElementContentsR.class,
        ActivationModelR.class,
        ElementStatusImageR.class,
        SavedElementPassedR.class,
        CurrentQuestionnaireR.class,
        ElementDatabaseModelR.class,
        QuestionnaireDatabaseModelR.class}, version = 160       )
@TypeConverters({ListStringConverter.class, ListIntConverter.class})

public abstract class QuizerDatabase extends RoomDatabase {

    public abstract QuizerDao getQuizerDao();

}
