package pro.quizer.quizerexit.model.quota;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.executable.QuestionnairesCountBySequenceExecutable;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.utils.LogUtils;

public class QuotaModel implements Serializable, Parcelable {

    private static final String SPLIT_SEQUENCE_SYMBOL = ",";

    @SerializedName("sequence")
    private String sequence;

    @SerializedName("limit")
    private int limit;

    @SerializedName("done")
    private int done;

    private Integer mLocalCount = null;
    private List<QuotaTimeLineModel> mStringList = null;
    private Set<Integer> mSet = null;
    private Integer[] mArray = null;

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

    public boolean containsString(final BaseActivity pBaseActivity, final Map<Integer, ElementModel> pMap, final String pString) {
        final List<QuotaTimeLineModel> stringSet = getStringSet(pBaseActivity, pMap);

        for (final QuotaTimeLineModel quotaTimeLineModel : stringSet) {
            if (quotaTimeLineModel != null && quotaTimeLineModel.contains(pString)) {
                return true;
            }
        }

        return false;
    }

    public List<QuotaTimeLineModel> getStringSet(final BaseActivity mBaseActivity, final Map<Integer, ElementModel> mMap) {
        if (mStringList == null) {
            final Set<Integer> pSet = getSet();
            final List<QuotaTimeLineModel> quotaTimeLineModels = new ArrayList<>();

            for (final int relativeId : pSet) {
                final ElementModel element = mMap.get(relativeId);

                quotaTimeLineModels.add(new QuotaTimeLineModel(element.getOptions().getTitle(mBaseActivity)));
            }

            mStringList = quotaTimeLineModels;
        }

        return mStringList;
    }

    public Set<Integer> getSet() {
        if (mSet == null) {
            final String[] strings = sequence.split(SPLIT_SEQUENCE_SYMBOL);
            final Integer[] integers = new Integer[strings.length];

            for (int i = 0; i < strings.length; i++) {
                integers[i] = Integer.parseInt(strings[i]);
            }

            mSet = new HashSet<>(Arrays.asList(integers));
        }

        return mSet;
    }

    public Integer[] getArray() {
        if (mArray == null) {
            final String[] strings = sequence.split(SPLIT_SEQUENCE_SYMBOL);
            final Integer[] integers = new Integer[strings.length];

            for (int i = 0; i < strings.length; i++) {
                integers[i] = Integer.parseInt(strings[i]);
            }

            mArray = integers;
        }

        return mArray;
    }

    public boolean isCompleted(final BaseActivity pBaseActivity) {
        return getDone(pBaseActivity) >= getLimit();
    }

    public boolean isCanDisplayed() {
        return getLimit() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}