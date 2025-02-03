package pro.quizer.quizer3.executable;

import pro.quizer.quizer3.database.QuizerDao;
import pro.quizer.quizer3.database.models.EncryptionTableR;

public class FillEncryptionTableExecutable extends BaseExecutable {

    private final ICallback mCallback;
    private final QuizerDao mDao;

    public FillEncryptionTableExecutable(final QuizerDao dao, final ICallback pCallback) {
        super(pCallback);

        mDao = dao;
        mCallback = pCallback;
    }

    @Override
    public void execute() {
        onStarting();

        try {
            mDao.insertEncryptionTableR(new EncryptionTableR(' ', 'q'));
            mDao.insertEncryptionTableR(new EncryptionTableR(' ', 'p'));
            mDao.insertEncryptionTableR(new EncryptionTableR(' ', '0'));

            mDao.insertEncryptionTableR(new EncryptionTableR('.', 'w'));
            mDao.insertEncryptionTableR(new EncryptionTableR('.', 'o'));
            mDao.insertEncryptionTableR(new EncryptionTableR('.', '2'));

            mDao.insertEncryptionTableR(new EncryptionTableR(',', 'e'));
            mDao.insertEncryptionTableR(new EncryptionTableR(',', 'i'));
            mDao.insertEncryptionTableR(new EncryptionTableR(',', '6'));

            mDao.insertEncryptionTableR(new EncryptionTableR(':', '3'));
            mDao.insertEncryptionTableR(new EncryptionTableR(':', '9'));
            mDao.insertEncryptionTableR(new EncryptionTableR(':', '5'));

            mDao.insertEncryptionTableR(new EncryptionTableR('1', 't'));
            mDao.insertEncryptionTableR(new EncryptionTableR('1', 'y'));
            mDao.insertEncryptionTableR(new EncryptionTableR('1', '1'));

            mDao.insertEncryptionTableR(new EncryptionTableR('2', 'a'));
            mDao.insertEncryptionTableR(new EncryptionTableR('2', 'l'));
            mDao.insertEncryptionTableR(new EncryptionTableR('2', '8'));

            mDao.insertEncryptionTableR(new EncryptionTableR('3', 's'));
            mDao.insertEncryptionTableR(new EncryptionTableR('3', 'k'));
            mDao.insertEncryptionTableR(new EncryptionTableR('3', '4'));

            mDao.insertEncryptionTableR(new EncryptionTableR('4', 'd'));
            mDao.insertEncryptionTableR(new EncryptionTableR('4', 'j'));

            mDao.insertEncryptionTableR(new EncryptionTableR('5', 'f'));
            mDao.insertEncryptionTableR(new EncryptionTableR('5', 'h'));

            mDao.insertEncryptionTableR(new EncryptionTableR('6', 'g'));
            mDao.insertEncryptionTableR(new EncryptionTableR('6', 'v'));

            mDao.insertEncryptionTableR(new EncryptionTableR('7', 'z'));
            mDao.insertEncryptionTableR(new EncryptionTableR('7', 'm'));

            mDao.insertEncryptionTableR(new EncryptionTableR('8', 'x'));
            mDao.insertEncryptionTableR(new EncryptionTableR('8', 'n'));

            mDao.insertEncryptionTableR(new EncryptionTableR('9', 'b'));
            mDao.insertEncryptionTableR(new EncryptionTableR('9', 'c'));

            mDao.insertEncryptionTableR(new EncryptionTableR('0', 'r'));
            mDao.insertEncryptionTableR(new EncryptionTableR('0', 'u'));
            mDao.insertEncryptionTableR(new EncryptionTableR('0', '7'));

            onSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            onError(e);
        }
    }
}
