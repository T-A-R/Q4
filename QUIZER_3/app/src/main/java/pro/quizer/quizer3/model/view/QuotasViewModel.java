package pro.quizer.quizer3.model.view;

import java.io.Serializable;
import java.util.List;

import pro.quizer.quizer3.model.quota.QuotaModel;

public class QuotasViewModel implements Serializable {

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

}