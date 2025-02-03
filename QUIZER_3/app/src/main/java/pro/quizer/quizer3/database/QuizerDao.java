package pro.quizer.quizer3.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import pro.quizer.quizer3.database.models.ActivationModelR;
import pro.quizer.quizer3.database.models.AddressR;
import pro.quizer.quizer3.database.models.AppLogsR;
import pro.quizer.quizer3.database.models.CrashLogs;
import pro.quizer.quizer3.database.models.CurrentQuestionnaireR;
import pro.quizer.quizer3.database.models.ElementContentsR;
import pro.quizer.quizer3.database.models.ElementDatabaseModelR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.ElementOptionsR;
import pro.quizer.quizer3.database.models.ElementPassedR;
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
import pro.quizer.quizer3.database.models.SavedElementPassedR;
import pro.quizer.quizer3.database.models.SelectedRoutesR;
import pro.quizer.quizer3.database.models.SettingsR;
import pro.quizer.quizer3.database.models.SmsAnswersR;
import pro.quizer.quizer3.database.models.SmsItemR;
import pro.quizer.quizer3.database.models.SmsReportR;
import pro.quizer.quizer3.database.models.StatisticR;
import pro.quizer.quizer3.database.models.TokensCounterR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.database.models.WarningsR;

@Dao
public interface QuizerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertActivationModelR(ActivationModelR activationModelR);

    @Query("SELECT * FROM ActivationModelR")
    List<ActivationModelR> getActivationModelR();

    @Query("SELECT `key` FROM ActivationModelR LIMIT 1")
    String getKey();

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

    @Query("UPDATE UserModelR SET phone = :phone WHERE user_id = :userId")
    void updateUserPhone(String phone, int userId);

    @Query("UPDATE UserModelR SET config = :config WHERE user_id = :userId AND user_project_id = :userProjectId")
    void updateConfig(String config, int userId, int userProjectId);

    @Query("UPDATE UserModelR SET config_new = :config WHERE user_id = :userId AND user_project_id = :userProjectId")
    void updateNewConfig(String config, int userId, int userProjectId);

    @Query("UPDATE UserModelR SET quotas = :quotas WHERE user_project_id = :userProjectId")
    void updateQuotas(String quotas, int userProjectId);

    @Query("SELECT id, login, password, user_id, user_project_id, questionnaire_opened FROM UserModelR")
    List<UserModelR> getAllUsers();

    @Query("SELECT * FROM UserModelR WHERE login = :login AND password = :password")
    List<UserModelR> getLocalUserModel(String login, String password);

    @Query("SELECT * FROM UserModelR WHERE user_id = :userId LIMIT 1")
    UserModelR getUserByUserId(int userId);

    @Query("SELECT COUNT(id) FROM UserModelR")
    LiveData<Integer> getUserCount();

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

    @Query("SELECT * FROM ElementDatabaseModelR WHERE token = :token AND send_sms = :sms_status")
    List<ElementDatabaseModelR> getElementByTokenAndSmsStatus(String token, boolean sms_status);

    @Query("UPDATE ElementDatabaseModelR SET send_sms = :send_sms WHERE relative_id = :relative_id AND token = :token")
    void setElementSendSms(boolean send_sms, Integer relative_id, String token);

    @Query("DELETE FROM ElementDatabaseModelR")
    void clearElementDatabaseModelR();

    @Query("DELETE FROM ElementDatabaseModelR WHERE token = :token")
    void deleteElementDatabaseModelByToken(String token);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE user_id = :userId AND status = :status")
    List<QuestionnaireDatabaseModelR> getQuestionnaireByUserIdWithStatus(int userId, String status);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE status = :status AND user_id = :userId AND user_project_id = :projectId AND survey_status = :surveyStatus")
    List<QuestionnaireDatabaseModelR> getQuestionnaireForQuotas(int userId, int projectId, String status, String surveyStatus);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE status = :status AND user_id = :userId AND user_project_id = :projectId AND (survey_status = :surveyStatus1 or survey_status = :surveyStatus2)")
    List<QuestionnaireDatabaseModelR> getQuestionnaireForQuotas(int userId, int projectId, String status, String surveyStatus1, String surveyStatus2);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE status = :status AND user_id = :userId AND user_project_id = :projectId AND survey_status = :surveyStatus AND user_name = :name AND (user_date = :user_date or (user_date is null and :user_date is null))")
    List<QuestionnaireDatabaseModelR> getQuestionnaireForQuotasByUser(int userId, int projectId, String status, String surveyStatus, String name, String user_date);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE status = :status AND user_id = :userId AND user_project_id = :projectId AND (survey_status = :surveyStatus1 or survey_status = :surveyStatus2) AND user_name = :name AND (user_date = :user_date or (user_date is null and :user_date is null))")
    List<QuestionnaireDatabaseModelR> getQuestionnaireForQuotasByUser(int userId, int projectId, String status, String surveyStatus1, String surveyStatus2, String name, String user_date);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE status = :status AND user_id = :userId AND user_project_id = :projectId AND (survey_status = :surveyStatus1 or survey_status = :surveyStatus2) AND questionnaire_route_id = :routeId AND on_route = :on_route")
    List<QuestionnaireDatabaseModelR> getQuestionnaireWithRoutes(int userId, int projectId, String status, String surveyStatus1, String surveyStatus2, Integer routeId, boolean on_route);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE status = :status AND user_id = :userId AND user_project_id = :projectId AND (survey_status = :surveyStatus1 or survey_status = :surveyStatus2) AND questionnaire_route_id = :routeId")
    List<QuestionnaireDatabaseModelR> getQuestionnaireWithRoutes(int userId, int projectId, String status, String surveyStatus1, String surveyStatus2, Integer routeId);

    //TODO RENAME TO setQuestionnaireStatusByToken
    @Query("UPDATE QuestionnaireDatabaseModelR SET status = :status WHERE token = :token")
    void setQuestionnaireStatus(String status, String token);

    @Query("UPDATE QuestionnaireDatabaseModelR SET is_online = :status")
    void setQuestionnaireSentOnline(Boolean status);

    @Query("UPDATE QuestionnaireDatabaseModelR SET send_sms = :send_sms WHERE token = :token")
    void setQuestionnaireSendSms(boolean send_sms, String token);

    @Query("UPDATE QuestionnaireDatabaseModelR SET sent_sms = :sent_sms WHERE token = :token")
    void setQuestionnaireSentSms(String sent_sms, String token);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE status = :status")
    List<QuestionnaireDatabaseModelR> getQuestionnaireByStatus(String status);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE user_id = :userId AND survey_status = :survey AND status = :status")
    List<QuestionnaireDatabaseModelR> getQuestionnaireSurveyStatus(int userId, String survey, String status);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE user_id = :userId AND (survey_status = :surveyStatus1 or survey_status = :surveyStatus2) AND status = :status")
    List<QuestionnaireDatabaseModelR> getQuestionnaireSurveyStatus(int userId, String surveyStatus1, String surveyStatus2, String status);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE user_id = :userId AND survey_status = :survey AND status = :status AND user_name = :name AND (user_date = :user_date or (user_date is null and :user_date is null))")
    List<QuestionnaireDatabaseModelR> getQuestionnaireByStatusAndName(int userId, String name, String user_date, String survey, String status);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE user_id = :userId AND (survey_status = :surveyStatus1 or survey_status = :surveyStatus2) AND status = :status AND user_name = :name AND (user_date = :user_date or (user_date is null and :user_date is null))")
    List<QuestionnaireDatabaseModelR> getQuestionnaireByStatusAndName(int userId, String name, String user_date, String surveyStatus1, String surveyStatus2, String status);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE user_id = :userId AND status = :status AND send_sms = :send_sms AND survey_status = :survey")
    List<QuestionnaireDatabaseModelR> getQuestionnaireForStage(int userId, String status, String survey, boolean send_sms);

