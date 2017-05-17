package com.divofmod.quizer;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.Dictionary;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

class DoRequest {

    private Context mContext;

    DoRequest(Context context) {
        mContext = context;
    }

    Request Post(Dictionary<String, String> dictionary, String url) {
        RequestBody formBody = null;
        switch (dictionary.get("name_form")) {
            case "key_client":
                formBody = new FormBody.Builder()
                        .add("name_form", "key_client")
                        .add("key", dictionary.get("key"))
                        .build();
                break;
            case "user_login":
                formBody = new FormBody.Builder()
                        .add("name_form", "user_login")
                        .add("login_admin", dictionary.get("login_admin"))
                        .add("login", dictionary.get("login"))
                        .add("passw", dictionary.get("passw"))
                        .build();
                break;
            case "download_update":
                formBody = new FormBody.Builder()
                        .add("name_form", "download_update")
                        .add("login_admin", dictionary.get("login_admin"))
                        .add("login", dictionary.get("login"))
                        .add("passw", dictionary.get("passw"))
                        .add("name_file", dictionary.get("name_file"))
                        .build();
                break;
            case "quota_question_answer":
                formBody = new FormBody.Builder()
                        .add("name_form", "quota_question_answer")
                        .add("login_admin", dictionary.get("login_admin"))
                        .add("login", dictionary.get("login"))
                        .add("passw", dictionary.get("passw"))
                        .build();
                break;
        }

        return new Request.Builder()
                .url(url)
                .post(formBody).build();
    }

    Request Post(Dictionary<String, String> dictionary, String url, ArrayList<String[]> question, ArrayList<String[]> selectiveQuestion) {
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)

                .addFormDataPart("name_form", "questionnaire")
                .addFormDataPart("login_admin", dictionary.get("login_admin"))
                .addFormDataPart("login", dictionary.get("login"))
                .addFormDataPart("sess_login", dictionary.get("sess_login"))
                .addFormDataPart("sess_passw", dictionary.get("sess_passw"))
                .addFormDataPart("project_id", dictionary.get("project_id"))
                .addFormDataPart("questionnaire_id", dictionary.get("questionnaire_id"))
                .addFormDataPart("user_project_id", dictionary.get("user_project_id"))
                .addFormDataPart("date_interview", dictionary.get("date_interview"))
                .addFormDataPart("gps", dictionary.get("gps"))
                .addFormDataPart("duration_time_questionnaire", dictionary.get("duration_time_questionnaire"))
                .addFormDataPart("selected_questions", dictionary.get("selected_questions"));

        for (int i = 0; i < question.size(); i++) {
            multipartBodyBuilder.addFormDataPart("answers[" + i + "][answer_id]", question.get(i)[0])
                    .addFormDataPart("answers[" + i + "][duration_time_question]", question.get(i)[1])
                    .addFormDataPart("answers[" + i + "][text_open_answer]", question.get(i)[2]);
        }
        for (int i = 0; i < selectiveQuestion.size(); i++) {
            multipartBodyBuilder.addFormDataPart("answers_selective[" + i + "][selective_answer_id]", selectiveQuestion.get(i)[0])
                    .addFormDataPart("answers_selective[" + i + "][duration_time_question]", selectiveQuestion.get(i)[1]);
        }
        if (dictionary.get("photo") != null)
            multipartBodyBuilder.addFormDataPart("photo", dictionary.get("photo"),
                    RequestBody.create(MediaType.parse("image/*"), new File(mContext.getFilesDir(), "files/" + dictionary.get("photo"))));

        return new Request.Builder()
                .url(url)
                .post(multipartBodyBuilder.build()).build();
    }


    Request Post(Dictionary<String, String> dictionary, String url, ArrayList<String[]> audio) {
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)

                .addFormDataPart("name_form", "audio_file")
                .addFormDataPart("login_admin", dictionary.get("login_admin"))
                .addFormDataPart("login", dictionary.get("login"))
                .addFormDataPart("passw", dictionary.get("passw"));

        if (audio.size() == 1)
            multipartBodyBuilder.addFormDataPart("audio_record", audio.get(0)[0] + ".amr",
                    RequestBody.create(MediaType.parse("audio/*"), new File(mContext.getFilesDir(), "files/" + audio.get(0)[0] + ".amr")));
        else
            for (int i = 0; i < audio.size(); i++) {
                multipartBodyBuilder.addFormDataPart("audio_record[" + i + "]", audio.get(i)[0] + ".amr",
                        RequestBody.create(MediaType.parse("audio/*"), new File(mContext.getFilesDir(), "files/" + audio.get(i)[0] + ".amr")));
            }

        return new Request.Builder()
                .url(url)
                .post(multipartBodyBuilder.build()).build();
    }

    Request Post(Dictionary<String, String> dictionary, String url, String photo) {
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)

                .addFormDataPart("name_form", "statistic")
                .addFormDataPart("login_admin", dictionary.get("login_admin"))
                .addFormDataPart("login", dictionary.get("login"))
                .addFormDataPart("passw", dictionary.get("passw"));

        multipartBodyBuilder.addFormDataPart("photo", photo + ".jpg",
                RequestBody.create(MediaType.parse("image/*"), new File(mContext.getFilesDir(), "files/" + photo + ".jpg")));

        return new Request.Builder()
                .url(url)
                .post(multipartBodyBuilder.build()).build();
    }

}
