package pro.quizer.quizerexit.activity;

import android.os.Bundle;

import java.util.List;

import pro.quizer.quizerexit.OnNextElementCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.fragment.ElementFragment;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.database.UserModel;

public class ElementActivity extends BaseActivity implements OnNextElementCallback {

    ConfigModel mConfig;
    List<ElementModel> mElements;

    @Override
    public void onNextElement(final ElementModel pElementModel) {
//        final StringBuilder sb = new StringBuilder();
//
//        for (int index = 0; index < pSubElements.size(); index++) {
//            final ElementModel model = pSubElements.get(index);
//
//            sb.append(model.getAttributes().getTitle()).append("\n");
//        }
//
//        showToastMessage(sb.toString());
//
//        showNextElement(pNextElement);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element);

        initView();
    }

    private void initView() {
        final UserModel userModel = getCurrentUser();
        mConfig = userModel.getConfig();
        mElements = mConfig.getProjectInfo().getElements();

        getSupportFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, ElementFragment.newInstance(mElements.get(0), this))
                .commit();
    }

    private void showNextElement(final int pNumberOfNextElement) {
        final ElementModel nextElement = getElementByNumber(pNumberOfNextElement);

        if (nextElement == null) {
            // TODO: 27.10.2018 it was last Element and we need to finish
            // I CAN GET ALL INFO FROM mCurrentElement, because this model changed after selection
            showToastMessage("это был последний вопрос");

            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, ElementFragment.newInstance(nextElement, this))
                .addToBackStack(nextElement.getAttributes().getTitle())
                .commit();
    }

    private ElementModel getElementByNumber(final int pElementNumber) {
        if (pElementNumber == 0) {
            return null;
        }

        for (final ElementModel element : mElements) {
            if (element.getAttributes().getNumber() == pElementNumber) {
                return element;
            }
        }

        return null;
    }
}