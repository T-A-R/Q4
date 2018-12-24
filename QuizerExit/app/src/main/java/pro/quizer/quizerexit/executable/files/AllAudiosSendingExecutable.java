package pro.quizer.quizerexit.executable.files;

import android.content.Context;

import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.model.database.UserModel;

public class AllAudiosSendingExecutable extends AbstractAllFilesSendingExecutable {

    public AllAudiosSendingExecutable(final Context pContext, final ICallback pCallback) {
        super(pContext, pCallback);
    }

    @Override
    public AbstractFilesSendingByUserModelExecutable getFilesExecutable(final UserModel pUserModel, final ICallback pCallback) {
        return new AudiosSendingByUserModelExecutable(getContext(), pUserModel, pCallback);
    }
}
