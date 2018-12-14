package pro.quizer.quizerexit.executable;

import pro.quizer.quizerexit.model.view.SyncViewModel;

public class SyncInfoExecutable extends BaseModelExecutable<SyncViewModel> {

    public SyncInfoExecutable() {
        super();
    }

    @Override
    public SyncViewModel execute() {
        final SyncViewModel syncViewModel = new SyncViewModel();

        return syncViewModel;
    }
}