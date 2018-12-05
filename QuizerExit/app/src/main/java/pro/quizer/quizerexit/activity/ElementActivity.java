package pro.quizer.quizerexit.activity;

import android.os.Bundle;

import java.util.List;

import pro.quizer.quizerexit.NavigationCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.fragment.ElementFragment;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.database.UserModel;

public class ElementActivity extends BaseActivity implements NavigationCallback {

    ConfigModel mConfig;
    List<ElementModel> mElements;

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
        final ElementModel nextElement = getElementByRelativeId(pNumberOfNextElement);

        if (nextElement == null) {
            // TODO: 27.10.2018 it was last Element and we need to finish
            // I CAN GET ALL INFO FROM mCurrentElement, because this model changed after selection
            showToast("это был последний вопрос");

            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, ElementFragment.newInstance(nextElement, this))
                .addToBackStack(nextElement.getAttributes().getTitle())
                .commit();
    }

    private ElementModel getElementByRelativeId(final int pRelativeId) {
        if (pRelativeId == 0) {
            return null;
        }

        for (final ElementModel element : mElements) {
            if (element.getRelativeID() == pRelativeId) {
                return element;
            }
        }

        return null;
    }

    @Override
    public void onForward(final ElementModel pElementModel) {
        final StringBuilder sb = new StringBuilder();
        int jumpValue = -1;

        for (int index = 0; index < pElementModel.getElements().size(); index++) {
            final ElementModel model = pElementModel.getElements().get(index);

            if (model.isFullySelected()) {
                sb.append(model.getAttributes().getTitle()).append("\n");
                jumpValue = model.getAttributes().getJump();
            }
        }

        showToast(sb.toString());

        if (jumpValue == -1) {
            showToast(getString(R.string.error_counting_next_element));
        } else {
            showNextElement(jumpValue);
        }
    }

    @Override
    public void onBack() {
        onBackPressed();
    }
}