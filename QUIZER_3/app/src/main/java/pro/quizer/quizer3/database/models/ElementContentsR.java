package pro.quizer.quizer3.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("relative_id")})
public class ElementContentsR {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "relative_id")
    private Integer relative_id;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "data")
    private String data;

    @ColumnInfo(name = "data_small")
    private String data_small;

    @ColumnInfo(name = "data_thumb")
    private String data_thumb;

    @ColumnInfo(name = "order")
    private Integer order;

    public ElementContentsR() {
    }

    public ElementContentsR(Integer relative_id, String type, String data, String data_small, String data_thumb, Integer order) {
        this.relative_id = relative_id;
        this.type = type;
        this.data = data;
        this.data_small = data_small;
        this.data_thumb = data_thumb;
        this.order = order;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getRelative_id() {
        return relative_id;
    }

    public void setRelative_id(Integer relative_id) {
        this.relative_id = relative_id;
    }

    public String getData_small() {
        return data_small;
    }

    public void setData_small(String data_small) {
        this.data_small = data_small;
    }

    public String getData_thumb() {
        return data_thumb;
    }

    public void setData_thumb(String data_thumb) {
        this.data_thumb = data_thumb;
    }
}