//    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE user_id = :userId AND status = :status AND send_sms = :sms_sent AND date_interview >= :timeFrom AND date_interview <= :timeTo")
//    List<QuestionnaireDatabaseModelR> getQuestionnaireWithTime(int userId, String status, boolean sms_sent, long timeFrom, long timeTo);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE user_id = :userId AND status = :status AND date_interview >= :timeFrom AND date_interview <= :timeTo")
    List<QuestionnaireDatabaseModelR> getQuestionnaireWithTime(int userId, String status, long timeFrom, long timeTo);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE user_id = :userId")
    List<QuestionnaireDatabaseModelR> getQuestionnaireByUserId(int userId);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE token = :token LIMIT 1")
    QuestionnaireDatabaseModelR getQuestionnaireByToken(String token);

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

    @Query("SELECT * FROM AppLogsR WHERE object =:object AND status = :status")
    List<AppLogsR> getLogsByObjectWithStatus(String object, String status);

    @Query("UPDATE AppLogsR SET status = :status WHERE login = :login")
    void setLogsStatusByLogin(String login, String status);

    @Query("UPDATE AppLogsR SET status = :status WHERE object =:object")
    void setLogsStatusByObject(String object, String status);

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSmsItemList(List<SmsItemR> smsItemsList);

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
    void saveElementPassedR(List<SavedElementPassedR> elements);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertElementPassedR(ElementPassedR elementPassedR);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertElementPassedR(List<ElementPassedR> elementPassedR);

    @Query("SELECT * FROM ElementPassedR WHERE token =:token")
    List<ElementPassedR> getAllElementsPassedR(String token);

    @Query("SELECT * FROM ElementPassedR WHERE token =:token AND from_quotas_block =:from_quotas_block")
    List<ElementPassedR> getQuotaPassedElements(String token, boolean from_quotas_block);

    @Query("SELECT relative_id FROM ElementPassedR WHERE token =:token AND from_quotas_block =:from_quotas_block AND parent_id =:parent_id")
    List<Integer> getQuotaPassedAnswers(String token, boolean from_quotas_block, Integer parent_id);

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

    @Query("SELECT * FROM CurrentQuestionnaireR WHERE config_id = :config_id")
    CurrentQuestionnaireR getCurrentQuestionnaireByConfigId(String config_id);

    @Query("UPDATE CurrentQuestionnaireR SET question_start_time = :time")
    void setQuestionTime(Long time);

    @Query("UPDATE CurrentQuestionnaireR SET audio_number = :number")
    void setAudioNumber(int number);

    @Query("UPDATE CurrentQuestionnaireR SET rotation_state = :state")
    void setRotationState(String state);

    @Query("UPDATE CurrentQuestionnaireR SET current_element_id = :id ")
    void setCurrentElement(Integer id);

    @Query("UPDATE CurrentQuestionnaireR SET count_interrupted = :counter WHERE config_id = :config_id")
    void setInterruptedCounter(String config_id, Integer counter);

    @Query("UPDATE CurrentQuestionnaireR SET paused = :paused ")
    void setCurrentQuestionnairePaused(boolean paused);

    @Query("UPDATE CurrentQuestionnaireR SET in_uik_question = :inUikQuestion ")
    void setCurrentQuestionnaireInUikQuestion(boolean inUikQuestion);

    @Query("UPDATE CurrentQuestionnaireR SET first_element_id = :first_element_id ")
    void setCurrentQuestionnaireFirstElementId(int first_element_id);

    @Query("UPDATE CurrentQuestionnaireR SET registered_uik = :uik")
    void setCurrentQuestionnaireUik(String uik);

    @Query("UPDATE CurrentQuestionnaireR SET is_use_absentee = :is_use_absentee")
    void setCurrentQuestionnaireIsUseAbsentee(boolean is_use_absentee);

    @Query("UPDATE CurrentQuestionnaireR SET in_aborted_box = :status ")
    void setCurrentQuestionnaireInAbortedBox(boolean status);

    @Query("UPDATE CurrentQuestionnaireR SET cond_complete = :status ")
    void setCurrentQuestionnaireCondComplete(boolean status);

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

    @Query("UPDATE SettingsR SET address_database = :ver")
    void setSettingsAddressDBVer(Long ver);

    @Query("UPDATE SettingsR SET started = :data")
    void setSettingsStarted(boolean data);

    @Query("UPDATE SettingsR SET uik_question_disabled = :data")
    void setUikQuestionDisabled(boolean data);

    @Query("UPDATE SettingsR SET auto_zoom = :data")
    void setSettingsAutoZoom(boolean data);

    @Query("UPDATE SettingsR SET root = :data")
    void setSettingsRoot(boolean data);

    @Query("UPDATE SettingsR SET table_speed = :data")
    void setSettingsTableSpeed(boolean data);

    @Query("UPDATE SettingsR SET timings_debug = :data")
    void setTimingsLogMode(boolean data);

    @Query("UPDATE SettingsR SET send_logs = :data")
    void setSendLogMode(boolean data);

    @Query("UPDATE SettingsR SET reset_debug = :data")
    void setResetDebug(boolean data);

    @Query("UPDATE SettingsR SET need_update_config = :data")
    void setUpdateConfig(boolean data);

    @Query("UPDATE SettingsR SET memory_check = :data")
    void setSettingsMemoryCheck(boolean data);

    @Query("UPDATE SettingsR SET dark_mode = :data")
    void setSettingsDarkMode(boolean data);

    @Query("UPDATE SettingsR SET is_address_enabled = :data")
    void useLocalAddressSearch(boolean data);

    @Query("UPDATE SettingsR SET project_is_active = :data")
    void setProjectActive(boolean data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertInterState(InterStateR stateR);

    @Query("SELECT * FROM InterStateR WHERE user_project_id = :user_project_id LIMIT 1")
    InterStateR getInterState(int user_project_id);

    @Query("UPDATE InterStateR SET is_blocked_inter = :data WHERE user_project_id =:user_project_id")
    void setBlockedInter(Integer user_project_id, boolean data);

    @Query("UPDATE InterStateR SET date_start_inter = :start AND date_end_inter = :end WHERE user_project_id =:user_project_id")
    void setDatesInter(Integer user_project_id, Long start, Long end);

    @Query("UPDATE SettingsR SET last_quota_time = :data")
    void setLastQuotaTime(Long data);

    @Query("UPDATE SettingsR SET config_time = :data")
    void setConfigTime(Long data);

    @Query("UPDATE SettingsR SET last_login_time = :data")
    void setLastLoginTime(Long data);

    @Query("UPDATE SettingsR SET last_quiz_time = :data")
    void setLastQuizTime(Long data);

    @Query("UPDATE SettingsR SET last_sent_quiz_time = :data")
    void setLastSentQuizTime(Long data);

    @Query("UPDATE SettingsR SET user_date = :data")
    void setUserBirthDate(String data);

    @Query("UPDATE SettingsR SET user_name = :data")
    void setUserName(String data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertToken(TokensCounterR token);

    @Query("SELECT * FROM TokensCounterR WHERE user_id = :userId")
    List<TokensCounterR> getTokens(int userId);

    @Query("DELETE FROM TokensCounterR WHERE user_id = :user_id")
    void clearTokensCounterR(Integer user_id);

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

    @Query("SELECT * FROM QuotaR WHERE userProjectId =:userProjectId")
    List<QuotaR> getQuotaR(Integer userProjectId);

    @Query("DELETE FROM QuotaR WHERE userProjectId =:userProjectId")
    void clearQuotaR(Integer userProjectId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStatisticR(StatisticR statistics);

    @Query("SELECT * FROM StatisticR WHERE user_id = :userId LIMIT 1")
    StatisticR getStatistics(int userId);

    @Query("DELETE FROM StatisticR WHERE user_id = :user_id")
    void clearStatisticR(Integer user_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRegistrationR(RegistrationR registrationR);

    @Query("SELECT * FROM RegistrationR")
    List<RegistrationR> getAllRegistrationR();

    @Query("SELECT * FROM RegistrationR WHERE user_id =:userId ORDER BY id DESC LIMIT 1")
    RegistrationR getRegistrationR(Integer userId);

    @Query("DELETE FROM RegistrationR WHERE user_id =:userId")
    void clearRegistrationRByUser(Integer userId);

    @Query("DELETE FROM RegistrationR WHERE id =:id")
    void clearRegistrationRById(Integer id);

    @Query("UPDATE RegistrationR SET status = :status WHERE id =:id")
    void setRegStatus(Integer id, String status);

    @Query("UPDATE RegistrationR SET status = :status WHERE user_id =:id")
    void setRegStatusByUserId(Integer id, String status);

    @Query("UPDATE RegistrationR SET phone = :phone WHERE id =:id")
    void setRegPhone(Integer id, String phone);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEncryptionTableR(EncryptionTableR encryptionTableR);

    @Query("SELECT decrypted FROM EncryptionTableR WHERE encrypted = :encrypted LIMIT 1")
    Character getSymbolsForDecrypt(Character encrypted);

    @Query("SELECT encrypted FROM EncryptionTableR WHERE decrypted = :decrypted ORDER BY RANDOM() LIMIT 1")
    Character getSymbolsForEncrypt(Character decrypted);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPhotoAnswerR(PhotoAnswersR statistics);

    @Query("SELECT * FROM PhotoAnswersR WHERE token = :token")
    List<PhotoAnswersR> getPhotoAnswersByToken(String token);

    @Query("DELETE FROM PhotoAnswersR WHERE token = :token")
    void clearPhotoAnswersByToken(String token);

    @Query("SELECT * FROM PhotoAnswersR WHERE status = :status")
    List<PhotoAnswersR> getPhotoAnswersByStatus(String status);

    @Query("DELETE FROM PhotoAnswersR WHERE status = :status")
    void clearPhotoAnswersByStatus(String status);

    @Query("UPDATE PhotoAnswersR SET status = :status WHERE token =:token")
    void setPhotoAnswerStatus(String token, String status);

    @Query("DELETE FROM PhotoAnswersR WHERE name = :name")
    void clearPhotoAnswersByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSmsAnswer(SmsAnswersR smsAnswer);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSmsAnswersList(List<SmsAnswersR> smsAnswersList);

    @Query("SELECT * FROM SmsAnswersR")
    List<SmsAnswersR> getAllSmsAnswers();

    @Query("SELECT * FROM SmsAnswersR WHERE userId = :userId")
    List<SmsAnswersR> getSmsAnswersByUserId(Integer userId);

    @Query("SELECT * FROM SmsAnswersR WHERE userId = :userId AND smsIndex = :smsIndex LIMIT 1")
    SmsAnswersR getSmsAnswersBySmsId(Integer userId, String smsIndex);

    @Query("DELETE FROM SmsAnswersR WHERE userId = :userId")
    void clearSmsAnswersByUserId(Integer userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSmsReportR(SmsReportR smsReportR);

    @Query("SELECT * FROM SmsReportR WHERE user_id = :user_id")
    List<SmsReportR> getSmsReportRByUserId(Integer user_id);

    @Query("DELETE FROM SmsReportR WHERE user_id = :user_id")
    void clearSmsReportRByUserId(Integer user_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPolygon(List<PointR> points);

    @Query("SELECT * FROM PointR")
    List<PointR> getAllPoints();

    @Query("SELECT * FROM PointR WHERE route_id =:route_id")
    List<PointR> getPolygon(Integer route_id);

    @Query("DELETE FROM PointR WHERE route_id = :route_id")
    void clearPolygonByProjectId(Integer route_id);

    @Query("DELETE FROM PointR")
    void clearAllPoints();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRoutes(List<RouteR> routes);

    @Query("SELECT * FROM RouteR")
    List<RouteR> getAllRoutes();

    @Query("SELECT * FROM RouteR WHERE project_id =:project_id AND user_project_id =:user_project_id")
    List<RouteR> getRoutes(Integer project_id, Integer user_project_id);

    @Query("SELECT * FROM RouteR WHERE route_id =:route_id AND user_project_id =:user_project_id  LIMIT 1")
    RouteR getSelectedRoute(Integer user_project_id, Integer route_id);

    @Query("DELETE FROM RouteR WHERE project_id = :user_project_id")
    void clearRoutesByProjectId(Integer user_project_id);

    @Query("DELETE FROM RouteR")
    void clearAllRoutes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOnlineQuota(OnlineQuotaR onlineQuotaR);

    @Query("DELETE FROM OnlineQuotaR WHERE token = :token")
    void deleteOnlineQuota(String token);

    @Query("DELETE FROM OnlineQuotaR")
    void clearOnlineQuotas();

    @Query("SELECT * FROM OnlineQuotaR WHERE token = :token LIMIT 1")
    OnlineQuotaR getOnlineQuota(String token);

    @Query("UPDATE OnlineQuotaR SET quotas = :quotas WHERE token = :token")
    void updateOnlineQuotas(String token, String quotas);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSelectedRoute(SelectedRoutesR selectedRoutesR);

    @Query("DELETE FROM SelectedRoutesR")
    void clearSelectedRoutes();

    @Query("SELECT * FROM SelectedRoutesR WHERE user_project_id = :user_project_id LIMIT 1")
    SelectedRoutesR getSavedSelectedRoute(Integer user_project_id);

    @Query("UPDATE SelectedRoutesR SET route_id = :route_id WHERE user_project_id = :user_project_id")
    void updateSelectedRoute(Integer route_id, Integer user_project_id);

    //================================================================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAddress(List<AddressR> addresses);

    @Query("DELETE FROM AddressR")
    void clearAddresses();

    @Query("SELECT * FROM AddressR WHERE street LIKE :street AND house LIKE :house AND project_id = :projectId")
    List<AddressR> findAddress(String street, String house, int projectId);

    @Query("SELECT * FROM AddressR WHERE street LIKE :street AND project_id = :projectId")
    List<AddressR> findAddress1(String street, int projectId);

    @Query("SELECT * FROM AddressR WHERE street = :street AND project_id = :projectId")
    List<AddressR> findAddress2(String street, int projectId);

    @Query("SELECT * FROM AddressR WHERE project_id = :projectId")
    List<AddressR> findAddress3(int projectId);

    @Query("SELECT * FROM AddressR WHERE city LIKE :city AND house LIKE :house AND project_id = :projectId")
    List<AddressR> findAddress4(String city, String house, int projectId);

    @Query("SELECT * FROM AddressR WHERE city LIKE :city AND project_id = :projectId")
    List<AddressR> findAddress5(String city, int projectId);

}
