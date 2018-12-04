package pro.quizer.quizerexit.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import pro.quizer.quizerexit.model.ElementType;

public class ElementModel implements Serializable {

    @SerializedName("relativeID")
    private int relativeID;

    @SerializedName("relativeParentID")
    private int relativeParentID;

    @ElementType
    @SerializedName("type")
    private String type;

    @SerializedName("attributes")
    private AttributesModel attributes;

    @SerializedName("elements")
    private List<ElementModel> elements;

    public int getRelativeID() {
        return relativeID;
    }

    public int getRelativeParentID() {
        return relativeParentID;
    }

    public String getType() {
        return type;
    }

    public List<ElementModel> getElements() {
        return elements;
    }
}