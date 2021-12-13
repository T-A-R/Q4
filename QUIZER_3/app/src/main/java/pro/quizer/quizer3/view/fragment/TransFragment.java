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
        this.nextElementId = nextElementId;
        this.restored = false;
        return this;
    }

    public TransFragment setStartElement(Integer startElementId, Integer nextElementId) {
        this.startElementId = startElementId;
        this.nextElementId = nextElementId;
        this.restored = false;
        return this;
    }

    public TransFragment setStartElement(Integer nextElementId, boolean restored) {
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
                if (checkQuotaJump(nextElementId)) fillPassedQuotas();
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
        ElementItemR currentElement = getElement(relativeId);
        if (currentElement.getRelative_parent_id() != null && currentElement.getRelative_parent_id() != 0 &&
                getElement(currentElement.getRelative_parent_id()).getSubtype().equals(ElementSubtype.QUOTA)) {
            isQuota = true;
            quotaElementsList = getElement(currentElement.getRelative_parent_id()).getElements();
//            for (ElementItemR item : quotaElementsList) {
//                Log.d("T-L.TransFragment", "checkQuotaJump: >>> " + item.getRelative_id());
//            }
        }
        return isQuota;
    }

    private void fillPassedQuotas() {
        Integer startPosition = null;
        for (int i = 0; i < quotaElementsList.size(); i++) {
            if (quotaElementsList.get(i).getRelative_id().equals(startElementId)) {
                startPosition = i;
                break;
            }
        }
        if (startPosition != null && startPosition != (quotaElementsList.size() - 1)) {
            for (int i = startPosition + 1; i < quotaElementsList.size(); i++) {
//                Log.d("T-L.TransFragment", "fillPassedQuotas: " + quotaElementsList.get(i).getRelative_id() + "/" + nextElementId);

                if (!quotaElementsList.get(i).getRelative_id().equals(nextElementId)) {
                    //TODO ADD PASSED_QUOTA
                    savePassedElement(quotaElementsList.get(i).getRelative_id());
                } else break;
            }
        }
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
                elementPassedR.setFrom_quotas_block(true);
                getDao().insertElementPassedR(elementPassedR);
                break;
            }
        }
    }
}
