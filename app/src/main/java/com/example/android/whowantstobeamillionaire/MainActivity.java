package com.example.android.whowantstobeamillionaire;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

class Question {
    String question;
    String[] options;
}

public class MainActivity extends AppCompatActivity {
    private String packageName = "com.example.android.whowantstobeamillionaire";
    private String defType = "string";
    private String WRONG_ANSWER = "Wrong answer!";
    private String CORRECT_ANSWER = "Correct answer!";
    private String SELECT_ANSWER = "You must select at least one answer!";

    private int MAX_QUESTIONS = 10;
    private int MAX_OPTIONS = 4;
    private int FINISH_ROUND = 11;

    private TextView questionTextView;
    private TextView playerNameTextView;
    private TextView scoreTextView;
    private EditText playerNameEditText;
    private LinearLayout formContainer;
    private LinearLayout container;
    private Button startButton;
    private Button restartButton;
    private Button shareButton;
    private CheckBox options[];

    String playerName;
    Question possibleQuestions[];

    int pastQuestions[];
    int currentQuestion = 1;
    int currentAnswer = 0;
    int score = 0;
    int currentRound = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        options = new CheckBox[5];
        pastQuestions = new int[MAX_QUESTIONS + 1];
        possibleQuestions = new Question[MAX_QUESTIONS + 1];

        scoreTextView = (TextView) findViewById(R.id.scoreTextView);
        playerNameTextView = (TextView) findViewById(R.id.playerNameTextView);
        playerNameEditText = (EditText) findViewById(R.id.playerNameEditText);
        formContainer = (LinearLayout) findViewById(R.id.formContainer);
        startButton = (Button) findViewById(R.id.startButton);
        restartButton = (Button) findViewById(R.id.restartButton);
        shareButton = (Button) findViewById(R.id.shareButton);
        container = (LinearLayout) findViewById(R.id.container);

        options[1] = (CheckBox) findViewById(R.id.option1);
        options[2] = (CheckBox) findViewById(R.id.option2);
        options[3] = (CheckBox) findViewById(R.id.option3);
        options[4] = (CheckBox) findViewById(R.id.option4);
        questionTextView = (TextView) findViewById(R.id.questionTextView);

        loadResources();
    }

    int kk=0;
    public void startGame(View view) {
        kk++;
        playerName = playerNameEditText.getText().toString();
        playerNameEditText.setVisibility(View.GONE);
        view.setVisibility(View.GONE);

        playerNameTextView.setText(playerName);
        playerNameTextView.setVisibility(View.VISIBLE);
        scoreTextView.setText("0/0");
        scoreTextView.setVisibility(View.VISIBLE);

        container.setVisibility(View.VISIBLE);

        options[1] = (CheckBox) findViewById(R.id.option1);
        options[2] = (CheckBox) findViewById(R.id.option2);
        options[3] = (CheckBox) findViewById(R.id.option3);
        options[4] = (CheckBox) findViewById(R.id.option4);

        loadQuestion();
    }

    private void loadResources() {
        //Loading questions & answers from strings.xml.
        //Before shuffling, the first answer is always the correct one.
        Resources res = getResources();
        for(int i=1; i<= MAX_QUESTIONS; i++) {
            possibleQuestions[i] = new Question();

            //Loading questions
            String questionResName="question" + i;
            int resId = res.getIdentifier(questionResName, defType, packageName);
            possibleQuestions[i].question = getString(resId);

            //Loading options
            possibleQuestions[i].options = new String[5];
            for(int j=1; j <= MAX_OPTIONS; j++) {
                String optionResName="option" + i + "" + j;
                resId = res.getIdentifier(optionResName, defType, packageName);
                possibleQuestions[i].options[j] = getString(resId);
            }
        }
    }

    void shuffleOptions() {
        //Randomizing the {1, 2, 3, 4} array
        int k;
        int[] randomizedArray = new int[5];
        Random rand = new Random();
        for(int i=1; i <= MAX_OPTIONS; i++) {
            k = 1 + rand.nextInt(MAX_OPTIONS);
            for (int j = 1; j < i; j++) {
                if (k == randomizedArray[j]) {
                    k = 1 + rand.nextInt(MAX_OPTIONS);
                    j = 0;
                }
            }
            randomizedArray[i] = k;
        }
        for(int i = 1; i <= MAX_OPTIONS; i++) {
            options[randomizedArray[i]].setText(possibleQuestions[currentQuestion].options[i]);
        }

        //The answers were shuffled, so the currentAnswer must have a new value now.
        currentAnswer = randomizedArray[1];
        Log.d("TEST", currentAnswer + "");
    }

    public void submitAnswer(View view) {
        //All questions have only one correct answer.
        //Verifying if is only one the right answer.
        int selectedAnswer = 0;
        for(int i = 1; i <= MAX_OPTIONS; i++) {
            if(options[i].isChecked()) {
                if(selectedAnswer != 0) {
                    Toast.makeText(MainActivity.this, WRONG_ANSWER,  Toast.LENGTH_SHORT).show();
                    break;
                }
                selectedAnswer = i;
            }
        }
        if (selectedAnswer == 0) {
            Toast.makeText(MainActivity.this, SELECT_ANSWER,  Toast.LENGTH_SHORT).show();
            return;
        } else if (selectedAnswer == currentAnswer) {
            score++;
            Toast.makeText(MainActivity.this, CORRECT_ANSWER,  Toast.LENGTH_SHORT).show();
        }  else {
            Toast.makeText(MainActivity.this, WRONG_ANSWER,  Toast.LENGTH_SHORT).show();
        }
        scoreTextView.setText(score + "/" + currentRound);
        loadQuestion();
    }

    public void loadQuestion() {
        //Unchecking checkboxes which were checked.
        for(int i = 1; i <= MAX_OPTIONS; i++) {
            options[i].setChecked(false);
        }

        //Increasing round.
        currentRound++;
        if (currentRound == FINISH_ROUND) {
            Toast.makeText(MainActivity.this, score + "/" + (FINISH_ROUND - 1) + ". Not bad!",  Toast.LENGTH_SHORT).show();
            restartButton.setVisibility(View.VISIBLE);
            shareButton.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);
            //Show ending screen
            return ;
        }

        //Loading a RANDOM question.
        Random rand = new Random();
        int k = 1 + rand.nextInt(MAX_QUESTIONS);
        for (int j = 1; j < currentRound; j++) {
            if (k == pastQuestions[j]) {
                k = 1 + rand.nextInt(MAX_QUESTIONS);
                j = 0;
            }
        }
        currentQuestion = k;
        pastQuestions[currentRound] = currentQuestion;

        //Displaying.
        questionTextView.setText(possibleQuestions[currentQuestion].question);
        shuffleOptions();
    }

    public void shareScore(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "I've just scored " + score + "/" + (FINISH_ROUND - 1) + " on \"Who wants to be a Millionaire\" application.");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void restartGame(View view) {
        finish();
        startActivity(getIntent());
    }
}
