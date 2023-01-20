package pro.quizer.quizer3.executable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.database.models.QuotaR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.model.config.ElementModel;
import pro.quizer.quizer3.model.config.ElementModelNew;
import pro.quizer.quizer3.model.quota.QuotaModel;
import pro.quizer.quizer3.model.view.QuotasViewModel;
import pro.quizer.quizer3.utils.StringUtils;

public class QuotasViewModelExecutable extends BaseModelExecutable<QuotasViewModel> {

    private final String mQuery;
    private final MainActivity mMainActivity;
    private final boolean mIsNotCompletedOnly;
    private final HashMap<Integer, ElementModelNew> mMap;

    public QuotasViewModelExecutable(final HashMap<Integer, ElementModelNew> pMap,
                                     final MainActivity pContext,
                                     final String pQuery,
                                     final boolean pIsNotCompletedOnly) {
        super();

        mMainActivity = pContext;
        mQuery = pQuery.toLowerCase();
        mMap = pMap;
        mIsNotCompletedOnly = pIsNotCompletedOnly;
    }

    @Override
    public QuotasViewModel execute() {
        final QuotasViewModel quotasViewModel = new QuotasViewModel();
        quotasViewModel.setQuery(mQuery);

        final UserModelR currentUser = mMainActivity.getCurrentUserForce();
        int user_id = mMainActivity.getCurrentUserId();

        Integer user_project_id = currentUser.getConfigR().getUserProjectId();
        if (user_project_id == null)
            user_project_id = currentUser.getUser_project_id();

        List<QuotaModel> quotas = new ArrayList<>();

        final List<QuotaR> quotasR = mMainActivity.getMainDao().getQuotaR(user_project_id);
        for (QuotaR quotaR : quotasR) {
            quotas.add(new QuotaModel(quotaR.getSequence(), quotaR.getLimit(), quotaR.getDone(), user_id, user_project_id));
        }

        if (quotas.isEmpty()) {
            return quotasViewModel;
        }

        quotas = getFilteredQuotasByDone(quotas, mIsNotCompletedOnly);

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

    private List<QuotaModel> getFilteredQuotasByDone(final List<QuotaModel> pQuotas, final boolean pIsNotCompletedOnly) {
        if (pIsNotCompletedOnly) {
            final List<QuotaModel> quotas = new ArrayList<>();

            for (final QuotaModel quotaModel : pQuotas) {
                if (!quotaModel.isCompleted(mMainActivity)) {
                    quotas.add(quotaModel);
                }
            }

            return quotas;
        } else {
            return pQuotas;
        }
    }

    private QuotasViewModel getFilteredQuotas(final HashMap<Integer, ElementModelNew> mMap, final QuotasViewModel quotasViewModel, final String pQuery) {
        final List<QuotaModel> quotas = quotasViewModel.getQuotas();
        final List<QuotaModel> result = new ArrayList<>();

        for (final QuotaModel quota : quotas) {
            if (quota.containsString(mMainActivity, mMap, pQuery)) {
                result.add(quota);
            }
        }

        quotasViewModel.setQuotas(result);

        return quotasViewModel;
    }
}