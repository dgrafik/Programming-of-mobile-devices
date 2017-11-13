package pl.wroc.uni.ift.android.quizactivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.os.Build;


public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";

    // Key for questions array to be stored in bundle;
    private static final String KEY_QUESTIONS = "questions";

    private static final int CHEAT_REQEST_CODE = 0;


    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPreviouButton;

    private Button mCheatButton;

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
        Log.d(TAG, "onCreate() called");

        setTitle(R.string.app_name);
        // inflating view objects
        setContentView(R.layout.activity_quiz);

        // check for saved data
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX);
            Log.i(TAG, String.format("onCreate(): Restoring saved index: %d", mCurrentIndex));

            // here in addition we are restoring our Question array;
            // getParcelableArray returns object of type Parcelable[]
            // since our Question is implementing this interface (Parcelable)
            // we are allowed to cast the Parcelable[] to desired type which
            // is the Question[] here.
            mQuestionsBank = (Question []) savedInstanceState.getParcelableArray(KEY_QUESTIONS);
            // sanity check
            if (mQuestionsBank == null)
            {
                Log.e(TAG, "Question bank array was not correctly returned from Bundle");

            } else {
                Log.i(TAG, "Question bank array was correctly returned from Bundle");
            }

            Toast toast = Toast.makeText(this, "Twoja werja API to: " +
                    Integer.valueOf(Build.VERSION.SDK_INT), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
            toast.show();

        }

        mCheatButton = (Button) findViewById(R.id.button_cheat);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    boolean currentAnswer = mQuestionsBank[mCurrentIndex].isAnswerTrue();
                    Intent intent = CheatActivity.newIntent(QuizActivity.this, currentAnswer);
//
//                Intent intent = new Intent(QuizActivity.this, CheatActivity.class);
//                boolean currentAnswer = mQuestionsBank[mCurrentIndex].isAnswerTrue();
//                intent.putExtra("answer", currentAnswer);

                    startActivityForResult(intent, CHEAT_REQEST_CODE);

            }
        });


        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mFalseButton.isEnabled()) {
                    mCurrentIndex++;
                    if (mCurrentIndex == mQuestionsBank.length) {
                        mCurrentIndex = mQuestionsBank.length - 1;
                        summaryAlert();
                        disableNavigationButton();

                    }

                    updateQuestion();
                }
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
                if(!mFalseButton.isEnabled()){
                    mNextButton.setEnabled(true);
                    mCurrentIndex++;
                    if (mCurrentIndex == mQuestionsBank.length){
                        mCurrentIndex = mQuestionsBank.length-1;
                        summaryAlert();
                        disableNavigationButton();

                    }

                    updateQuestion();
                    mTrueButton.setEnabled(isClicked[mCurrentIndex]);
                    mFalseButton.setEnabled(isClicked[mCurrentIndex]);
                }else{
                    mNextButton.setEnabled(false);
                }

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
                mNextButton.setEnabled(true);

            }
        });
        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == CHEAT_REQEST_CODE) {
            if (data != null)
            {
                boolean answerWasShown = CheatActivity.wasAnswerShown(data);
                if (answerWasShown) {

                    Toast.makeText(this,
                            R.string.message_for_cheaters,
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, String.format("onSaveInstanceState: current index %d ", mCurrentIndex) );

        //we still have to store current index to correctly reconstruct state of our app
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);

        // because Question is implementing Parcelable interface
        // we are able to store array in Bundle
        savedInstanceState.putParcelableArray(KEY_QUESTIONS, mQuestionsBank);
    }

//    public void sendHint(View view) {
//        if (mCheatCounter < 3) {
//            Intent intent = new Intent(this, CheatActivity.class);
//            boolean answer = mQuestionsBank[mCurrentIndex].isAnswerTrue();
//            intent.putExtra(ANSWER, answer);
//            startActivity(intent);
//            mCheatCounter++;
//        }
//        else
//            Toast.makeText(getApplicationContext(), "Koniec Podpowiedzi", Toast.LENGTH_SHORT).show();
//    }

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
        mNextButton.setEnabled(true);

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
