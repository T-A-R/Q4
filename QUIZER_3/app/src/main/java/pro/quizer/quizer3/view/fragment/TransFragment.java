package pro.quizer.quizer3.view.fragment;

import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.ElementPassedR;
import pro.quizer.quizer3.model.ElementSubtype;

import static pro.quizer.quizer3.MainActivity.AVIA;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class TransFragment extends ScreenFragment {

    private Integer startElementId;
    private Integer nextElementId;
    private boolean restored;
    private List<ElementItemR> quotaElementsList = new ArrayList<>();

    public TransFragment() {
        super(R.layout.fragment_trans);
    }

    public TransFragment setStartElement(Integer nextElementId) {
        Log.d("T-A-R.TransFragment", "setStartElement: 1");
        this.nextElementId = nextElementId;
        this.restored = false;
        return this;
    }

    public TransFragment setStartElement(Integer startElementId, Integer nextElementId) {
        Log.d("T-A-R.TransFragment", "setStartElement: 2");
        this.startElementId = startElementId;
        this.nextElementId = nextElementId;
        this.restored = false;
        return this;
    }

    public TransFragment setStartElement(Integer nextElementId, boolean restored) {
        Log.d("T-A-R.TransFragment", "setStartElement: 3");
        this.nextElementId = nextElementId;
        this.restored = restored;
        return this;
    }

    @Override
    protected void onReady() {

        if (!AVIA) {
            ElementFragment fragment = new ElementFragment();
            fragment.setStartElement(nextElementId, restored);
            if (!restored) {
                if (nextElementId != 0 && nextElementId != -1 && checkQuotaJump(nextElementId)) fillPassedQuotas();
                replaceFragment(fragment);
            } else
                replaceFragmentBack(fragment);
        } else {
            ElementAviaFragment fragment = new ElementAviaFragment();
            fragment.setStartElement(nextElementId, restored);
            if (!restored)
                replaceFragment(fragment);
            else
                replaceFragmentBack(fragment);
        }


    }

    private boolean checkQuotaJump(int relativeId) {
        boolean isQuota = false;
        Log.d("T-A-R.TransFragment", "======= checkQuotaJump: " + relativeId);
        ElementItemR currentElement = getElement(relativeId);
        if (currentElement.getRelative_parent_id() != null && currentElement.getRelative_parent_id() != 0 &&
                getElement(currentElement.getRelative_parent_id()).getSubtype().equals(ElementSubtype.QUOTA)) {
            isQuota = true;
            quotaElementsList = getElement(currentElement.getRelative_parent_id()).getElements();
        }
//        Log.d("T-A-R.TransFragment", "checkQuotaJump: " + isQuota);
        return isQuota;
    }

    private void fillPassedQuotas() {
//        Log.d("T-A-R.TransFragment", "=== quotaElementsList: " + quotaElementsList.size());
//        Log.d("T-A-R.TransFragment", "=== startElementId: " + startElementId);
        int startPosition = 0;
        for (int i = 0; i < quotaElementsList.size(); i++) {
            if (quotaElementsList.get(i).getRelative_id().equals(startElementId)) {
                startPosition = i;
                break;
            }
        }

        if(startPosition == 0) {
            for (int i = 0; i < quotaElementsList.size(); i++) {
                if (!quotaElementsList.get(i).getRelative_id().equals(nextElementId)) {
                    savePassedElement(quotaElementsList.get(i).getRelative_id());
                } else break;
            }
        } else if (startPosition != (quotaElementsList.size() - 1)) {
            for (int i = startPosition + 1; i < quotaElementsList.size(); i++) {
                if (!quotaElementsList.get(i).getRelative_id().equals(nextElementId)) {
                    savePassedElement(quotaElementsList.get(i).getRelative_id());
                } else break;
            }
        }

//        List<ElementPassedR> passed = getDao().getQuotaPassedElements(getMainActivity().getToken(), true);
//        for(ElementPassedR item : passed) {
//            Log.d("T-A-R.TransFragment", "passed: " + item.getRelative_id());
//        }
    }

    private void savePassedElement(int id) {
        ElementItemR currentElement = getElement(id);
        ElementPassedR elementPassedR = new ElementPassedR();
        elementPassedR.setRelative_id(id);
        elementPassedR.setProject_id(currentElement.getProjectId());
        elementPassedR.setToken(getQuestionnaire().getToken());
        elementPassedR.setHelper(true);
        elementPassedR.setFrom_quotas_block(false);
        getDao().insertElementPassedR(elementPassedR);

        List<ElementItemR> answers = currentElement.getElements();
        for (ElementItemR answer : answers) {
            if (answer.getElementOptionsR().isHelper()) {
                elementPassedR.setRelative_id(answer.getRelative_id());
                elementPassedR.setParent_id(answer.getRelative_parent_id());
                elementPassedR.setFrom_quotas_block(true);
                Log.d("T-A-R.TransFragment", "savePassedElement 10: " + elementPassedR.getRelative_id());
                getDao().insertElementPassedR(elementPassedR);
                break;
            }
        }
    }
}
