package pro.quizer.quizer3.executable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.model.config.ConfigModel;
import pro.quizer.quizer3.model.config.ElementModelNew;
import pro.quizer.quizer3.model.config.ProjectInfoModel;
import pro.quizer.quizer3.model.config.ReserveChannelModel;
import pro.quizer.quizer3.model.config.StagesModel;
import pro.quizer.quizer3.model.sms.SmsStage;
import pro.quizer.quizer3.model.view.SmsViewModel;

public class SmsViewModelExecutable extends BaseModelExecutable<SmsViewModel> {

    private final MainActivity mBaseActivity;
    private final Map<Integer, ElementModelNew> mMap;

    public SmsViewModelExecutable(final Map<Integer, ElementModelNew> pMap,
                                  final MainActivity pContext) {
        super();

        mBaseActivity = pContext;
        mMap = pMap;
    }

    @Override
    public SmsViewModel execute() {
        final SmsViewModel smsViewModel = new SmsViewModel();
        final UserModelR currentUser = mBaseActivity.getCurrentUser();
        final ConfigModel configModel = mBaseActivity.getConfig();
        final ProjectInfoModel projectInfoModel = configModel.getProjectInfo();
        final ReserveChannelModel reserveChannelModel = projectInfoModel.getReserveChannel();

        if (reserveChannelModel != null){
            final List<StagesModel> stagesModels = reserveChannelModel.getStages();
            final List<SmsStage> smsStages = new ArrayList<>();

            for (final StagesModel stageModel : stagesModels) {
                smsStages.add(new SmsStage(mBaseActivity, stageModel, mBaseActivity));
            }

            smsViewModel.setSmsStages(smsStages);
        }

        return smsViewModel;
    }
}