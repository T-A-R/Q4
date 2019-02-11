package pro.quizer.quizerexit.executable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.quota.QuotaModel;
import pro.quizer.quizerexit.model.view.QuotasViewModel;
import pro.quizer.quizerexit.utils.StringUtils;

public class QuotasViewModelExecutable extends BaseModelExecutable<QuotasViewModel> {

    final BaseActivity mBaseActivity;
    final String mQuery;
    final Map<Integer, ElementModel> mMap;
    final boolean mIsNotCompletedOnly;

    public QuotasViewModelExecutable(final Map<Integer, ElementModel> pMap,
                                     final BaseActivity pContext,
                                     final String pQuery,
                                     final boolean pIsNotCompletedOnly) {
        super();

        mBaseActivity = pContext;
        mQuery = pQuery.toLowerCase();
        mMap = pMap;
        mIsNotCompletedOnly = pIsNotCompletedOnly;
    }

    @Override
    public QuotasViewModel execute() {
        final QuotasViewModel quotasViewModel = new QuotasViewModel();
        quotasViewModel.setQuery(mQuery);

        final UserModel currentUser = mBaseActivity.getCurrentUser();
        List<QuotaModel> quotas = currentUser.getQuotas();

        if (quotas == null || quotas.isEmpty()) {
            return quotasViewModel;
        }

        quotas = getFilteredQuotasByDone(mBaseActivity, quotas, mIsNotCompletedOnly);

        if (quotas == null || quotas.isEmpty()) {
            return quotasViewModel;
        }

        quotasViewModel.setQuotas(quotas);

        if (StringUtils.isNotEmpty(mQuery)) {
            return getFilteredQuotas(mMap, quotasViewModel, mQuery);
        } else {
            return quotasViewModel;
        }
    }

    private List<QuotaModel> getFilteredQuotasByDone(final BaseActivity pBaseActivity, final List<QuotaModel> pQuotas, final boolean pIsNotCompletedOnly) {
        if (pIsNotCompletedOnly) {
            final List<QuotaModel> quotas = new ArrayList<>();

            for (final QuotaModel quotaModel : pQuotas) {
                if (!quotaModel.isCompleted(pBaseActivity)) {
                    quotas.add(quotaModel);
                }
            }

            return quotas;
        } else {
            return pQuotas;
        }
    }

    private QuotasViewModel getFilteredQuotas(final Map<Integer, ElementModel> mMap, final QuotasViewModel quotasViewModel, final String pQuery) {
        final List<QuotaModel> quotas = quotasViewModel.getQuotas();
        final List<QuotaModel> result = new ArrayList<>();

        for (final QuotaModel quota : quotas) {
            if (quota.containsString(mBaseActivity, mMap, pQuery)) {
                result.add(quota);
            }
        }

        quotasViewModel.setQuotas(result);

        return quotasViewModel;
    }
}