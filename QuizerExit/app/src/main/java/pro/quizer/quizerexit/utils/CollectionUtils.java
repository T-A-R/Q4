package pro.quizer.quizerexit.utils;

import java.util.Collections;
import java.util.List;

import pro.quizer.quizerexit.model.ElementSubtype;
import pro.quizer.quizerexit.model.ElementType;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.OptionsModel;

import static pro.quizer.quizerexit.model.config.ElementModel.COMPARATOR;

public class CollectionUtils {

    public static void sortByOrder(final List<ElementModel> pList) {
        Collections.sort(pList, COMPARATOR);
    }

    public static void shuffleAnswers(final ElementModel pParent, final List<ElementModel> pList) {
        if (pParent.isShuffeled()) {
            return;
        }

        sortByOrder(pList);

        Collections.shuffle(pList);

        for (int i = 0; i < pList.size(); i++) {
            final ElementModel answer = pList.get(i);
            final OptionsModel attributes = answer.getOptions();
            final int realOrder = attributes.getOrder() - 1;

            if (attributes.isFixedOrder() && realOrder != i) {
                pList.remove(answer);
                pList.add(attributes.getOrder() - 1, answer);

                i = 0;
            }
        }

        if (ElementType.BOX.equals(pParent.getType()) && ElementSubtype.CONTAINER.equals(pParent.getSubtype())) {
            final List<ElementModel> questions = pParent.getElements();
            final int questionsSize = questions.size();

            for (int i = 0; i < questionsSize; i++) {
                final ElementModel question = questions.get(i);
                final List<ElementModel> answers = question.getElements();
                int jump;

                if (i == questionsSize - 1) {
                    jump = pParent.getOptions().getJump();
                } else {
                    final int next = i + 1;
                    jump = questions.get(next).getRelativeID();
                }

                for (final ElementModel answer : answers) {
                    answer.getOptions().setJump(jump);
                }
            }
        }

        pParent.setShuffeled(true);
    }
}
