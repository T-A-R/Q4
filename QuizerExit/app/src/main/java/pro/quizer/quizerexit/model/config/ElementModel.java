package pro.quizer.quizerexit.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizerexit.model.ElementType;
import pro.quizer.quizerexit.utils.StringUtils;

import static pro.quizer.quizerexit.model.AttributeOpenType.CHECKBOX;

public class ElementModel implements Serializable {

    @SerializedName("relativeID")
    private int relativeID;

    @SerializedName("relativeParentID")
    private int relativeParentID;

    @SerializedName("type")
    private String type;

    @SerializedName("options")
    private AttributesModel options;

    @SerializedName("elements")
    private List<ElementModel> elements;

    // @Ignore start
    private boolean isShowing;
    private boolean isChecked;
    private boolean isEnabled = true;
    private long startTime = 0;
    private long endTime = 0;
    private String textAnswer;
    // @Ignore end

    public List<ElementModel> getSubElementsByType(@ElementType final String pType) {
        final List<ElementModel> list = new ArrayList<>();

        for (final ElementModel subElement : getElements()) {
            if (subElement != null && subElement.getType().equals(pType)) {
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

    public int getCountOfSelectedSubElements() {
        int count = 0;

        if (elements != null) {
            for (final ElementModel element : elements) {
                if (element != null && element.isFullySelected()) {
                    count++;
                }
            }
        }

        return count;
    }

    public List<ElementModel> getElements() {
        return elements;
    }

    public AttributesModel getOptions() {
        return options;
    }


    // @Ignore start
    public boolean isChecked() {
        return isChecked;
    }

    public boolean isFullySelected() {
        if (CHECKBOX.equals(options.getOpenType())) {
            return isChecked;
        } else {
            return isChecked && StringUtils.isNotEmpty(textAnswer);
        }
    }

    public boolean isCheckedAndTextIsEmptyForSpecialOpenTypes() {
        if (CHECKBOX.equals(options.getOpenType())) {
            return false;
        } else {
            return isChecked && StringUtils.isEmpty(textAnswer);
        }
    }

    public String getTextAnswer() {
        return textAnswer;
    }

    public void setTextAnswer(final String textAnswer) {
        this.textAnswer = textAnswer;
    }

    public void setChecked(final boolean pIsChecked) {
        isChecked = pIsChecked;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setShowing(boolean showing) {
        isShowing = showing;
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void setStartTime(long start) {
        this.startTime = start;
    }

    public void setEndTime(long end) {
        this.endTime = end;
    }

    public long getDuration() {
        return endTime - startTime;
    }

    public void setEnabled(final boolean pIsEnabled) {
        isEnabled = pIsEnabled;
    }
    // @Ignore end
}