package pro.quizer.quizer3.executable.files;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.ICallback;

public class AllAudiosSendingExecutable extends AbstractAllFilesSendingExecutable {

    public AllAudiosSendingExecutable(final MainActivity pContext, final ICallback pCallback) {
        super(pContext, pCallback);
    }

    @Override
    public AbstractFilesSendingByUserModelExecutable getFilesExecutable(final UserModelR pUserModel, final ICallback pCallback) {
        return new AudiosSendingByUserModelExecutable(getContext(), pUserModel, pCallback);
    }
}
