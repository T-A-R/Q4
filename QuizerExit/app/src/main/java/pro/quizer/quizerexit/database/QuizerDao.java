package pro.quizer.quizerexit.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import pro.quizer.quizerexit.database.model.ActivationModelR;
import pro.quizer.quizerexit.database.model.AppLogsR;
import pro.quizer.quizerexit.database.model.CrashLogs;
import pro.quizer.quizerexit.database.model.ElementDatabaseModelR;
import pro.quizer.quizerexit.database.model.QuestionnaireDatabaseModelR;
import pro.quizer.quizerexit.database.model.UserModelR;

@Dao
public interface QuizerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertActivationModelR(ActivationModelR activationModelR);

    @Query("SELECT * FROM ActivationModelR")
    List<ActivationModelR> getActivationModelR();

    @Query("DELETE FROM ActivationModelR")
    void clearActivationModelR();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserModelR userModelR);

    @Query("UPDATE UserModelR SET password = :password, login = :login, config_id = :configId, role_id = :roleId, user_project_id = :userProjectId WHERE user_id = :userId")
    void updateUserModelR(String login, String password, String configId, int roleId, int userProjectId, int userId);

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

    @Query("DELETE FROM UserModelR WHERE user_id = :userId")
    void deleteUserByUserId(int userId);

    @Query("DELETE FROM UserModelR")
    void clearUserModelR();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertElement(ElementDatabaseModelR elementDatabaseModelR);

    @Query("SELECT * FROM ElementDatabaseModelR WHERE token = :token")
    List<ElementDatabaseModelR> getElementByToken(String token);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE user_id = :userId AND status = :status")
    List<QuestionnaireDatabaseModelR> getQuestionnaireByUserIdWithStatus(int userId, String status);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE status = :status AND user_id = :userId AND user_project_id = :projectId AND survey_status = :surveyStatus")
    List<QuestionnaireDatabaseModelR> getQuestionnaireForQuotas(int userId, int projectId, String status, String surveyStatus);

    //TODO RENAME TO setQuestionnaireStatusByToken
    @Query("UPDATE QuestionnaireDatabaseModelR SET status = :status WHERE token = :token")
    void setQuestionnaireStatus(String status, String token);

    @Query("SELECT * FROM QuestionnaireDatabaseModelR WHERE status = :status")
    List<QuestionnaireDatabaseModelR> getQuestionnaireByStatus(String status);

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAppLogsR(AppLogsR appLogsR);

    @Query("SELECT * FROM AppLogsR")
    List<AppLogsR> getAppLogsR();

    @Query("DELETE FROM AppLogsR")
    void clearAppLogsR();

    @Query("DELETE FROM CrashLogs")
    void clearCrashLogs();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCrashLog(CrashLogs crashLogs);

    @Query("SELECT * FROM CrashLogs")
    List<CrashLogs> getCrashLogs();

}
