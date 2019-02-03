package pro.quizer.quizerexit.model.view;

import java.io.Serializable;
import java.util.List;

import pro.quizer.quizerexit.model.quota.QuotaModel;

public class QuotasViewModel implements Serializable {

    private List<QuotaModel> mQuotas;

    public List<QuotaModel> getQuotas() {
        return mQuotas;
    }

    public void setQuotas(List<QuotaModel> mQuotas) {
        this.mQuotas = mQuotas;
    }
}