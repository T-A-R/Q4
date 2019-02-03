package pro.quizer.quizerexit.model.quota;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.executable.QuestionnairesCountBySequenceExecutable;
import pro.quizer.quizerexit.model.config.ElementModel;

public class QuotaModel implements Serializable {

    private static final String SPLIT_SEQUENCE_SYMBOL = ",";

    @SerializedName("sequence")
    private String sequence;

    @SerializedName("limit")
    private int limit;

    @SerializedName("done")
    private int done;

    private Integer mLocalCount = null;

    public String getSequence() {
        return sequence;
    }

    public boolean contains(final int pId) {
        return getSet().contains(pId);
    }

    public int getLimit() {
        return limit;
    }

    public int getDone(final BaseActivity pBaseActivity) {
        return done + getLocalCount(pBaseActivity);
    }

    private int getLocalCount(final BaseActivity pBaseActivity) {
        if (mLocalCount == null) {
            mLocalCount = new QuestionnairesCountBySequenceExecutable(pBaseActivity, getSet()).execute();
        }

        return mLocalCount;
    }

    public Set<Integer> getSet() {
        final String[] strings = sequence.split(SPLIT_SEQUENCE_SYMBOL);
        final Integer[] integers = new Integer[strings.length];

        for (int i = 0; i < strings.length; i++) {
            integers[i] = Integer.parseInt(strings[i]);
        }

        return new HashSet<>(Arrays.asList(integers));
    }

    public Integer[] getArray() {
        final String[] strings = sequence.split(SPLIT_SEQUENCE_SYMBOL);
        final Integer[] integers = new Integer[strings.length];

        for (int i = 0; i < strings.length; i++) {
            integers[i] = Integer.parseInt(strings[i]);
        }

        return integers;
    }

    public boolean isCompleted(final BaseActivity pBaseActivity) {
        return getDone(pBaseActivity) >= getLimit();
    }

    public boolean isStartedElement(final ElementModel pElementModel) {
        return getArray()[0] == pElementModel.getRelativeID();
    }
}