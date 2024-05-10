package com.example.a61d;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {
    private TextView questionTextView;
    private RadioGroup optionsRadioGroup;
    private Button submitButton;
    private TextView progressTextView;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Intent intent = getIntent();
        String interest = intent.getStringExtra("interest");

        questionTextView = findViewById(R.id.questionTextView);
        optionsRadioGroup = findViewById(R.id.optionsRadioGroup);
        submitButton = findViewById(R.id.submitButton);
        progressTextView = findViewById(R.id.progressTextView);

        // Fetch questions asynchronously
        new FetchQuestionsTask().execute(interest);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });
    }

    private void displayQuestion(int index) {
        int progress = currentQuestionIndex + 1;
        progressTextView.setText("Progress: " + progress + "/10");

        Question currentQuestion = questions.get(index);
        questionTextView.setText(currentQuestion.getQuestion());
        optionsRadioGroup.clearCheck();
        optionsRadioGroup.removeAllViews();

        List<String> options = currentQuestion.getOptions();
        Collections.shuffle(options);

        for (String option : options) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(option);
            optionsRadioGroup.addView(radioButton);
        }
    }

    private void checkAnswer() {
        int selectedOptionId = optionsRadioGroup.getCheckedRadioButtonId();
        if (selectedOptionId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedOptionId);
            if (selectedRadioButton != null) {
                String selectedAnswer = selectedRadioButton.getText().toString();
                if (selectedAnswer.equals(questions.get(currentQuestionIndex).getCorrectAnswer())) {
                    correctAnswers++;
                }
                currentQuestionIndex++;
                if (currentQuestionIndex < questions.size()) {
                    displayQuestion(currentQuestionIndex);
                } else {
                    showFinalScore();
                }
            } else {
                Toast.makeText(this, "Error: Selected radio button is null", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
        }
    }

    private void showFinalScore() {
        Intent intent = new Intent(QuizActivity.this, FinalScoreActivity.class);
        intent.putExtra("final_score", correctAnswers);
        startActivity(intent);
        finish();
    }

    private class FetchQuestionsTask extends AsyncTask<String, Void, List<Question>> {
        @Override
        protected List<Question> doInBackground(String... params) {
            String interest = params[0];
            return QuizFetcher.getQuestions(interest);
        }

        @Override
        protected void onPostExecute(List<Question> result) {
            if (result != null && !result.isEmpty()) {
                questions = result;
                Collections.shuffle(questions);
                displayQuestion(currentQuestionIndex);
            } else {
                Toast.makeText(QuizActivity.this, "No questions available", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
