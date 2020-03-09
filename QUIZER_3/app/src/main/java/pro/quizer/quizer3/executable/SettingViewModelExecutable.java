package pro.quizer.quizer3.executable;

import android.content.Context;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.model.config.ConfigModel;
import pro.quizer.quizer3.model.view.SettingsViewModel;

public class SettingViewModelExecutable extends BaseModelExecutable<SettingsViewModel> {

    final Context mContext;

    public SettingViewModelExecutable(final Context pContext) {
        super();

        mContext = pContext;
    }

    @Override
    public SettingsViewModel execute() {
        final SettingsViewModel settingsViewModel = new SettingsViewModel();

        if (mContext instanceof MainActivity) {
            final MainActivity activity = (MainActivity) mContext;
            final UserModelR currentUser = activity.getCurrentUser();
            final ConfigModel configModel = activity.getConfig();

            settingsViewModel.setmConfigDate(configModel.getConfigDate());
            settingsViewModel.setmConfigId(currentUser.getConfig_id());
            settingsViewModel.setmAnswerMargin(activity.getAnswerMargin());
            settingsViewModel.setSmsSection(configModel.hasReserveChannels());

            return settingsViewModel;
        } else {
            return settingsViewModel;
        }
    }
}