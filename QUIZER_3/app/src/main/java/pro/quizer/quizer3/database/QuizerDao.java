package pro.quizer.quizer3.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import pro.quizer.quizer3.database.models.ActivationModelR;
import pro.quizer.quizer3.database.models.AppLogsR;
import pro.quizer.quizer3.database.models.CrashLogs;
import pro.quizer.quizer3.database.models.CurrentQuestionnaireR;
import pro.quizer.quizer3.database.models.ElementContentsR;
import pro.quizer.quizer3.database.models.ElementDatabaseModelR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.ElementOptionsR;
import pro.quizer.quizer3.database.models.ElementPassedR;
import pro.quizer.quizer3.database.models.OptionsR;
import pro.quizer.quizer3.database.models.PrevElementsR;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
import pro.quizer.quizer3.database.models.QuotaR;
import pro.quizer.quizer3.database.models.SettingsR;
import pro.quizer.quizer3.database.models.SmsItemR;
import pro.quizer.quizer3.database.models.TokensCounterR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.database.models.WarningsR;

@Dao
public interface QuizerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertActivationModelR(ActivationModelR activationModelR);

    @Query("SELECT * FROM ActivationModelR")
    List<ActivationModelR> getActivationModelR();

    @Query("DELETE FROM ActivationModelR")
    void clearActivationModelR();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertElementItemR(ElementItemR elementItemR);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertListElementItemR(List<ElementItemR> elementItemR);

    @Query("SELECT * FROM ElementItemR")
    List<ElementItemR> getAllElementItemR();

    @Query("SELECT * FROM ElementItemR")
    List<ElementItemR> getCurrentElements();

    @Query("SELECT * FROM ElementItemR WHERE userId =:user_id")
    ElementItemR getElementsByUserId(Integer user_id);

    @Query("SELECT * FROM ElementItemR LIMIT 1")
    ElementItemR getOneElement();

    @Query("SELECT * FROM ElementItemR WHERE relative_parent_id =:quotaBlockId")
    List<ElementItemR> getQuotaElements(Integer quotaBlockId);

    @Query("UPDATE ElementItemR SET was_shown =:was_shown WHERE relative_id =:id AND userId =:user_id AND projectId = :project_id")
    void setWasElementShown(boolean was_shown, Integer id, Integer user_id, Integer project_id);

    @Query("UPDATE ElementItemR SET shown_at_id =:shown_id WHERE relative_id =:id AND userId =:user_id AND projectId = :project_id")
    void setShownId(Integer shown_id, Integer id, Integer user_id, Integer project_id);

    @Query("UPDATE ElementItemR SET enabled =:enabled WHERE relative_id =:id AND userId =:user_id AND projectId = :project_id")
    void setElementEnabled(boolean enabled, Integer id, Integer user_id, Integer project_id);

    @Query("UPDATE ElementItemR SET was_shown =:was_shown")
    void clearWasElementShown(boolean was_shown);

    @Query("SELECT * FROM ElementItemR WHERE relative_id =:id")
    ElementItemR getElementById(Integer id);

    @Query("SELECT * FROM ElementItemR WHERE relative_parent_id =:id")
    List<ElementItemR> getChildElements(Integer id);

    @Query("DELETE FROM ElementItemR")
    void clearElementItemR();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserModelR userModelR);

    @Query("UPDATE UserModelR SET password = :password, login = :login, config_id = :configId, role_id = :roleId, user_project_id = :userProjectId WHERE user_id = :userId")
    void updateUserModelR(String login, String password, String configId, int roleId, int userProjectId, int userId);

    @Query("UPDATE UserModelR SET questionnaire_opened = :wasStarted WHERE user_id = :userId")
    void updateQuestionnaireStart(boolean wasStarted, int userId);

    @Query("UPDATE UserModelR SET config = :config WHERE user_id = :userId AND user_project_id = :userProjectId")
    void updateConfig(String config, int userId, int userProjectId);

    @Query("UPDATE UserModelR SET quotas = :quotas WHERE user_project_id = :userProjectId")
    void updateQuotas(String quotas, int userProjectId);

    @Query("SELECT * FROM UserModelR")
    List<UserModelR> getAllUsers();

    @Query("SELECT * FROM UserModelR WHERE login = :login AND password = :password")
    List<UserModelR> getLocalUserModel(String login, String password);

    @Query("SELECT * FROM UserModelR WHERE user_id = :userId LIMIT 1")
    UserModelR getUserByUserId(int userId);

    @Query("SELECT * FROM UserModelR WHERE questionnaire_opened = :status")
    List<UserModelR> getUserWithAbortedQUestionnaire(boolean status);

    @Query("DELETE FROM UserModelR WHERE user_id = :userId")
    void deleteUserByUserId(int userId);

    @Query("DELETE FROM UserModelR")
    void clearUserModelR();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertElement(ElementDatabaseModelR elementDatabaseModelR);

    @Query("SELECT * FROM ElementDatabaseModelR WHERE token = :token")
    List<ElementDatabaseModelR> getElementByToken(String token);

    @Query("UPDATE ElementDatabaseModelR SET send_sms = :send_sms WHERE relative_id = :relative_id")
    void setElementSendSms(boolean send_sms, Integer relative_id);

    @Query("DELETE FROM ElementDatabaseModelR")
    void clearElementDatabaseModelR();

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE user_id = :userId AND status = :status")
    List<QuestionnaireDatabaseModelR> getQuestionnaireByUserIdWithStatus(int userId, String status);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE status = :status AND user_id = :userId AND user_project_id = :projectId AND survey_status = :surveyStatus")
    List<QuestionnaireDatabaseModelR> getQuestionnaireForQuotas(int userId, int projectId, String status, String surveyStatus);

    //TODO RENAME TO setQuestionnaireStatusByToken
    @Query("UPDATE QuestionnaireDatabaseModelR SET status = :status WHERE token = :token")
    void setQuestionnaireStatus(String status, String token);

    @Query("UPDATE QuestionnaireDatabaseModelR SET send_sms = :send_sms WHERE token = :token")
    void setQuestionnaireSendSms(boolean send_sms, String token);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE status = :status")
    List<QuestionnaireDatabaseModelR> getQuestionnaireByStatus(String status);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE user_id = :userId AND survey_status = :survey AND status = :status")
    List<QuestionnaireDatabaseModelR> getQuestionnaireSurveyStatus(int userId, String survey, String status);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE user_id = :userId AND status = :status AND send_sms = :send_sms AND survey_status = :survey")
    List<QuestionnaireDatabaseModelR> getQuestionnaireForStage(int userId, String status, String survey, boolean send_sms);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE user_id = :userId AND status = :status AND date_interview >= :timeFrom AND date_interview <= :timeTo")
    List<QuestionnaireDatabaseModelR> getQuestionnaireWithTime(int userId, String status, long timeFrom, long timeTo);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE user_id = :userId")
    List<QuestionnaireDatabaseModelR> getQuestionnaireByUserId(int userId);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE status = :status AND user_id = :userId AND user_project_id = :userProjectId")
    List<QuestionnaireDatabaseModelR> getQuestionnaireByUserIdAndProjectIdWithStatus(int userId, int userProjectId, String status);

    @Query("UPDATE QuestionnaireDatabaseModelR SET status = :status WHERE user_id = :userId")
    void setQuestionnaireStatusByUserId(String status, int userId);

    @Query("DELETE FROM QuestionnaireDatabaseModelR WHERE token = :token")
    void deleteQuestionnaireByToken(String token);

    @Query("DELETE FROM QuestionnaireDatabaseModelR WHERE user_id = :userId")
    void deleteQuestionnaireStatusByUserId(int userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuestionnaire(QuestionnaireDatabaseModelR questionnaireDatabaseModelR);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR")
    List<QuestionnaireDatabaseModelR> getAllQuestionnaires();

    @Query("DELETE FROM QuestionnaireDatabaseModelR")
    void clearQuestionnaireDatabaseModelR();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAppLogsR(AppLogsR appLogsR);

    @Query("SELECT * FROM AppLogsR")
    List<AppLogsR> getAppLogsR();

    @Query("SELECT * FROM AppLogsR WHERE login = :login")
    List<AppLogsR> getAppLogsByLogin(String login);

    @Query("SELECT * FROM AppLogsR WHERE login = :login AND status = :status")
    List<AppLogsR> getAppLogsByLoginWithStatus(String login, String status);

    @Query("SELECT * FROM AppLogsR WHERE status = :status")
    List<AppLogsR> getAllLogsWithStatus(String status);

    @Query("UPDATE AppLogsR SET status = :status WHERE login = :login")
    void setLogsStatusByLogin(String login, String status);

    @Query("UPDATE AppLogsR SET status = :status")
    void setLogsStatus(String status);

    @Query("DELETE FROM AppLogsR")
    void clearAppLogsR();

    @Query("DELETE FROM AppLogsR WHERE login = :login")
    void clearAppLogsByLogin(String login);

    @Query("DELETE FROM CrashLogs")
    void clearCrashLogs();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCrashLog(CrashLogs crashLogs);

    @Query("SELECT * FROM CrashLogs")
    List<CrashLogs> getCrashLogs();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSmsItem(SmsItemR smsItemR);

    @Query("UPDATE SmsItemR SET smsStatus = :status WHERE smsNumber = :smsNumber")
    void setSmsItemStatusBySmsNumber(String smsNumber, String status);

    @Query("SELECT * FROM SmsItemR")
    List<SmsItemR> getSmsItems();

    @Query("SELECT * FROM SmsItemR WHERE smsNumber = :smsNumber")
    List<SmsItemR> getSmsItemBySmsNumber(String smsNumber);

    @Query("DELETE FROM SmsItemR")
    void clearSmsDatabase();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWarning(WarningsR warningsR);

    @Query("DELETE FROM WarningsR")
    void clearWarningsR();

    @Query("UPDATE WarningsR SET warningStatus = :status WHERE warning = :warning")
    void setWarningStatus(String warning, String status);

    @Query("SELECT * FROM WarningsR")
    List<WarningsR> getWarnings();

    @Query("SELECT * FROM WarningsR WHERE warning = :warning AND warningStatus = :status")
    List<WarningsR> getWarningsByStatus(String warning, String status);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertElementPassedR(ElementPassedR elementPassedR);

    @Query("SELECT * FROM ElementPassedR WHERE token =:token")
    List<ElementPassedR> getAllElementsPassedR(String token);

    @Query("SELECT * FROM ElementPassedR")
    List<ElementPassedR> getAllElementsPassedRNoToken();

    @Query("SELECT * FROM ElementPassedR WHERE token =:token AND relative_id =:relative_id LIMIT 1")
    ElementPassedR getElementPassedR(String token, int relative_id);

    @Query("SELECT * FROM ElementPassedR WHERE token =:token ORDER BY id DESC LIMIT 1")
    ElementPassedR getLastElementPassedR(String token);

    @Query("DELETE FROM ElementPassedR WHERE id >=:id")
    void deleteOldElementsPassedR(int id);

    @Query("DELETE FROM ElementPassedR")
    void clearElementPassedR();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCurrentQuestionnaireR(CurrentQuestionnaireR currentQuestionnaireR);

    @Query("SELECT * FROM CurrentQuestionnaireR ORDER BY id DESC LIMIT 1")
    CurrentQuestionnaireR getCurrentQuestionnaireR();

    @Query("UPDATE CurrentQuestionnaireR SET question_start_time = :time")
    void setQuestionTime(Long time);

    @Query("UPDATE CurrentQuestionnaireR SET audio_number = :number")
    void setAudioNumber(int number);

    @Query("UPDATE CurrentQuestionnaireR SET current_element_id = :id ")
    void setCurrentElement(Integer id);

    @Query("UPDATE CurrentQuestionnaireR SET count_interrupted = :counter ")
    void setInterruptedCounter(Integer counter);

    @Query("UPDATE CurrentQuestionnaireR SET paused = :paused ")
    void setCurrentQuestionnairePaused(boolean paused);

    @Query("UPDATE CurrentQuestionnaireR SET in_aborted_box = :status ")
    void setCurrentQuestionnaireInAbortedBox(boolean status);

    @Query("UPDATE CurrentQuestionnaireR SET has_photo = :has_photo ")
    void setCurrentQuestionnairePhoto(boolean has_photo);

    @Query("DELETE FROM CurrentQuestionnaireR")
    void clearCurrentQuestionnaireR();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOption(OptionsR option);

    @Query("SELECT * FROM OptionsR WHERE name = :name LIMIT 1")
    OptionsR getOption(String name);

    @Query("UPDATE OptionsR SET data = :data WHERE name = :name")
    void setOption(String name, String data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSettings(SettingsR settingsR);

    @Query("SELECT * FROM SettingsR LIMIT 1")
    SettingsR getSettings();

    @Query("UPDATE SettingsR SET started = :data")
    void setSettingsStarted(boolean data);

    @Query("UPDATE SettingsR SET auto_zoom = :data")
    void setSettingsAutoZoom(boolean data);

    @Query("UPDATE SettingsR SET table_speed = :data")
    void setSettingsTableSpeed(boolean data);

    @Query("UPDATE SettingsR SET memory_check = :data")
    void setSettingsMemoryCheck(boolean data);

    @Query("UPDATE SettingsR SET dark_mode = :data")
    void setSettingsDarkMode(boolean data);

    @Query("UPDATE SettingsR SET last_quota_time = :data")
    void setLastQuotaTime(Long data);

    @Query("UPDATE SettingsR SET last_quiz_time = :data")
    void setLastQuizTime(Long data);

    @Query("UPDATE SettingsR SET last_sent_quiz_time = :data")
    void setLastSentQuizTime(Long data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertToken(TokensCounterR token);

    @Query("SELECT * FROM TokensCounterR WHERE user_id = :userId")
    List<TokensCounterR> getTokens(int userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPrevElementsR(PrevElementsR prevElementsR);

    @Query("SELECT * FROM PrevElementsR")
    List<PrevElementsR> getPrevElementsR();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setPrevElement(List<PrevElementsR> elementItemR);

    @Query("DELETE FROM PrevElementsR")
    void clearPrevElementsR();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertElementContentsR(List<ElementContentsR> contentsR);

    @Query("SELECT * FROM ElementContentsR WHERE relative_id =:relativeId")
    List<ElementContentsR> getElementContentsR(Integer relativeId);

    @Query("DELETE FROM ElementContentsR")
    void clearElementContentsR();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertElementOptionsR(ElementOptionsR elementOptionsR);

    @Query("SELECT * FROM ElementOptionsR WHERE relative_id =:relativeId LIMIT 1")
    ElementOptionsR getElementOptionsR(Integer relativeId);

    @Query("DELETE FROM ElementOptionsR")
    void clearElementOptionsR();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuotaR(List<QuotaR> quotaRList);

    @Query("SELECT * FROM QuotaR")
    List<QuotaR> getQuotaR();

    @Query("DELETE FROM QuotaR")
    void clearQuotaR();
}
