package pl.wroc.uni.ift.android.quizactivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class QuizActivity extends AppCompatActivity {


    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPreviouButton;

    private TextView mQuestionTextView;

    private Question[] mQuestionsBank = new Question[]{
            new Question(R.string.question_stolica_polski, true),
            new Question(R.string.question_stolica_dolnego_slaska, false),
            new Question(R.string.question_sniezka, true),
            new Question(R.string.question_wisla, true)
    };

    private boolean isClicked[] = new boolean[]{
        true, true, true, true
    };

    private int mCurrentIndex = 0;
    private int mScore = 0;

    //    Bundles are generally used for passing data between various Android activities.
    //    It depends on you what type of values you want to pass, but bundles can hold all
    //    types of values and pass them to the new activity.
    //    see: https://stackoverflow.com/questions/4999991/what-is-a-bundle-in-an-android-application

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        // inflating view objects
        setContentView(R.layout.activity_quiz);

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex++;
                if (mCurrentIndex == mQuestionsBank.length){
                    mCurrentIndex = 0;
                }
                updateQuestion();
            }
        });

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkAnswer(true);
                        mTrueButton.setEnabled(isClicked[mCurrentIndex]);
                        isClicked();
                    }
                }
        );

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
                mFalseButton.setEnabled(isClicked[mCurrentIndex]);
                isClicked();
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(!isClicked[mCurrentIndex-1]){
//                    mNextButton.setEnabled(false);
//                }else{
//                    mNextButton.setEnabled(true);
//                }

                mCurrentIndex++;
                if (mCurrentIndex == mQuestionsBank.length){
                    mCurrentIndex = mQuestionsBank.length-1;
                    summaryAlert();
                    disableNavigationButton();

                }

                updateQuestion();
                mTrueButton.setEnabled(isClicked[mCurrentIndex]);
                mFalseButton.setEnabled(isClicked[mCurrentIndex]);
            }
        });

        mPreviouButton = (ImageButton) findViewById(R.id.previous_button);
        mPreviouButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mCurrentIndex--;
                if (mCurrentIndex == -1) {
                    mCurrentIndex = 0;
                    canNotBackAlert();
                }
                updateQuestion();
                mTrueButton.setEnabled(isClicked[mCurrentIndex]);
                mFalseButton.setEnabled(isClicked[mCurrentIndex]);

            }
        });
        updateQuestion();
    }

    private void updateQuestion() {
        int question = mQuestionsBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionsBank[mCurrentIndex].isAnswerTrue();

        int toastMessageId = 0;

        if (userPressedTrue == answerIsTrue) {
            toastMessageId = R.string.correct_toast;
            mScore++;
        } else {
            toastMessageId = R.string.incorrect_toast;
        }

        Toast toast = Toast.makeText(this, toastMessageId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }


    private void isClicked(){
        isClicked[mCurrentIndex] = false;
        mTrueButton.setEnabled(isClicked[mCurrentIndex]);
        mFalseButton.setEnabled(isClicked[mCurrentIndex]);

    }

    private void disableNavigationButton(){
        mNextButton.setEnabled(false);
        mPreviouButton.setEnabled(false);
    }

    private void canNotBackAlert(){
        AlertDialog alert = new AlertDialog.Builder(QuizActivity.this).create();
        alert.setTitle("Uwaga");
        alert.setMessage("Nie można się cofnąć!");
        alert.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
            }
        });
        alert.show();
    }

    private void summaryAlert(){
        AlertDialog alert = new AlertDialog.Builder(QuizActivity.this).create();
        alert.setTitle("Koniec pytań");
        alert.setMessage("Odpowiedziałeś poprawnie na: " + mScore + '/' + mQuestionsBank.length);
        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Zagraj ponownie", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                QuizActivity.super.recreate();
                Toast.makeText(getApplicationContext(), "Zagraj ponownie", Toast.LENGTH_SHORT).show();

            }
        });
        alert.setButton(AlertDialog.BUTTON_POSITIVE, "Wyjdź", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                QuizActivity.this.finish();
                Toast.makeText(getApplicationContext(), "Finish", Toast.LENGTH_SHORT).show();

            }
        });
        alert.show();
    }
}
