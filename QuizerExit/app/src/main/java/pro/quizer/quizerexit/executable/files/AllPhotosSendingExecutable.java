package pro.quizer.quizerexit.executable.files;

import android.content.Context;

import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.executable.files.AbstractAllFilesSendingExecutable;
import pro.quizer.quizerexit.executable.files.AbstractFilesSendingByUserModelExecutable;
import pro.quizer.quizerexit.executable.files.PhotosSendingByUserModelExecutable;
import pro.quizer.quizerexit.model.database.UserModel;

public class AllPhotosSendingExecutable extends AbstractAllFilesSendingExecutable {

    public AllPhotosSendingExecutable(final BaseActivity pContext, final ICallback pCallback) {
        super(pContext, pCallback);
    }

//    @Override
//    public AbstractFilesSendingByUserModelExecutable getFilesExecutable(final UserModel pUserModel, final ICallback pCallback) {
//        return new PhotosSendingByUserModelExecutable(getContext(), pUserModel, pCallback);
//    }
@Override
public AbstractFilesSendingByUserModelExecutable getFilesExecutable(final UserModelR pUserModel, final ICallback pCallback) {
    return new PhotosSendingByUserModelExecutable(getContext(), pUserModel, pCallback);
}
}
