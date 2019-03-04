package pro.quizer.quizerexit.executable.files;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.model.database.UserModel;

import static pro.quizer.quizerexit.utils.OkHttpUtils.AUDIO_AMR;

public class AudiosSendingByUserModelExecutable extends AbstractFilesSendingByUserModelExecutable {

    public AudiosSendingByUserModelExecutable(final Context pContext, final UserModel pUserModel, final ICallback pCallback) {
        super(pContext, pUserModel, pCallback);
    }

    @Override
    public List<File> getFiles() {
        final Context context = getContext();

        if (context instanceof BaseActivity) {
            return ((BaseActivity) context).getAudioByUserId(getUserId());
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
