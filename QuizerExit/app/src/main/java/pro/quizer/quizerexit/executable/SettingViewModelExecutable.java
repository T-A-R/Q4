package pro.quizer.quizerexit.executable;

import android.content.Context;

import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.config.ProjectInfoModel;
import pro.quizer.quizerexit.model.config.ReserveChannelModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.view.SettingsViewModel;

public class SettingViewModelExecutable extends BaseModelExecutable<SettingsViewModel> {

    final Context mContext;

    public SettingViewModelExecutable(final Context pContext) {
        super();

        mContext = pContext;
    }

    @Override
    public SettingsViewModel execute() {
        final SettingsViewModel settingsViewModel = new SettingsViewModel();

        if (mContext instanceof BaseActivity) {
            final BaseActivity activity = (BaseActivity) mContext;

            final UserModel currentUser = activity.getCurrentUser();
            final ConfigModel configModel = currentUser.getConfig();
            final ProjectInfoModel projectInfoModel = configModel.getProjectInfo();
            final ReserveChannelModel reserveChannelModel = projectInfoModel.getReserveChannel();

            settingsViewModel.setmConfigDate(configModel.getConfigDate());
            settingsViewModel.setmConfigId(currentUser.config_id);
            settingsViewModel.setmAnswerMargin(activity.getAnswerMargin());
            settingsViewModel.setSmsSection(reserveChannelModel != null);

            return settingsViewModel;
        } else {
            return settingsViewModel;
        }
    }
}