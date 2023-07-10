package pro.quizer.quizer3.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(indices = {@Index("project_id")})
public class RouteR {

    @ColumnInfo(name = "project_id")
    public Integer project_id;

    @PrimaryKey
    @ColumnInfo(name = "route_id")
    public Integer route_id;

    @ColumnInfo(name = "route_name")
    public String route_name;

    @ColumnInfo(name = "route_limit")
    public Integer route_limit;

    @ColumnInfo(name = "route_rqs_count_all")
    public Integer route_rqs_count_all;

    @ColumnInfo(name = "route_rqs_count_correct_inter")
    public Integer route_rqs_count_correct_inter;

    @ColumnInfo(name = "route_rqs_count_correct_login")
    public Integer route_rqs_count_correct_login;

    @ColumnInfo(name = "user_project_id")
    public Integer user_project_id;

    public RouteR() {
    }

    public Integer getProject_id() {
        return project_id;
    }

    public Integer getRoute_id() {
        return route_id;
    }

    public String getRoute_name() {
        return route_name;
    }

    public Integer getRoute_limit() {
        return route_limit;
    }

    public Integer getRoute_rqs_count_all() {
        return route_rqs_count_all;
    }

    public Integer getRoute_rqs_count_correct_inter() {
        return route_rqs_count_correct_inter;
    }

    public Integer getRoute_rqs_count_correct_login() {
        return route_rqs_count_correct_login;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public void setRoute_id(Integer route_id) {
        this.route_id = route_id;
    }

    public void setRoute_name(String route_name) {
        this.route_name = route_name;
    }

    public void setRoute_limit(Integer route_limit) {
        this.route_limit = route_limit;
    }

    public void setRoute_rqs_count_all(Integer route_rqs_count_all) {
        this.route_rqs_count_all = route_rqs_count_all;
    }

    public void setRoute_rqs_count_correct_inter(Integer route_rqs_count_correct_inter) {
        this.route_rqs_count_correct_inter = route_rqs_count_correct_inter;
    }

    public void setRoute_rqs_count_correct_login(Integer route_rqs_count_correct_login) {
        this.route_rqs_count_correct_login = route_rqs_count_correct_login;
    }

    public Integer getUser_project_id() {
        return user_project_id;
    }

    public void setUser_project_id(Integer user_project_id) {
        this.user_project_id = user_project_id;
    }
}
