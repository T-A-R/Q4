package pro.quizer.quizer3.view.fragment;

import pro.quizer.quizer3.R;

public class TransFragment extends ScreenFragment {

    private int startElementId;

    public TransFragment() {
        super(R.layout.fragment_trans);
    }

    public TransFragment setStartElement(Integer startElementId) {
        this.startElementId = startElementId;
        return this;
    }

    @Override
    protected void onReady() {
        ElementFragment fragment = new ElementFragment();
        fragment.setStartElement(startElementId);
        replaceFragment(fragment);
    }
}
