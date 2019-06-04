package pro.quizer.quizerexit.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.ElementType;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.OptionsModel;
import pro.quizer.quizerexit.model.quota.QuotaModel;

public final class QuotasUtils {

    public static boolean isCanDisplayed(final List<QuotaModel> quotas, final HashMap<Integer, ElementModel> mMap, final ElementModel pElementModel, final BaseActivity pBaseActivity) {
        if (quotas == null || quotas.isEmpty()) {
            return true;
        }

        LogUtils.startAction("isCanDisplayed");

        final String type = pElementModel.getType();
        final boolean isCanDisplayed;
        if (ElementType.ANSWER.equals(type)) {
            isCanDisplayed = isAnswerCanDisplayed(quotas, mMap, pElementModel, pBaseActivity);
        } else if (ElementType.QUESTION.equals(type)) {
            isCanDisplayed = isQuestionCanDisplayed(quotas, mMap, pElementModel, pBaseActivity);
        } else {
            isCanDisplayed = true;
        }

        LogUtils.endAction("isCanDisplayed");

        return isCanDisplayed;
    }

    private static boolean isQuestionCanDisplayed(final List<QuotaModel> quotas, final HashMap<Integer, ElementModel> mMap, final ElementModel pElementModel, final BaseActivity pBaseActivity) {
        // вопрос может отоброзиться только если все внутренние элементы типа "answer" !getOptions().isCanShow(),
        // в свою очередь isCanShow проверит то, что может ли отображаться данный ответ исходя из pre_condition,
        // а также исходя из квот

        for (final ElementModel answer : pElementModel.getSubElementsByType(ElementType.ANSWER)) {
            final OptionsModel optionsModel = answer.getOptions();
            final boolean isCanShow = optionsModel.isCanShow(pBaseActivity, mMap, answer);
            final boolean isEnabled = optionsModel.isEnabled(quotas, pBaseActivity, mMap, answer);

            if (isCanShow && isEnabled) {
                return true;
            }
        }

        return false;
    }

    private static boolean isAnswerCanDisplayed(final List<QuotaModel> quotas, final HashMap<Integer, ElementModel> mMap, final ElementModel pElementModel, final BaseActivity pBaseActivity) {
        // если сиквенс содержит pElementModel делаем магию, иначе return TRUE;


        // если это начало сиквенса (isStartedElement()): может отображаться ТОЛЬКО если DONE < limit (сделал isCompleted())!!!!!!! ЗНАЧИТ !isCompleted()
        // ХОТЯ БЫ в одном из сиквенсов (которые стартуют на текущем элементе)
        // ИЛИ ЕСТЬ ХОТЯБЫ один не заквотированный ответ в следующем элементе
        // тогда действия: получили все сиквенсы начинающиеся на данный relativeId, взяли done + localDone
        // (а что если done уже будет возвращать + localDone) = СДЕЛАНО!!!!!!

        // если это середина сиквенса getSet().contains(id)
        //
        //

        if (!isQuotedElement(quotas, pElementModel)) {
            return true;
        }

        final List<QuotaModel> notCompletedCurrentQuotas = getNotCompletedQuotasForCurrentQuestionnaire(quotas, mMap, pElementModel, pBaseActivity);
        final List<QuotaModel> allSelectedQuotasForCurrentQuestionnaire = getAllSelectedQuotasForCurrentQuestionnaire(quotas, mMap, pElementModel);

        if ((notCompletedCurrentQuotas != null && !notCompletedCurrentQuotas.isEmpty())
                || isNextQuestionContainsNotQuotedAnswer(allSelectedQuotasForCurrentQuestionnaire, mMap, pElementModel)
                || isCurrentAnswerIsNotQuotedInQuotasForCurrentQuestionnaire(allSelectedQuotasForCurrentQuestionnaire)) {
            return true;
        }

        return false;
    }

    private static boolean isCurrentAnswerIsNotQuotedInQuotasForCurrentQuestionnaire(final List<QuotaModel> pCurrentQuotas) {
        LogUtils.startAction("isCurrentAnswerIsNotQuotedInQuotasForCurrentQuestionnaire");

        if (pCurrentQuotas == null || pCurrentQuotas.isEmpty()) {
            LogUtils.endAction("isCurrentAnswerIsNotQuotedInQuotasForCurrentQuestionnaire");
            return true;
        } else {
            return false;
        }
//        final int currentAnswerId = pElementModel.getRelativeID();
//
//        for (final QuotaModel quotaModel : pCurrentQuotas) {
//            if (!quotaModel.contains(currentAnswerId)) {
//                return true;
//            }
//        }
//
//        return false;
    }

