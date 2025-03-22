package com.example.home_study;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.home_study.Model.ExamCenter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExamCenterActivity extends AppCompatActivity {

    private TextView question, explanation, questionCounter, title;
    private Button nextBtn, previousBtn;
    private ImageView backBtn;
    private RadioGroup chooses;
    private int currentQuestionIndex = 0;
    private List<ExamCenter> questionList;
    private DatabaseReference questionRef;
    private String selectedSubject, selectedChapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_center);

        nextBtn = findViewById(R.id.nextBtn);
        previousBtn = findViewById(R.id.previousBtn);
        backBtn = findViewById(R.id.backBtn);
        questionCounter = findViewById(R.id.questionCounter);
        title = findViewById(R.id.title);
        question = findViewById(R.id.question);
        explanation = findViewById(R.id.explanation);

        chooses = findViewById(R.id.questionChoose);

        selectedSubject = getIntent().getStringExtra("subject");
        selectedChapter = getIntent().getStringExtra("chapter");

        questionList = new ArrayList<>();

        title.setText(selectedChapter);
        questionRef = FirebaseDatabase.getInstance().getReference().child("Exams")
                .child(selectedSubject).child(selectedChapter);

        fetchQuestions();

        backBtn.setOnClickListener(v ->{
            finish();
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestionIndex < questionList.size() - 1){
                    currentQuestionIndex++;
                    displayQuestion();
                    explanation.setText("Explanation");
                }
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestionIndex > 0){
                    currentQuestionIndex--;
                    displayQuestion();
                    explanation.setText("Explanation");
                }
            }
        });
    }

    private void displayQuestion() {

        if (questionList.isEmpty()) return;

        ExamCenter questions = questionList.get(currentQuestionIndex);
        question.setText((currentQuestionIndex + 1) + ". " +questions.getQuestion());
        questionCounter.setText("Question" + " " + (currentQuestionIndex + 1) + " of " + questionList.size());

        chooses.removeAllViews();
        for (String option : questions.getOptions()){
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(option);
            chooses.addView(radioButton);
        }

        chooses.setOnCheckedChangeListener((group, checkedId) ->{
            RadioButton selectedRadioButton = group.findViewById(checkedId);

            if (selectedRadioButton != null){
                String selectedAnswer = selectedRadioButton.getText().toString();

                if (selectedAnswer.equals(questions.getCorrectAnswer()))
                {
                    explanation.setText("✅ Correct! " + questions.getExplanation());
                }else {
                    explanation.setText("❌ Incorrect! " + questions.getExplanation());
                }
            }
        });
    }

    private void fetchQuestions() {

        questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                questionList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ExamCenter question = dataSnapshot.getValue(ExamCenter.class);
                    questionList.add(question);
                }

                if (!questionList.isEmpty()){
                    displayQuestion();
                }else {
                    question.setText("No question available");
                    chooses.removeAllViews();
                    explanation.setText("");
                    questionCounter.setText("Question" + 0 + " of " + 0);
                    previousBtn.setEnabled(false);
                    nextBtn.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExamCenterActivity.this, "Failed to loaded questions", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
