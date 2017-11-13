package pl.wroc.uni.ift.android.quizactivity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CheatActivity extends AppCompatActivity {

    private final static String EXTRA_KEY_ANSWER = "Answer";
    private final static String KEY_QUESTION = "QUESTION";
    private final static String EXTRA_KEY_SHOWN = "Shown";
    TextView mTextViewAnswer;
    TextView mTextViewToken;
    Button mButtonShow;
    private int mCheatCounter = 0;

    boolean mAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mAnswer = getIntent().getBooleanExtra(EXTRA_KEY_ANSWER,false);

        mTextViewAnswer = (TextView) findViewById(R.id.text_view_answer);
        mButtonShow = (Button) findViewById(R.id.button_show_answer);
        mTextViewToken = (TextView) findViewById(R.id.token_counter);
        mButtonShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCheatCounter < 3){
                    mCheatCounter++;
                    if (mAnswer) {
                        mTextViewAnswer.setText("Prawda");
                    } else {
                        mTextViewAnswer.setText("Fałsz");
                    }
                    setAnswerShown(true);
                    }else{
                    Toast.makeText(getApplicationContext(), "Nie ma już więcej podpowiedzi",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
//        mTextViewToken.append(mCheatCounter.toString);
        mTextViewToken.setText(mCheatCounter);
        setAnswerShown(false);
    }


    public static boolean wasAnswerShown(Intent data)
    {
        return data.getBooleanExtra(EXTRA_KEY_SHOWN, false);
    }

    public static Intent newIntent(Context context, boolean answerIsTrue)
    {

        Intent intent = new Intent(context, CheatActivity.class);
        intent.putExtra(EXTRA_KEY_ANSWER, answerIsTrue);
        return intent;

    }

    private void setAnswerShown (boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra("wasShown", isAnswerShown);
        setResult(RESULT_OK, data);
    }





}
