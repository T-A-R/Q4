package pro.quizer.quizer3.executable.files;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.ICallback;

import static pro.quizer.quizer3.utils.OkHttpUtils.AUDIO_AMR;

public class AudiosSendingByUserModelExecutable extends AbstractFilesSendingByUserModelExecutable {

    public AudiosSendingByUserModelExecutable(final MainActivity pContext, final UserModelR pUserModel, final ICallback pCallback) {
        super(pContext, pUserModel, pCallback);
    }

    @Override
    public List<File> getFiles() {
        final Context context = getContext();

        if (context instanceof MainActivity) {
            return ((MainActivity) context).getAudioByUserId(getUserId());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public String getMediaType() {
        return AUDIO_AMR;
    }

    @Override
    public String getNameForm() {
        return Constants.NameForm.AUDIO_FILE;
    }
}
