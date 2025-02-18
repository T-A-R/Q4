package pro.quizer.quizer3.model.quota;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.executable.QuestionnairesCountBySequenceExecutable;
import pro.quizer.quizer3.model.config.ElementModelNew;

public class QuotaModel implements Serializable {

    private static final String SPLIT_SEQUENCE_SYMBOL = ",";

    @SerializedName("quota_id")
    private Integer quota_id;
    @SerializedName("sequence")
    private String sequence;

    @SerializedName("limit")
    private int limit;

    @SerializedName("done")
    private int done;

    private Integer mLocalCount = null;
    private Integer mLocalUserCount = null;
    private List<QuotaTimeLineModel> mStringList = null;
    private Set<Integer> mSet = null;
    private Integer[] mArray = null;
    private int userId;
    private int userProjectId;

    public QuotaModel(String sequence, int limit, int done, int userId, int userProjectId, Integer quota_id) {
        this.sequence = sequence;
        this.limit = limit;
        this.done = done;
        this.userId = userId;
        this.userProjectId = userProjectId;
        this.quota_id = quota_id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserProjectId(int userProjectId) {
        this.userProjectId = userProjectId;
    }

    public String getSequence() {
        return sequence;
    }

    public boolean contains(final int pId) {
        return getSet().contains(pId);
    }

    public int getLimit() {
        return limit;
    }

    public int getDone(MainActivity activity) {
        return done + getLocalCount(activity);
    }

    public int getSent() {
        return done;
    }

    private int getLocalCount(MainActivity activity) {
        if (mLocalCount == null) {
            mLocalCount = new QuestionnairesCountBySequenceExecutable(activity, userId, userProjectId, getSet(), false).execute();
        }

        return mLocalCount;
    }

    public int getLocalUserDoneCount(MainActivity activity) {
        if (mLocalUserCount == null) {
            mLocalUserCount = new QuestionnairesCountBySequenceExecutable(activity, userId, userProjectId, getSet(), true).execute();
        }

        return mLocalUserCount;
    }

    public boolean containsString(final MainActivity pMainActivity, final HashMap<Integer, ElementModelNew> pMap, final String pString) {
        final List<QuotaTimeLineModel> stringSet = getStringSet(pMainActivity, pMap);

        for (final QuotaTimeLineModel quotaTimeLineModel : stringSet) {
            if (quotaTimeLineModel != null && quotaTimeLineModel.contains(pString)) {
                return true;
            }
        }

        return false;
    }

//    public List<QuotaTimeLineModel> getStringSet(final MainActivity mMainActivity, final HashMap<Integer, ElementModelNew> mMap) {
//        if (mStringList == null) {
//            final Set<Integer> pSet = getSet();
//            final List<QuotaTimeLineModel> quotaTimeLineModels = new ArrayList<>();
//            for (final int relativeId : pSet) {
//                final ElementModelNew element = mMap.get(relativeId);
//                try {
//                    quotaTimeLineModels.add(new QuotaTimeLineModel(element.getOptions().getTitle()));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            mStringList = quotaTimeLineModels;
//        }
//
//        return mStringList;
//    }

    public List<QuotaTimeLineModel> getStringSet(final MainActivity mMainActivity, final HashMap<Integer, ElementModelNew> mMap) {
        if (mStringList == null) {
            final Integer[] pSet = getArray();
            final List<QuotaTimeLineModel> quotaTimeLineModels = new ArrayList<>();
            for (final int relativeId : pSet) {
                final ElementModelNew element = mMap.get(relativeId);
                try {
                    quotaTimeLineModels.add(new QuotaTimeLineModel(element.getOptions().getTitle()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                try {
                    integers[i] = Integer.parseInt(strings[i]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            mArray = integers;
        }

        return mArray;
    }

    public boolean isCompleted(MainActivity activity) {
        return getDone(activity) >= getLimit();
    }

    public boolean isCanDisplayed() {
//        return getLimit() != 0;
        return true;
    }

    public Integer getQuota_id() {
        return quota_id;
    }

    public void setQuota_id(Integer quota_id) {
        this.quota_id = quota_id;
    }
}