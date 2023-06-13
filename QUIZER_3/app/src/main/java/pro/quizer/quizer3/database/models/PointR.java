package pro.quizer.quizer3.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("project_id")})
public class PointR {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "x")
    public Double x;

    @ColumnInfo(name = "y")
    public Double y;

    @ColumnInfo(name = "project_id")
    public Integer project_id;

    public PointR() {
    }

    public PointR(Double x, Double y, Integer project_id) {
        this.x = x;
        this.y = y;
        this.project_id = project_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Integer getProject_id() {
        return project_id;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }
}
