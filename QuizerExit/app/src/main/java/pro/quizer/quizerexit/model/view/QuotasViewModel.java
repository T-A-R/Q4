package pro.quizer.quizerexit.model.view;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

import pro.quizer.quizerexit.model.quota.QuotaModel;

public class QuotasViewModel implements Serializable, Parcelable {

    private List<QuotaModel> mQuotas;
    private String mQuery;

    public List<QuotaModel> getQuotas() {
        return mQuotas;
    }

    public void setQuery(final String pQuery) {
        mQuery = pQuery;
    }

    public String getQuery() {
        return mQuery;
    }

    public void setQuotas(List<QuotaModel> mQuotas) {
        this.mQuotas = mQuotas;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}