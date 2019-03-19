package pro.quizer.quizerexit.executable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.ProjectInfoModel;
import pro.quizer.quizerexit.model.config.ReserveChannelModel;
import pro.quizer.quizerexit.model.config.StagesModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.sms.SmsStage;
import pro.quizer.quizerexit.model.view.SmsViewModel;

public class SmsViewModelExecutable extends BaseModelExecutable<SmsViewModel> {

    private final BaseActivity mBaseActivity;
    private final Map<Integer, ElementModel> mMap;

    public SmsViewModelExecutable(final Map<Integer, ElementModel> pMap,
                                  final BaseActivity pContext) {
        super();

        mBaseActivity = pContext;
        mMap = pMap;
    }

    @Override
    public SmsViewModel execute() {
        final SmsViewModel smsViewModel = new SmsViewModel();

        final UserModel currentUser = mBaseActivity.getCurrentUser();
        final ConfigModel configModel = currentUser.getConfig();
        final ProjectInfoModel projectInfoModel = configModel.getProjectInfo();
        final ReserveChannelModel reserveChannelModel = projectInfoModel.getReserveChannel();
        final List<StagesModel> stagesModels = reserveChannelModel.getStages();
        final List<SmsStage> smsStages = new ArrayList<>();

        for (final StagesModel stageModel : stagesModels) {
            smsStages.add(new SmsStage(mBaseActivity, stageModel, mBaseActivity));
        }

        smsViewModel.setSmsStages(smsStages);

        return smsViewModel;
    }
}