    private static boolean isQuotedElement(final List<QuotaModel> allQuotas, final ElementModel pElementModel) {
        final int id = pElementModel.getRelativeID();

        for (final QuotaModel quotaModel : allQuotas) {
            if (quotaModel.contains(id)) {
                return true;
            }
        }

        return false;
    }

    private static List<QuotaModel> getNotCompletedQuotasForCurrentQuestionnaire(final List<QuotaModel> quotas, final HashMap<Integer, ElementModel> mMap, final ElementModel pElementModel, final BaseActivity pBaseActivity) {
        final List<QuotaModel> result = new ArrayList<>();
        final int elementId = pElementModel.getRelativeID();

        for (final QuotaModel quota : quotas) {
            if (quota.contains(elementId)) {

//                if (!quota.isCompleted(pBaseActivity) && isPreviousQuotasSelected(mMap, pElementModel, quota)) {
                if (isPreviousQuotasSelected(mMap, pElementModel, quota) && !quota.isCompleted()) {
                    result.add(quota);
                }
            }
        }

        return result;
    }

    private static List<QuotaModel> getAllSelectedQuotasForCurrentQuestionnaire(final List<QuotaModel> quotas, final HashMap<Integer, ElementModel> mMap, final ElementModel pElementModel) {
        final List<QuotaModel> result = new ArrayList<>();

        for (final QuotaModel quota : quotas) {
            if (quota.contains(pElementModel.getRelativeID()) && isPreviousQuotasSelected(mMap, pElementModel, quota)) {
                result.add(quota);
            }
        }

        return result;
    }

//    private static List<QuotaModel> getAllQuotasForCurrentQuestionnaire(final HashMap<Integer, ElementModel> mMap, final ElementModel pElementModel, final BaseActivity pBaseActivity) {
//        final List<QuotaModel> result = new ArrayList<>();
//        final List<QuotaModel> quotas = pBaseActivity.getCurrentUser().getQuotas();
//
//        for (final QuotaModel quota : quotas) {
//            if (quota.contains(pElementModel.getRelativeID())) {
//                result.add(quota);
//            }
//        }
//
//        return result;
//    }

    private static boolean isNextQuestionContainsNotQuotedAnswer(final List<QuotaModel> pCurrentQuotas,
                                                                 final HashMap<Integer, ElementModel> mMap,
                                                                 final ElementModel pElementModel) {
        LogUtils.startAction("isNextQuestionContainsNotQuotedAnswer");

        boolean result = false;

        if (pCurrentQuotas == null) {
            result = true;
        }

        final Set<Integer> nextElementIds = new HashSet<>();
        final int currentId = pElementModel.getRelativeID();

        for (final QuotaModel quotaModel : pCurrentQuotas) {
            final Integer[] array = quotaModel.getArray();

            for (int i = 0; i < array.length; i++) {
                final int tmpId = array[i];

                if (currentId == tmpId && i != array.length - 1) {
                    nextElementIds.add(array[i + 1]);
                }
            }
        }

        if (nextElementIds.size() == 0) {
            result = false;
        }

        final Set<ElementModel> nextElements = new HashSet<>();

        for (final int id : nextElementIds) {
            nextElements.add(mMap.get(id));
        }

        final Set<ElementModel> nextParents = new HashSet<>();

        for (final ElementModel elementModel : nextElements) {
            final ElementModel parent = getParentElement(elementModel.getRelativeParentID(), mMap);

            if (parent != null) {
                nextParents.add(parent);
            }
        }

        final Set<Integer> nextChildrens = new HashSet<>();

        for (final ElementModel elementModel : nextParents) {
            for (final ElementModel childrens : elementModel.getElements()) {
                nextChildrens.add(childrens.getRelativeID());
            }
        }

        for (final int childrenId : nextChildrens) {
            if (!nextElementIds.contains(childrenId)) {
                result = true;
            }
        }

        LogUtils.endAction("isNextQuestionContainsNotQuotedAnswer");

        return result;
    }

    private static ElementModel getParentElement(final int id, final HashMap<Integer, ElementModel> mMap) {
        for (final Map.Entry<Integer, ElementModel> elementModel : mMap.entrySet()) {
            final ElementModel model = elementModel.getValue();

            if (model.getRelativeID() == id) {
                return model;
            }
        }

        return null;
    }
    private static boolean isPreviousQuotasSelected(final HashMap<Integer, ElementModel> mMap, final ElementModel pElementModel, final QuotaModel pQuota) {
        final int currentRelativeId = pElementModel.getRelativeID();
        for (final int id : pQuota.getArray()) {
            if (currentRelativeId == id) {
                return true;
            }

            final ElementModel tmpElement = mMap.get(id);

            if (tmpElement == null) {
                return true;
            }

            if (!tmpElement.isFullySelected()) {
                return false;
            }
        }

        return true;
    }
}