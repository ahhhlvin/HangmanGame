package linkedin.app.hangmangame;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.support.design.widget.Snackbar;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements HangmanInterface.View {

    private FrameLayout mainLayout;
    private LinearLayout linearLayout;
    private View coordinatorLayoutView;
    private AppCompatSpinner difficultySpinner;
    private TextView triesCounterTextView;
    private TextView guessWordTextView;
    private TextInputEditText guessEditText;
    private FrameLayout progressBar;
    private Button submitButton;
    private TextView incorrectGuessesTextView;
    private android.support.design.widget.FloatingActionButton newWordButton;

    private ImageView headImage;
    private ImageView leftArmImage;
    private ImageView bodyImage;
    private ImageView rightArmImage;
    private ImageView leftLegImage;
    private ImageView rightLegImage;

    private MainPresenter presenter;

    WordsAsyncTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainPresenter(this, getApplicationContext());
        setupViews();
        task = new WordsAsyncTask();
        task.setWordsTaskListener(presenter);
    }

    /**
     * Initializes the variables as well as instantiating the views and any of their properties.
     */
    public void setupViews() {
        mainLayout = (FrameLayout) findViewById(R.id.activity_main);
        linearLayout = (LinearLayout) findViewById(R.id.mainLinearLayout);
        coordinatorLayoutView = (CoordinatorLayout) findViewById(R.id.snackbarPosition);
        difficultySpinner = (AppCompatSpinner) findViewById(R.id.difficultySpinner);

        triesCounterTextView = (TextView) findViewById(R.id.triesCounterTV);
        guessWordTextView = (TextView) findViewById(R.id.guessWordTV);
        guessEditText = (TextInputEditText) findViewById(R.id.guessET);
        progressBar = (FrameLayout) findViewById(R.id.progressBar);
        newWordButton = (android.support.design.widget.FloatingActionButton) findViewById(R.id.newWordButton);
        submitButton = (Button) findViewById(R.id.submitButton);
        headImage = (ImageView) findViewById(R.id.head);
        leftArmImage = (ImageView) findViewById(R.id.leftArm);
        bodyImage = (ImageView) findViewById(R.id.body);
        rightArmImage = (ImageView) findViewById(R.id.rightArm);
        leftLegImage = (ImageView) findViewById(R.id.leftLeg);
        rightLegImage = (ImageView) findViewById(R.id.rightLeg);
        incorrectGuessesTextView = (TextView) findViewById(R.id.incorrectGuessesTV);

        spinnerSetup();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String guessSubmission = guessEditText.getText().toString();
                if (!guessSubmission.isEmpty()) {
                    presenter.checkSubmission(guessSubmission.toLowerCase());
                    guessEditText.setText("");
                } else {
                    hideKeyboard();
                    Snackbar.make(coordinatorLayoutView, R.string.bad_submission_snackmsg, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        showProgressBar();

        Animation fabAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_anim);
        final Animation fabClick = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_click);
        newWordButton.setAnimation(fabAnim);
        newWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(fabClick);
                view.setClickable(false);
                presenter.setupNewRound();
                submitButton.setAlpha(1);
                submitButton.setEnabled(true);
                guessEditText.setEnabled(true);
                guessEditText.setVisibility(View.VISIBLE);
                headImage.setVisibility(View.INVISIBLE);
                leftArmImage.setVisibility(View.INVISIBLE);
                bodyImage.setVisibility(View.INVISIBLE);
                rightArmImage.setVisibility(View.INVISIBLE);
                leftLegImage.setVisibility(View.INVISIBLE);
                rightLegImage.setVisibility(View.INVISIBLE);
                view.setClickable(true);
                Snackbar.make(coordinatorLayoutView, R.string.new_word_snackmsg, Snackbar.LENGTH_LONG).show();
            }
        });


    }

    /**
     * Setup of Adapter for spinner options
     */
    public void spinnerSetup() {
        final List<String> difficultyLevels = new ArrayList<>();
        final ArrayAdapter<String> arrAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, difficultyLevels);
        presenter.spinnerClickSetup(difficultyLevels);
        difficultySpinner.setAdapter(arrAdapter);
        difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                showProgressBar();
                presenter.changeLevelDifficulty(adapterView.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    // Callback methods /////////////////////////////////////////////////////////////////////////

    /**
     * Cancels AsyncTask if currently running and back button is pressed to prevent memory leak
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (task != null && !task.isCancelled()) {
            task.cancel(true);
        }
    }

    /**
     * Cancels AsyncTask if currently running to prevent memory leak
     */
    @Override
    protected void onPause() {
        super.onPause();

        if (this.isFinishing() && task != null && !task.isCancelled()) {
            task.cancel(true);
        }
    }

    // MainPresenter interface method implementations ////////////////////////////////////////////

    /**
     * Programmatically hides keyboard to reveal 'new word' button to start new round
     */
    @Override
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);

    }

    /**
     * Updates the number of guesses remaining the user has within the current round
     */
    @Override
    public void refreshTriesCount(String triesFormattedInput) {
        triesCounterTextView.setText(triesFormattedInput);
    }

    /**
     * Displays guess word with a "_" representing every character of that word and win/lose message
     */
    @Override
    public void updateGuessWordTextView(String guessWord) {
        guessWordTextView.setText(guessWord);
    }

    @Override
    public void displayWinLoseSnackbar(boolean userDidWin) {
        guessEditText.setEnabled(false);
        guessEditText.setHint("");
        submitButton.setAlpha(0.3f);
        submitButton.setEnabled(false);
        String message = "";
        if (userDidWin) {
            message = "Congratulations, you won!";
        } else {
            message = "Aww great attempt though, how about trying again?";
        }
        Snackbar.make(coordinatorLayoutView, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Refreshes with the latest incorrectly guessed characters
     */
    @Override
    public void updateIncorrectGuessesTextView(String incorrectGuesses) {
        incorrectGuessesTextView.setText(incorrectGuesses);
    }

    @Override
    public void showHead() {
        headImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLeftArm() {
        leftArmImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void showBody() {
        bodyImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void showRightArm() {
        rightArmImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLeftLeg() {
        leftLegImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void showRightLeg() {
        rightLegImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        linearLayout.setAlpha(0.3f);
        newWordButton.setAlpha(0.3f);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        linearLayout.setAlpha(1f);
        newWordButton.setEnabled(true);
        newWordButton.setAlpha(1f);
        submitButton.setEnabled(true);
        guessEditText.setEnabled(true);
    }

    @Override
    public void displayNewWordSnackbar() {
        Snackbar.make(coordinatorLayoutView, R.string.new_word_snackmsg, Snackbar.LENGTH_LONG).show();
    }
}
