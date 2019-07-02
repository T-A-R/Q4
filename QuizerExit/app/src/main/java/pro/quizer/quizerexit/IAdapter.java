package pro.quizer.quizerexit;

import android.os.Parcelable;

import java.io.Serializable;

public interface IAdapter extends Serializable {

    // return next relative_id or -1
    int processNext() throws Exception;

}
