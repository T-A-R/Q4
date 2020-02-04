package pro.quizer.quizer3.utils;

import java.util.Collections;
import java.util.List;

import pro.quizer.quizer3.model.ElementSubtype;
import pro.quizer.quizer3.model.ElementType;
import pro.quizer.quizer3.model.config.ElementModel;
import pro.quizer.quizer3.model.config.OptionsModel;

import static pro.quizer.quizer3.model.config.ElementModel.COMPARATOR;

public class CollectionUtils {

    public static void sortByOrder(final List<ElementModel> pList) {
        Collections.sort(pList, COMPARATOR);
    }

    public static void shuffleTableAnswers(final ElementModel pParent, final List<ElementModel> pQuestions) {
//        if (pParent.isShuffeled() || pQuestions == null || pQuestions.isEmpty()) {
//            return;
//        }
//
//        final ElementModel exampleQuestion = pQuestions.get(0);
//        final List<ElementModel> exampleAnswers = exampleQuestion.getElements();
//
//        shuffleElements(exampleQuestion, exampleAnswers);
//
//        for (int i = 1; i < exampleAnswers.size(); i++) {
//
//        }
//
//        pParent.setShuffeled(true);
    }

    public static void shuffleElements(final ElementModel pParent, final List<ElementModel> pList) {
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
            pParent.setShuffeledIntoBox(true);

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
