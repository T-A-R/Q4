package pro.quizer.quizerexit.executable;

import android.content.Context;

import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.view.QuotasViewModel;

public class QuotasViewModelExecutable extends BaseModelExecutable<QuotasViewModel> {

    final Context mContext;

    public QuotasViewModelExecutable(final Context pContext) {
        super();

        mContext = pContext;
    }

    @Override
    public QuotasViewModel execute() {
        final QuotasViewModel quotasViewModel = new QuotasViewModel();

        if (mContext instanceof BaseActivity) {
            final BaseActivity activity = (BaseActivity) mContext;
            final UserModel currentUser = activity.getCurrentUser();

            quotasViewModel.setQuotas(currentUser.getQuotas());

            return quotasViewModel;
        } else {
            return quotasViewModel;
        }
    }
}