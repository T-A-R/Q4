package pro.quizer.quizer3.view.fragment;

import pro.quizer.quizer3.R;
import pro.quizer.quizer3.model.ElementSubtype;

public class TransFragment extends ScreenFragment {

    private int startElementId;
    private boolean resumed;

    public TransFragment() {
        super(R.layout.fragment_trans);
    }

    public TransFragment setStartElement(Integer startElementId) {
        this.startElementId = startElementId;
        this.resumed = false;
        return this;
    }

    public TransFragment setStartElement(Integer startElementId, boolean resumed) {
        this.startElementId = startElementId;
        this.resumed = resumed;
        return this;
    }

    @Override
    protected void onReady() {

        ElementFragment fragment = new ElementFragment();
        fragment.setStartElement(startElementId, resumed);
        replaceFragment(fragment);

    }
}
