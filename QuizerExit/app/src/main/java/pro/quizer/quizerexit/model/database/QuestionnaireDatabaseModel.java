package pro.quizer.quizerexit.model.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

@Table(name = "QuestionnaireDatabaseModel")
public class QuestionnaireDatabaseModel extends Model implements Serializable {

    public final static String USER_PROJECT_ID = "user_project_id";
    public final static String STATUS = "status";
    public final static String TOKEN = "token";
    public final static String LOGIN = "login";
    public final static String LOGIN_ADMIN = "login_admin";

    // like id
    @Column(name = TOKEN)
    public String token;

    @Column(name = LOGIN_ADMIN)
    public String login_admin;

    @Column(name = LOGIN)
    public String login;

    @Column(name = "passw")
    public String passw;

    @Column(name = "questionnaire_id")
    public int questionnaire_id;

    @Column(name = "project_id")
    public int project_id;

    @Column(name = USER_PROJECT_ID)
    public int user_project_id;

    @Column(name = "date_interview")
    public long date_interview;

    @Column(name = "gps")
    public String gps;

    @Column(name = "questions_passed")
    public int questions_passed;

    @Column(name = "screens_passed")
    public int screens_passed;

    @Column(name = "duration_time_questionnaire")
    public int duration_time_questionnaire;

    @Column(name = STATUS)
    public String status;

}