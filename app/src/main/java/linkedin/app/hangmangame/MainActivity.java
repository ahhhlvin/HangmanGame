package linkedin.app.hangmangame;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.support.design.widget.Snackbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainPresenter.UI {

    private RelativeLayout mainLayout;
    private LinearLayout linearLayout;
    private View coordinatorLayoutView;
    private AppCompatSpinner difficultySpinner;
    private TextView triesCounterTextView;
    private TextView guessWordTextView;
    private TextInputEditText guessEditText;
    private ProgressBar progressBar;
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

        presenter = new MainPresenter();
        presenter.setView(this, getApplicationContext());
        setupViews();
        task = new WordsAsyncTask();
        task.setWordsTaskListener(presenter);
    }

    /**
     * Initializes the variables as well as instantiating the views and any of their properties.
     */
    public void setupViews() {
        presenter.setup();
        mainLayout = (RelativeLayout) findViewById(R.id.activity_main);
        linearLayout = (LinearLayout) findViewById(R.id.mainLinearLayout);
        coordinatorLayoutView = (CoordinatorLayout) findViewById(R.id.snackbarPosition);
        difficultySpinner = (AppCompatSpinner) findViewById(R.id.difficultySpinner);
        spinnerSetup();
        triesCounterTextView = (TextView) findViewById(R.id.triesCounterTV);
        guessWordTextView = (TextView) findViewById(R.id.guessWordTV);
        guessEditText = (TextInputEditText) findViewById(R.id.guessET);
        guessEditText.setEnabled(false);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setEnabled(false);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String guessSubmission = guessEditText.getText().toString();
                if (!guessSubmission.isEmpty()) {
                    presenter.checkSubmission(guessSubmission.toLowerCase());
                    guessEditText.setText("");
                } else {
                    hideKeyboard();
                    Snackbar
                            .make(coordinatorLayoutView, R.string.bad_submission_snackmsg, Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        incorrectGuessesTextView = (TextView) findViewById(R.id.incorrectGuessesTV);

        newWordButton = (android.support.design.widget.FloatingActionButton) findViewById(R.id.newWordButton);
        newWordButton.setEnabled(false);
        newWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.newGame();
                if (guessEditText.getVisibility() == View.INVISIBLE && !submitButton.isEnabled()) {
                    guessEditText.setVisibility(View.VISIBLE);
                    submitButton.setEnabled(true);
                }
                headImage.setVisibility(View.VISIBLE);
                leftArmImage.setVisibility(View.VISIBLE);
                bodyImage.setVisibility(View.VISIBLE);
                rightArmImage.setVisibility(View.VISIBLE);
                leftLegImage.setVisibility(View.VISIBLE);
                rightLegImage.setVisibility(View.VISIBLE);
                Snackbar
                        .make(coordinatorLayoutView, R.string.new_word_snackmsg, Snackbar.LENGTH_LONG)
                        .show();
            }
        });

        headImage = (ImageView) findViewById(R.id.head);
        leftArmImage = (ImageView) findViewById(R.id.leftArm);
        bodyImage = (ImageView) findViewById(R.id.body);
        rightArmImage = (ImageView) findViewById(R.id.rightArm);
        leftLegImage = (ImageView) findViewById(R.id.leftLeg);
        rightLegImage = (ImageView) findViewById(R.id.rightLeg);
    }

    /**
     * Setup of Adapter for spinner options
     */
    public void spinnerSetup() {
        final ArrayList<String> difficultyLevels = new ArrayList<>();
        final ArrayAdapter<String> arrAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, difficultyLevels);
        presenter.spinnerClickSetup(difficultyLevels);
        difficultySpinner.setAdapter(arrAdapter);
        difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                progressBar.setVisibility(View.VISIBLE);
                linearLayout.setAlpha((float)0.3);
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
     * Enables the text field and buttons after network call to API is completed, and disables
     * progress bar
     */
    @Override
    public void setGameReadyUI() {
        progressBar.setVisibility(View.INVISIBLE);
        linearLayout.setAlpha(1);
        guessEditText.setEnabled(true);
        submitButton.setEnabled(true);
        newWordButton.setEnabled(true);
    }

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
        guessEditText.setVisibility(View.INVISIBLE);
        submitButton.setEnabled(false);
        String message = "";
        if (userDidWin) {
            message = "Congratulations, you won!";
        } else {
            message = "Aww great attempt though, how about trying again?";
        }
        Snackbar
                .make(coordinatorLayoutView, message, Snackbar.LENGTH_LONG)
                .show();
    }

    /**
     * Refreshes with the latest incorrectly guessed characters
     */
    @Override
    public void updateIncorrectGuessesTextView(String incorrectGuesses) {
        incorrectGuessesTextView.setText(incorrectGuesses);
    }

    @Override
    public void hideHead() {
        headImage.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideLeftArm() {
        leftArmImage.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideBody() {
        bodyImage.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideRightArm() {
        rightArmImage.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideLeftLeg() {
        leftLegImage.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideRightLeg() {
        rightLegImage.setVisibility(View.INVISIBLE);
    }
}
