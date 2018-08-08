package com.divofmod.quizer.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.divofmod.quizer.Constants.Constants;
import com.divofmod.quizer.DataBase.DBReader;
import com.divofmod.quizer.model.Config.AnswersField;
import com.divofmod.quizer.model.Config.ConfigField;
import com.divofmod.quizer.model.Config.ConfigResponseModel;
import com.divofmod.quizer.model.Config.QuestionsField;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public final class Utils {

    public static void saveConfig(final Context pContext, final ConfigResponseModel pConfigResponseModel) {
        final SharedPreferences.Editor editor = getShared(pContext).edit().putString(Constants.Shared.CONFIG, new Gson().toJson(pConfigResponseModel));
        editor.apply();
    }

    public static ConfigResponseModel getConfig(final Context pContext) {
        final String json = getShared(pContext).getString(Constants.Shared.CONFIG, "");

        return new GsonBuilder().create().fromJson(json, ConfigResponseModel.class);
    }

    public static SharedPreferences getShared(final Context pContext) {
        return pContext.getSharedPreferences("data", Context.MODE_PRIVATE);
    }

    public static String getQuestionnaireId(final Context pContext) {
        final ConfigResponseModel configResponseModel = getConfig(pContext);

        return String.valueOf(configResponseModel.getConfig().getProject_info().getQuestionnaire_id());
    }

    public static String getProjectId(final Context pContext) {
        final ConfigResponseModel configResponseModel = getConfig(pContext);

        return String.valueOf(configResponseModel.getConfig().getProject_info().getProject_id());
    }

    public static ArrayList<String[]> getQuestionnaire(final Context pContext) {
        // TODO: 8/8/18 EMPTY JSON WTF?

//        DBReader.read(mSQLiteDatabase,
//                "questionnaire",
//                new String[]{"name", "description", "music", "picture", "picture_thankyou", "thankyou_text"})

        return new ArrayList<>();
    }

    public static ArrayList<String[]> getConfigValues(final Context pContext) {
        final ArrayList<String[]> arrayList = new ArrayList<>();
        final ConfigResponseModel configResponseModel = getConfig(pContext);
        final ConfigField cfg = configResponseModel.getConfig();

        arrayList.add(new String[]{"server", cfg.getServer()});
        arrayList.add(new String[]{"count_questions_min", cfg.getCount_questions_min()});
        arrayList.add(new String[]{"photo_questionnaire", cfg.getPhoto_questionnaire()});
        arrayList.add(new String[]{"gps", cfg.getGps()});
        arrayList.add(new String[]{"audio", cfg.getAudio()});
        arrayList.add(new String[]{"audio_speex_mode", ""}); // ???
        arrayList.add(new String[]{"audio_speex_quality", ""}); // ???
        arrayList.add(new String[]{"audio_speex_sample_rate", ""}); // ???
        arrayList.add(new String[]{"audio_record_questions", cfg.getAudio_record_questions()});
        arrayList.add(new String[]{"audio_record_limit_time", String.valueOf(cfg.getAudio_record_limit_time())});
        arrayList.add(new String[]{"autonomous_limit_count_questionnare", String.valueOf(cfg.getAutonomous_limit_count_questionnare())});
        arrayList.add(new String[]{"autonomous_limit_time_questionnare", String.valueOf(cfg.getAutonomous_limit_time_questionnare())});
        arrayList.add(new String[]{"delete_data_password", cfg.getDelete_data_password()});
        arrayList.add(new String[]{"quotas_applied", ""});

        return arrayList;
    }

    public static ArrayList<String[]> getQuestions(final Context pContext) {
        final ArrayList<String[]> arrayList = new ArrayList<>();
        final ConfigResponseModel configResponseModel = getConfig(pContext);
        final List<QuestionsField> questionsFieldList = configResponseModel.getConfig().getProject_info().getQuestions();

        for (final QuestionsField q : questionsFieldList) {
            final int type = q.getType();

            if (type == 1) {
                final String id = String.valueOf(q.getId());
                final String number = String.valueOf(q.getNumber());
                final String title = q.getTitle();
                final String polyanswer = String.valueOf(q.getOptions().getPolyanswer());
                final String max_answers = "0";
                final String picture = "";
                final String questionnaire_id = "";
                final String next_question = "";
                final String is_filter = "";
                final String table_id = "0";
                final String next_after_selective_question = "0";

                arrayList.add(new String[]{id, number, title, polyanswer, max_answers, picture, questionnaire_id, next_question, is_filter, table_id, next_after_selective_question});
            }
        }

        return arrayList;
    }

    public static ArrayList<String[]> getSelectiveQuestions(final Context pContext) {
        final ArrayList<String[]> arrayList = new ArrayList<>();
        final ConfigResponseModel configResponseModel = getConfig(pContext);
        final List<QuestionsField> questionsFieldList = configResponseModel.getConfig().getProject_info().getQuestions();

        for (final QuestionsField q : questionsFieldList) {
            final int type = q.getType();

            if (type == 2) {
                final String id = String.valueOf(q.getId());
                final String num = String.valueOf(q.getNumber());
                final String title = q.getTitle();
                final String question_id = String.valueOf(q.getId());

                arrayList.add(new String[]{id, num, title, question_id});
            }
        }

        return arrayList;
    }

    public static ArrayList<String[]> getAnswers(final Context pContext) {
        final ArrayList<String[]> arrayList = new ArrayList<>();
        final ConfigResponseModel configResponseModel = getConfig(pContext);
        final List<QuestionsField> questionsFieldList = configResponseModel.getConfig().getProject_info().getQuestions();

        for (final QuestionsField q : questionsFieldList) {
            final int type = q.getType();

            if (type == 1) {
                final List<AnswersField> answersFieldList = q.getAnswers();

                for (final AnswersField answersField : answersFieldList) {
                    final String id = String.valueOf(answersField.getId()); // WTF?
                    final String title = answersField.getTitle();
                    final String picture = "";
                    final String question_id = String.valueOf(q.getId());
                    final String next_question = String.valueOf(answersField.getNext_question());
                    final String is_open = "0";
                    final String table_question_id = String.valueOf(q.getId()); // WTF?

                    arrayList.add(new String[]{id, title, picture, question_id, next_question, is_open, table_question_id});
                }
            }
        }

        return arrayList;
    }

    public static ArrayList<String[]> getSelectiveAnswers(final Context pContext) {
        final ArrayList<String[]> arrayList = new ArrayList<>();
        final ConfigResponseModel configResponseModel = getConfig(pContext);
        final List<QuestionsField> questionsFieldList = configResponseModel.getConfig().getProject_info().getQuestions();

        for (final QuestionsField q : questionsFieldList) {
            final int type = q.getType();

            if (type == 2) {
                final List<AnswersField> answersFieldList = q.getAnswers();

                for (final AnswersField answersField : answersFieldList) {
                    final String id = String.valueOf(answersField.getId()); // WTF?
                    final String num = String.valueOf(answersField.getNumber());
                    final String title = answersField.getTitle();
                    final String title_search = "";
                    final String parent_num = "";
                    final String selective_question_id = "";

                    arrayList.add(new String[]{id, num, title, title_search, parent_num, selective_question_id});
                }
            }
        }

        return arrayList;
    }
}