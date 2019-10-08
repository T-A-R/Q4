package pro.quizer.quizer3.model.config;

import android.view.View;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import pro.quizer.quizer3.model.ElementSubtype;
import pro.quizer.quizer3.model.ElementType;
import pro.quizer.quizer3.utils.StringUtils;

import static pro.quizer.quizer3.model.OptionsOpenType.CHECKBOX;

public class ElementModelNew implements Serializable {

    @SerializedName("relative_id")
    private int relative_id;

    @SerializedName("relative_parent_id")
    private int relative_parent_id;

    @ElementType
    @SerializedName("type")
    private String type;

    @ElementSubtype
    @SerializedName("subtype")
    private String subtype;

    @SerializedName("data")
    private String data;

    @SerializedName("data_on")
    private String data_on;

    @SerializedName("data_off")
    private String data_off;

    @SerializedName("options")
    private OptionsModelNew options;

    @SerializedName("elements")
    private List<ElementModelNew> elements;

    @SerializedName("contents")
    private List<Contents> contents;

    // @Ignore start
    private boolean isScreenShowing;
    private boolean isQuestionShowing;
    private boolean isChecked;
    private int queryVisibility = View.VISIBLE;
    private boolean isShuffeled;
    private boolean isShuffeledIntoBox;
    private boolean isEnabled = true;
    private long startTime = 0;
    private long endTime = 0;
    private String textAnswer;
    // @Ignore end

    public static Comparator<ElementModelNew> COMPARATOR = new Comparator<ElementModelNew>() {

        public int compare(ElementModelNew s1, ElementModelNew s2) {
            int StudentName1 = s1.getOptions().getOrder();
            int StudentName2 = s2.getOptions().getOrder();

            return StudentName1 - StudentName2;
        }
    };

    public List<ElementModelNew> getSubElementsByType(@ElementType final String pType) {
        final List<ElementModelNew> list = new ArrayList<>();

        for (final ElementModelNew subElement : getElements()) {
            if (subElement != null && subElement.getType().equals(pType)) {
                list.add(subElement);
            }
        }

        return list;
    }

    public int getRelativeID() {
        return relative_id;
    }

    public int getRelativeParentID() {
        return relative_parent_id;
    }

    @ElementType
    public String getType() {
        return type;
    }

    @ElementSubtype
    public String getSubtype() {
        return subtype;
    }

    public int getCountOfSelectedSubElements() {
        int count = 0;

        if (elements != null) {
            for (final ElementModelNew element : elements) {
                if (element != null && element.isFullySelected()) {
                    count++;
                }
            }
        }

        return count;
    }

    public String getData() {
        return data;
    }

    public String getDataOn() {
        return data_on;
    }

    public String getDataOff() {
        return data_off;
    }

    public List<Contents> getContents() {
        return contents;
    }

    public List<ElementModelNew> getElements() {
        return elements;
    }

    public int getNotNullableSubElementsCount() {
        int count = 0;

        if (elements != null) {
            for (final ElementModelNew element : elements) {
                if (element != null) {
                    count++;
                }
            }
        }

        return count;
    }

    public OptionsModelNew getOptions() {
        return options;
    }


    // @Ignore start
    public boolean isChecked() {
        return isChecked;
    }

    public boolean isShuffeled() {
        return isShuffeled;
    }

    public boolean isShuffeledIntoBox() {
        return isShuffeledIntoBox;
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

    public boolean isAnyChecked() {
        for (final ElementModelNew elementModel : getElements()) {
            if (elementModel.isFullySelected()) {
                return true;
            }
        }

        return false;
    }

    public String getTextAnswer() {
        return textAnswer;
    }

    public void setTextAnswer(final String textAnswer) {
        this.textAnswer = textAnswer;
    }

    public void setQueryVisibility(final int queryVisibility) {
        this.queryVisibility = queryVisibility;
    }

    public int getQueryVisibility() {
        return queryVisibility;
    }

    public void setChecked(final boolean pIsChecked) {
        isChecked = pIsChecked;
    }

    public void setShuffeled(final boolean pIsShuffeled) {
        isShuffeled = pIsShuffeled;
    }

    public void setShuffeledIntoBox(final boolean pIsShuffeledIntoBox) {
        isShuffeledIntoBox = pIsShuffeledIntoBox;
    }

//    public boolean isEnabled(final List<QuotaModel> quotas, final BaseActivity pBaseActivity, final HashMap<Integer, ElementModel> mMap, final ElementModel pElementModel) {
//        return isEnabled && getOptions().isEnabled(quotas, pBaseActivity, mMap, pElementModel);
//    }

    public void setScreenShowing(boolean screenShowing) {
        isScreenShowing = screenShowing;
    }

    public void setQuestionShowing(boolean questionShowing) {
        isQuestionShowing = questionShowing;
    }

    public boolean isScreenShowing() {
        return isScreenShowing;
    }

    public boolean isQuestionShowing() {
        return isQuestionShowing;
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