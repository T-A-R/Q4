package pro.quizer.quizerexit.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import pro.quizer.quizerexit.model.ElementType;

public class ElementField implements Serializable {

    @SerializedName("relativeID")
    private int relativeID;

    @SerializedName("relativeParentID")
    private int relativeParentID;

    @ElementType
    @SerializedName("type")
    private String type;

    @SerializedName("attributes")
    private AttributesField attributes;

    @SerializedName("elements")
    private List<ElementField> elements;

    public int getRelativeID() {
        return relativeID;
    }

    public int getRelativeParentID() {
        return relativeParentID;
    }

    public String getType() {
        return type;
    }

    public List<ElementField> getElements() {
        return elements;
    }
}