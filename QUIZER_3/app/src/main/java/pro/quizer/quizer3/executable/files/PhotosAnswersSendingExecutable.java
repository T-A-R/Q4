package pro.quizer.quizer3.executable.files;

import static pro.quizer.quizer3.utils.OkHttpUtils.IMAGE_JPEG;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.database.models.PhotoAnswersR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.ICallback;

public class PhotosAnswersSendingExecutable extends AbstractFilesSendingByUserModelExecutable {

    private List<PhotoAnswersR> list;

    public PhotosAnswersSendingExecutable(final MainActivity pContext, final UserModelR pUserModel, final List<PhotoAnswersR> list, final ICallback pCallback) {
        super(pContext, pUserModel, pCallback);
        this.list = list;
    }

    @Override
    public List<File> getFiles() {
        Log.d("T-A-R", "getFiles: <<<<<<<<<<<<<<<<<<<<<<<<<<<");
        final Context context = getContext();
        List<File> files = new ArrayList<>();

        if (context instanceof MainActivity) {
            List<String> tokens = new ArrayList<>();
            for (PhotoAnswersR answer : list) {
                if (!tokens.contains(answer.getToken())) tokens.add(answer.getToken());
            }

            if (!tokens.isEmpty())
                for (String token : tokens) {
                    Log.d("T-A-R", "getFiles getPhotosAnswersByToken: <<<");
                    files.addAll(((MainActivity) context).getPhotosAnswersByToken(token));
                }
        }
        Log.d("T-A-R", "getFiles getPhotosAnswersByToken: " + files.size());
        return files;
    }

    @Override
    public String getMediaType() {
        return IMAGE_JPEG;
    }

    @Override
    public String getNameForm() {
        return Constants.NameForm.PHOTO_FILE_ANSWER;
    }
}