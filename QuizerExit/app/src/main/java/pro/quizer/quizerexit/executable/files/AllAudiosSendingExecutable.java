package pro.quizer.quizerexit.executable.files;

import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.executable.ICallback;

public class AllAudiosSendingExecutable extends AbstractAllFilesSendingExecutable {

    public AllAudiosSendingExecutable(final BaseActivity pContext, final ICallback pCallback) {
        super(pContext, pCallback);
    }

    @Override
    public AbstractFilesSendingByUserModelExecutable getFilesExecutable(final UserModelR pUserModel, final ICallback pCallback) {
        return new AudiosSendingByUserModelExecutable(getContext(), pUserModel, pCallback);
    }
}
