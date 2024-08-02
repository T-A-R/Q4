package pro.quizer.quizer3.view.fragment;

import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.CurrentQuestionnaireR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.ElementPassedR;
import pro.quizer.quizer3.model.ElementSubtype;
import pro.quizer.quizer3.model.mappers.MapperQuizer;
import pro.quizer.quizer3.objectbox.models.ElementPassedOB;

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
        Log.d("T-A-R", "onReady TR: " + nextElementId);
        st("Trans +++");
        if (!AVIA) {
            ElementFragment fragment = new ElementFragment();
            fragment.setStartElement(nextElementId, restored);
            if (!restored) {
//                Log.d("T-A-R", "TRANS FR nextElementId: " + nextElementId + " " + checkQuotaJump(nextElementId));
                if (nextElementId != 0 && nextElementId != -1 && checkQuotaJump(nextElementId)) fillPassedQuotas();
                st("Trans ---");
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
        st("checkQuotaJump() +++");
        boolean isQuota = false;
//        Log.d("T-A-R.TransFragment", "======= checkQuotaJump: " + relativeId);
        ElementItemR currentElement = getElement(relativeId);
        if (currentElement.getRelative_parent_id() != null && currentElement.getRelative_parent_id() != 0 &&
                getElement(currentElement.getRelative_parent_id()).getSubtype().equals(ElementSubtype.QUOTA)) {
            isQuota = true;
            quotaElementsList = getElement(currentElement.getRelative_parent_id()).getElements();
//            if(!quotaElementsList.isEmpty()) {
//                for(int i = 0; i < quotaElementsList.size(); i++) {
//                    Log.d("T-A-R", "checkQuotaJump (" + i + ") ID: " + quotaElementsList.get(i).getRelative_id());
//                }
//            }
        }
        st("checkQuotaJump() ---");
        return isQuota;
    }

    private void fillPassedQuotas() {
        Log.d("T-A-R", "fillPassedQuotas: <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        Log.d("T-A-R", "startElementId: " + startElementId + " nextElementId:" + nextElementId);
        st("fillPassedQuotas() +++");
//        Log.d("T-A-R.TransFragment", "=== quotaElementsList: " + quotaElementsList.size());
//        Log.d("T-A-R.TransFragment", "=== startElementId: " + startElementId);
        int startPosition = -1;
        for (int i = 0; i < quotaElementsList.size(); i++) {
            if (quotaElementsList.get(i).getRelative_id().equals(startElementId)) {
                startPosition = i;
                break;
            }
        }
        Log.d("T-A-R", "startPosition: " + startPosition);
        if (startPosition == -1) {
            for (int i = 0; i < quotaElementsList.size(); i++) {
                if (!quotaElementsList.get(i).getRelative_id().equals(nextElementId)) {
                    Log.d("T-A-R", "savePassedElement: (-1)");
                    savePassedElement(quotaElementsList.get(i).getRelative_id());
                } else break;
            }
        } else if (startPosition == 0) {
            for (int i = 1; i < quotaElementsList.size(); i++) {
                if (!quotaElementsList.get(i).getRelative_id().equals(nextElementId)) {
                    Log.d("T-A-R", "savePassedElement: (0)");
                    savePassedElement(quotaElementsList.get(i).getRelative_id());
                } else break;
            }
        } else if (startPosition != (quotaElementsList.size() - 1)) {
            for (int i = startPosition + 1; i < quotaElementsList.size(); i++) {
                Log.d("T-A-R", "i: " + i);
                if (!quotaElementsList.get(i).getRelative_id().equals(nextElementId)) {
                    Log.d("T-A-R", "savePassedElement: (2)");
                    savePassedElement(quotaElementsList.get(i).getRelative_id());
                } else break;
            }
        }

        st("fillPassedQuotas() ---");
    }

    private void savePassedElement(int id) {
        CurrentQuestionnaireR quiz =  getQuestionnaire();
        Log.d("T-A-R.TransFragment", "><><><><><: " + id);

        st("savePassedElement() +++");

        ElementItemR currentElement = getElement(id);
        ElementPassedOB elementPassedR = new ElementPassedOB();
        elementPassedR.setRelative_id(id);
        elementPassedR.setProject_id(currentElement.getProjectId());
        elementPassedR.setToken(getQuestionnaire().getToken());
        elementPassedR.setHelper(true);
        elementPassedR.setFrom_quotas_block(false);
        elementPassedR.setIs_question(false);
//        getDao().insertElementPassedR(elementPassedR);
        getObjectBoxDao().insertElementPassedR(elementPassedR);
        setCondComp(elementPassedR.getRelative_id());

        List<ElementItemR> answers = currentElement.getElements();
        for (ElementItemR answer : answers) {
            if (answer.getElementOptionsR().isHelper()) {
                elementPassedR.setRelative_id(answer.getRelative_id());
                elementPassedR.setParent_id(answer.getRelative_parent_id());
                elementPassedR.setFrom_quotas_block(true);
                elementPassedR.setIs_question(false);
                Log.d("T-A-R.TransFragment", "savePassedElement 10: " + elementPassedR.getRelative_id());
//                getDao().insertElementPassedR(elementPassedR);
                getObjectBoxDao().insertElementPassedR(elementPassedR);
                break;
            }
        }
        st("savePassedElement() ---");
    }
}
