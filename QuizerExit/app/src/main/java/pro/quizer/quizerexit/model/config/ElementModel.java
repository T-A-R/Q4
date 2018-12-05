package pro.quizer.quizerexit.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizerexit.model.ElementType;

public class ElementModel implements Serializable {

    @SerializedName("relativeID")
    private int relativeID;

    @SerializedName("relativeParentID")
    private int relativeParentID;

    @SerializedName("type")
    private String type;

    @SerializedName("attributes")
    private AttributesModel attributes;

    @SerializedName("elements")
    private List<ElementModel> elements;

    // @Ignore start
    private boolean isChecked;
    private boolean isEnabled = true;
    // @Ignore end

    public List<ElementModel> getSubElementsByType(@ElementType final String pType) {
        final List<ElementModel> list = new ArrayList<>();

        for (final ElementModel subElement : getElements()) {
            if (subElement.getType().equals(pType)) {
                list.add(subElement);
            }
        }

        return list;
    }

    public int getRelativeID() {
        return relativeID;
    }

    public int getRelativeParentID() {
        return relativeParentID;
    }

    @ElementType
    public String getType() {
        return type;
    }

    public List<ElementModel> getElements() {
        return elements;
    }

    public AttributesModel getAttributes() {
        return attributes;
    }


    // @Ignore start
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(final boolean pIsChecked) {
        isChecked = pIsChecked;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(final boolean pIsEnabled) {
        isEnabled = pIsEnabled;
    }
    // @Ignore end
}