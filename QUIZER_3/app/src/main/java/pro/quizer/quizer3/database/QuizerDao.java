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
import pro.quizer.quizer3.database.models.ElementDatabaseModelR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.ElementPassedR;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
import pro.quizer.quizer3.database.models.SmsItemR;
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

    @Query("SELECT * FROM ElementItemR")
    List<ElementItemR> getAllElementItemR();

    @Query("SELECT * FROM ElementItemR WHERE userId =:user_id AND projectId = :project_id")
    List<ElementItemR> getCurrentElements(Integer user_id, Integer project_id);

    @Query("SELECT * FROM ElementItemR WHERE relative_id =:id AND userId =:user_id AND projectId = :project_id LIMIT 1")
    ElementItemR getElementById(Integer id, Integer user_id, Integer project_id);

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

    @Query("SELECT * FROM UserModelR WHERE user_id = :userId")
    List<UserModelR> getUserByUserId(int userId);

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertElementPassedR(ElementPassedR elementPassedR);

    @Query("SELECT * FROM ElementPassedR WHERE token =:token")
    List<ElementPassedR> getAllElementPassedR(String token);

    @Query("SELECT * FROM ElementPassedR WHERE token =:token ORDER BY id DESC LIMIT 1")
    ElementPassedR getLastElementPassedR(String token);

    @Query("DELETE FROM ElementPassedR")
    void clearElementPassedR();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCurrentQuestionnaireR(CurrentQuestionnaireR currentQuestionnaireR);

    @Query("SELECT * FROM CurrentQuestionnaireR ORDER BY id DESC LIMIT 1")
    CurrentQuestionnaireR getCurrentQuestionnaireR();

    @Query("UPDATE CurrentQuestionnaireR SET question_start_time = :time")
    void setQuestionTime(Long time);

    @Query("UPDATE CurrentQuestionnaireR SET prev_element_id = :id ")
    void setPrevElement(List<Integer> id);

    @Query("UPDATE CurrentQuestionnaireR SET current_element_id = :id ")
    void setCurrentElement(Integer id);

    @Query("DELETE FROM CurrentQuestionnaireR")
    void clearCurrentQuestionnaireR();
}
