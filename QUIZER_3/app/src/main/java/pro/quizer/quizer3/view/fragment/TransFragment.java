package pro.quizer.quizer3.view.fragment;

import pro.quizer.quizer3.R;

import static pro.quizer.quizer3.MainActivity.AVIA;

public class TransFragment extends ScreenFragment {

    private int startElementId;
    private boolean restored;

    public TransFragment() {
        super(R.layout.fragment_trans);
    }

    public TransFragment setStartElement(Integer startElementId) {
        this.startElementId = startElementId;
        this.restored = false;
        return this;
    }

    public TransFragment setStartElement(Integer startElementId, boolean restored) {
        this.startElementId = startElementId;
        this.restored = restored;
        return this;
    }

    @Override
    protected void onReady() {

//        PageElementFragment fragment = new PageElementFragment();
        if(!AVIA) {
            ElementFragment fragment = new ElementFragment();
            fragment.setStartElement(startElementId, restored);
            if (!restored)
                replaceFragment(fragment);
            else
                replaceFragmentBack(fragment);
        } else {
            ElementAviaFragment fragment = new ElementAviaFragment();
            fragment.setStartElement(startElementId, restored);
            if (!restored)
                replaceFragment(fragment);
            else
                replaceFragmentBack(fragment);
        }



    }
}
