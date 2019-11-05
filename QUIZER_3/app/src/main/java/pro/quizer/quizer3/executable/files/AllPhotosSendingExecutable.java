package pro.quizer.quizer3.executable.files;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.executable.files.AbstractAllFilesSendingExecutable;
import pro.quizer.quizer3.executable.files.AbstractFilesSendingByUserModelExecutable;

public class AllPhotosSendingExecutable extends AbstractAllFilesSendingExecutable {

    public AllPhotosSendingExecutable(final MainActivity pContext, final ICallback pCallback) {
        super(pContext, pCallback);
    }

@Override
public AbstractFilesSendingByUserModelExecutable getFilesExecutable(final UserModelR pUserModel, final ICallback pCallback) {
    return new PhotosSendingByUserModelExecutable(getContext(), pUserModel, pCallback);
}
}
