package com.example.a61d;


import java.util.List;
import java.util.ArrayList;

public class Question {
    private String question;
    private String correctAnswer;
    private List<String> options;

    public Question(String question, String correctAnswer, String... options) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.options = new ArrayList<>();
        for (String option : options) {
            this.options.add(option);
        }
    }

    public String getQuestion() {
        return question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public List<String> getOptions() {
        return options;
    }
}
