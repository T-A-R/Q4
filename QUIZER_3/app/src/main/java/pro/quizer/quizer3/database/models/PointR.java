package pro.quizer.quizer3.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("route_id")})
public class PointR {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "x")
    public Double x;

    @ColumnInfo(name = "y")
    public Double y;

    @ColumnInfo(name = "route_id")
    public Integer route_id;

    @ColumnInfo(name = "polygon_number")
    public Integer polygon_number;

    public PointR() {
    }

    public PointR(Double x, Double y, Integer route_id, Integer polygon_number) {
        this.x = x;
        this.y = y;
        this.route_id = route_id;
        this.polygon_number = polygon_number;
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

    public Integer getRoute_id() {
        return route_id;
    }

    public void setRoute_id(Integer project_id) {
        this.route_id = route_id;
    }

    public Integer getPolygon_number() {
        return polygon_number;
    }

    public void setPolygon_number(Integer polygon_number) {
        this.polygon_number = polygon_number;
    }
}
