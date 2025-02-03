package pro.quizer.quizer3.executable;

import static pro.quizer.quizer3.CoreApplication.getQuizerDatabase;
import static pro.quizer.quizer3.MainActivity.TAG;
import static pro.quizer.quizer3.utils.FileUtils.moveFile;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.SmsAnswersR;
import pro.quizer.quizer3.database.models.SmsItemR;
import pro.quizer.quizer3.utils.FileUtils;

public class ClearAddressesExecutable extends BaseExecutable {

    private final Context mContext;
    private final ICallback mCallback;

    public ClearAddressesExecutable(final Context pContext, final ICallback pCallback) {
        super(pCallback);

        mContext = pContext;
        mCallback = pCallback;
    }

    @Override
    public void execute() {
        onStarting();

        try {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        getQuizerDatabase().getQuizerDao().clearAddresses();
                        getQuizerDatabase().getQuizerDao().setSettingsAddressDBVer(0L);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    onSuccess();
                }
            }, 3000);


        } catch (Exception e) {
            Log.d(TAG, mContext.getString(R.string.db_clear_error));
            onError(e);
        }

    }
}
