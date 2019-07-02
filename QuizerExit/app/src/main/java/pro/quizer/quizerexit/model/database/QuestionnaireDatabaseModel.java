package pro.quizer.quizerexit.model.database;

import android.os.Parcel;
import android.os.Parcelable;

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
    public final static String USER_ID = "user_id";
    public final static String DATE_INTERVIEW = "date_interview";

    // like id
    @Column(name = TOKEN)
    public String token;

    @Column(name = LOGIN_ADMIN)
    public String login_admin;

    @Column(name = LOGIN)
    public String login;

    @Column(name = USER_ID)
    public int user_id;

    @Column(name = "passw")
    public String passw;

    @Column(name = "questionnaire_id")
    public int questionnaire_id;

    @Column(name = "project_id")
    public int project_id;

    @Column(name = "billing_questions")
    public int billing_questions;

    @Column(name = USER_PROJECT_ID)
    public int user_project_id;

    @Column(name = DATE_INTERVIEW)
    public long date_interview;

    @Column(name = "gps")
    public String gps;

    @Column(name = "gps_time")
    public long gps_time;

    @Column(name = "questions_passed")
    public int questions_passed;

    @Column(name = "screens_passed")
    public int screens_passed;

    @Column(name = "selected_questions")
    public int selected_questions;

    @Column(name = "duration_time_questionnaire")
    public int duration_time_questionnaire;

    @Column(name = "quota_time_difference")
    public Long quota_time_difference;

    @Column(name = "send_time_difference")
    public Long send_time_difference;

    @Column(name = "auth_time_difference")
    public Long auth_time_difference;

    @Column(name = STATUS)
    public String status;

}