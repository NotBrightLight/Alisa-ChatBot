package ru.brightlight.alisa;

import java.util.ArrayList;

public class QuestionResponse {
    String response;
    ArrayList<ArrayList<String>> questions;

    public QuestionResponse(String response, ArrayList<ArrayList<String>> questions) {
        this.response = response;
        this.questions = questions;
    }

    public QuestionResponse() {
        this.questions = new ArrayList<>();
    }
}
