package pro.quizer.quizer3.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.util.Log;

import java.util.List;

import pro.quizer.quizer3.database.ElementContentsRConverter;
import pro.quizer.quizer3.database.ElementOptionsRConverter;
import pro.quizer.quizer3.utils.DateUtils;

import static pro.quizer.quizer3.MainActivity.TAG;
import static pro.quizer.quizer3.view.fragment.SmartFragment.getDao;

//@Entity
@Entity(indices = {@Index("relative_id")})
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

    @ColumnInfo(name = "was_shown")
    private boolean was_shown;

    @ColumnInfo(name = "shown_at_id")
    private Integer shown_at_id;

    @ColumnInfo(name = "checked")
    private boolean checked;

    @ColumnInfo(name = "enabled")
    private boolean enabled;

    @ColumnInfo(name = "done")
    private Integer done;

    @ColumnInfo(name = "limit")
    private Integer limit;

    @ColumnInfo(name = "elementOptionsR")
    @TypeConverters({ElementOptionsRConverter.class})
    private ElementOptionsR elementOptionsR;

    @ColumnInfo(name = "elementContentsR")
    @TypeConverters({ElementContentsRConverter.class})
    private List<ElementContentsR> elementContentsR;

    public ElementItemR() {
        this.was_shown = false;
        this.enabled = true;
        this.done = 0;
        this.limit = 999999;
        this.shown_at_id = -102;
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
        this.was_shown = false;
        this.checked = false;
        this.enabled = true;
        this.shown_at_id = -102;
    }

    public static ElementItemR clone(ElementItemR item) {
        if (item == null) {
            return null;
        }
        ElementItemR newItem = new ElementItemR();
        newItem.configId = item.getConfigId();
        newItem.userId = item.getUserId();
        newItem.projectId = item.getProjectId();
        newItem.questionnaireId = item.getQuestionnaireId();
        newItem.type = item.getType();
        newItem.subtype = item.getSubtype();
        newItem.relative_id = item.getRelative_id();
        newItem.relative_parent_id = item.getRelative_parent_id();
        newItem.elementOptionsR = item.getElementOptionsR();
        newItem.elementContentsR = item.getElementContentsR();
        newItem.was_shown = false;
        newItem.checked = false;
        newItem.enabled = true;

        return newItem;
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

    public boolean isWas_shown() {
        return was_shown;
    }

    public void setWas_shown(boolean was_shown) {
        this.was_shown = was_shown;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public List<ElementItemR> getElements() {
        List<ElementItemR> elements = null;
        try {
//            elements = getDao().getChildElements(relative_id, userId, projectId);
            elements = getDao().getChildElements(relative_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (elements != null)
//            Log.d(TAG, "getElements in model: answers size= " + elements.size());
//        for(ElementItemR elementItemR : elements) {
//            Log.d(TAG, "Element: " + elementItemR.getId() + "/" + elementItemR.getRelative_id());
//        }
        return elements;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getDone() {
        return done;
    }

    public void setDone(Integer done) {
        this.done = done;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getShown_at_id() {
        return shown_at_id;
    }

    public void setShown_at_id(Integer shown_at_id) {
        this.shown_at_id = shown_at_id;
    }
}
