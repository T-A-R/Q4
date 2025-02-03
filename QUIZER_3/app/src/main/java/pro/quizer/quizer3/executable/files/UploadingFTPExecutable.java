package pro.quizer.quizer3.executable.files;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.commons.net.ftp.FTPSClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.executable.BaseExecutable;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.utils.FileUtils;

public class UploadingFTPExecutable extends BaseExecutable {

    private final MainActivity mContext;
    private List<File> mFileList;

    public UploadingFTPExecutable(final MainActivity pContext, final ICallback pCallback) {
        super(pCallback);

        mContext = pContext;
    }

    @Override
    public void execute() {
        new UploadingExecutable(mContext, new ICallback() {
            @Override
            public void onStarting() {

            }

            @Override
            public void onSuccess() {
                sendFTPFiles();
            }

            @Override
            public void onError(Exception pException) {

            }
        }).execute();
    }

    private void sendFTPFiles() {
        mFileList = new ArrayList<>();
        mFileList.addAll(FileUtils.getFilesRecursion(FileUtils.JSON));
        mFileList.addAll(FileUtils.getFilesRecursion(FileUtils.AMR));
        mFileList.addAll(FileUtils.getFilesRecursion(FileUtils.JPEG));

        if (mFileList.isEmpty()) {
            return;
        }

        final UploadFTPAsyncTask uploadFTPAsyncTask = new UploadFTPAsyncTask();
        uploadFTPAsyncTask.execute(mFileList);
    }

    class UploadFTPAsyncTask extends AsyncTask<List<File>, Void, Void> {
        @Override
        protected Void doInBackground(List<File>... lists) {
            try {
                final FTPSClient mFTPClient = new FTPSClient();
                mFTPClient.connect(Constants.FTP.server, Constants.FTP.port);
                mFTPClient.execPBSZ(0);
                mFTPClient.execPROT("P");


                boolean status = mFTPClient.login(Constants.FTP.user, Constants.FTP.password);

//                if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
//                    mFTPClient.setFileType(FTP.ASCII_FILE_TYPE);
//                    mFTPClient.enterLocalPassiveMode();
//                }

                for (final File file : lists[0]) {
                    FileInputStream srcFileStream = new FileInputStream(file);

                    try {
                        if (mFTPClient.storeFile(file.getName(), srcFileStream)) {
//                            FileUtils.renameFile()
                            // TODO: 4/9/2019 can remove file
                        }
                    } catch (Exception e) {

                    } finally {
                        srcFileStream.close();
                    }
                }

                mFTPClient.logout();
                mFTPClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }


//            try {
//                SimpleFTP ftp = new SimpleFTP();
//
//                ftp.connect(Constants.FTP.server, Constants.FTP.port, Constants.FTP.user, Constants.FTP.password);
//                ftp.bin();
//                ftp.cwd("web");
//
//                for (final File file : lists[0]) {
//                    try {
//                        if (ftp.stor(file)) {
//                            // TODO: 4/9/2019 can remove file
//                        }
//                    } catch (Exception e) {
//
//                    }
//                }
//
//
//                ftp.disconnect();
//            } catch (IOException e) {
//                // Jibble.
//            }

//            try {
//                final FtpSender ftp = new FtpSender();
//                ftp.connect(Constants.FTP.server, Constants.FTP.port, Constants.FTP.user, Constants.FTP.password);
//
//                for (final File file : lists[0]) {
//                    try {
//                        ftp.uploadFile(file);
//
//                        // TODO: 4/9/2019 can remove file
//                    } catch (Exception e) {
//                        // TODO: 4/9/2019  cannot remove file
//                    }
//                }
//
//                return null;
//            } catch (Exception e) {
//                return null;
//            }
            return null;
        }
    }
}
