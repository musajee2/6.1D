package com.example.a61d;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class QuizFetcher {

    public static List<Question> getQuestions(String topic) {
        List<Question> questions = new ArrayList<>();

        try {
            // Create URL
            URL url = new URL("http://172.20.10.2:5000/getQuiz?topic=" + topic);

            // Create connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            //Toast.makeText(QuizFetcher.this, "No questions available", Toast.LENGTH_SHORT).show();
            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse JSON response
            JSONArray jsonArray = new JSONArray(response.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                String questionText = jsonObj.getString("question");
                String correctAnswer = jsonObj.getString("correct_answer");
                List<String> options = new ArrayList<>();
                options.add(jsonObj.getString("option1"));
                options.add(jsonObj.getString("option2"));
                options.add(jsonObj.getString("option3"));
                options.add(jsonObj.getString("option4"));
                questions.add(new Question(questionText, correctAnswer, String.valueOf(options)));
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            // Handle error
        }

        return questions;
    }
}

