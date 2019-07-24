package pro.quizer.quizerexit.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import pro.quizer.quizerexit.database.model.ActivationModelR;
import pro.quizer.quizerexit.database.model.ItemR;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.model.database.Item;

@Dao
public interface QuizerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertActivationModelR(ActivationModelR activationModelR);

    @Query("SELECT * FROM ActivationModelR")
    List<ActivationModelR> getActivationModelR();

    @Query("DELETE FROM ActivationModelR")
    void clearActivationModelR();

    @Query("UPDATE UserModelR SET password = :password, login = :login, config_id = :configId, role_id = :roleId, user_project_id = :userProjectId WHERE user_id = :userId")
    void updateUserModelR(String login, String password, String configId, int roleId, int userProjectId, int userId);

    @Query("UPDATE UserModelR SET config = :config WHERE user_id = :userId AND user_project_id = :userProjectId")
    void updateConfig(String config, int userId, int userProjectId);

    @Query("SELECT * FROM UserModelR")
    List<UserModelR> getAllUsers();

    @Query("SELECT * FROM UserModelR WHERE login = :login AND password = :password")
    List<UserModelR> getLocalUserModel(String login, String password);

    @Query("SELECT * FROM UserModelR WHERE user_id = :userId")
    List<UserModelR> getUserByUserId(int userId);

    @Query("DELETE FROM UserModelR WHERE user_id = :userId")
    void deleteUserByUserId(int userId);

}
