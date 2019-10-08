package pro.quizer.quizer3.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.List;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.database.ElementContentsRConverter;
import pro.quizer.quizer3.database.ElementOptionsRConverter;

@Entity
public class ElementItemR {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "configId")
    private String configId;

    @ColumnInfo(name = "userId")
    private int userId;

    @ColumnInfo(name = "projectId")
    private int projectId;

    @ColumnInfo(name = "questionnaireId")
    private int questionnaireId;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "subtype")
    private String subtype;

    @ColumnInfo(name = "relative_id")
    private Integer relative_id;

    @ColumnInfo(name = "relative_parent_id")
    private Integer relative_parent_id;

    @ColumnInfo(name = "elementOptionsR")
    @TypeConverters({ElementOptionsRConverter.class})
    private ElementOptionsR elementOptionsR;

    @ColumnInfo(name = "elementContentsR")
    @TypeConverters({ElementContentsRConverter.class})
    private List<ElementContentsR> elementContentsR;

    public ElementItemR() {
    }

    public ElementItemR(String configId, int userId, int projectId, int questionnaireId, String type, String subtype, Integer relative_id, Integer relative_parent_id, ElementOptionsR elementOptionsR, List<ElementContentsR> elementContentsR) {
        this.configId = configId;
        this.userId = userId;
        this.projectId = projectId;
        this.questionnaireId = questionnaireId;
        this.type = type;
        this.subtype = subtype;
        this.relative_id = relative_id;
        this.relative_parent_id = relative_parent_id;
        this.elementOptionsR = elementOptionsR;
        this.elementContentsR = elementContentsR;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(int questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public Integer getRelative_id() {
        return relative_id;
    }

    public void setRelative_id(Integer relative_id) {
        this.relative_id = relative_id;
    }

    public Integer getRelative_parent_id() {
        return relative_parent_id;
    }

    public void setRelative_parent_id(Integer relative_parent_id) {
        this.relative_parent_id = relative_parent_id;
    }

    public ElementOptionsR getElementOptionsR() {
        return elementOptionsR;
    }

    public void setElementOptionsR(ElementOptionsR elementOptionsR) {
        this.elementOptionsR = elementOptionsR;
    }

    public List<ElementContentsR> getElementContentsR() {
        return elementContentsR;
    }

    public void setElementContentsR(List<ElementContentsR> elementContentsR) {
        this.elementContentsR = elementContentsR;
    }
}
