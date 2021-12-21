package pro.quizer.quizer3.executable.files;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.ICallback;

import static pro.quizer.quizer3.utils.OkHttpUtils.IMAGE_JPEG;

public class PhotosSendingByUserModelExecutable extends AbstractFilesSendingByUserModelExecutable {

    public PhotosSendingByUserModelExecutable(final MainActivity pContext, final UserModelR pUserModel, final ICallback pCallback) {
        super(pContext, pUserModel, pCallback);
    }

    @Override
    public List<File> getFiles() {
        final Context context = getContext();

        if (context instanceof MainActivity) {
            return ((MainActivity) context).getPhotosByUserId(getUserId());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public String getMediaType() {
        return IMAGE_JPEG;
    }

    @Override
    public String getNameForm() {
        return Constants.NameForm.PHOTO_FILE;
    }
}