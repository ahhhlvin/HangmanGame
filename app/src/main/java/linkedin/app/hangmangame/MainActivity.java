package linkedin.app.hangmangame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements MainPresenter.UI {

    private TextView triesCounterTextView;
    private TextView guessWordTextView;
    private EditText guessEditText;
    private ProgressBar progressBar;
    private Button submitButton;
    private TextView incorrectGuessesTextView;
    private Button newWordButton;

    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainPresenter();
        presenter.setView(this, getApplicationContext());
        setupViews();
        presenter.runWordsTask();
    }

    /**
     * Initializes the variables as well as instantiating the views and any of their properties.
     */
    public void setupViews() {
        presenter.setup();
        triesCounterTextView = (TextView) findViewById(R.id.triesCounterTV);
        guessWordTextView = (TextView) findViewById(R.id.guessWordTV);
        guessEditText = (EditText) findViewById(R.id.guessET);
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
                }
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        incorrectGuessesTextView = (TextView) findViewById(R.id.incorrectGuessesTV);

        newWordButton = (Button) findViewById(R.id.newWordButton);
        newWordButton.setEnabled(false);
        newWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.newGame();
            }
        });
    }


    /**
     * Updates the number of guesses remaining the user has within the current round
     */
    public void refreshTriesCount(String triesFormattedInput) {
        triesCounterTextView.setText(triesFormattedInput);
    }

    /**
     * Displays guess word with a "_" representing every character of that word
     */
    public void updateGuessWordTextView(String guessWord) {
        guessWordTextView.setText(guessWord);
    }

    /**
     * Refreshes with the latest incorrectly guessed characters
     */
    public void updateIncorrectGuessesTextView(String incorrectGuesses) {
        incorrectGuessesTextView.setText(incorrectGuesses);
    }

    /**
     * Enables the text field and buttons after network call to API is completed, and disables
     * progress bar
     */
    @Override
    public void setGameReadyUI() {
        progressBar.setVisibility(View.GONE);
        guessEditText.setEnabled(true);
        submitButton.setEnabled(true);
        newWordButton.setEnabled(true);
    }

    /**
     * Displays a toast message that notifies the user the game has ended
     */
    @Override
    public void displayLoseToast() {
        Toast.makeText(getApplicationContext(), "GAME OVER :(", Toast.LENGTH_SHORT).show();
    }

    /**
     * Displays a toast message that notifies the user the game has been won
     */
    @Override
    public void displayWinToast() {
        Toast.makeText(getApplicationContext(), "YOU WON! :)", Toast.LENGTH_SHORT).show();
    }
}
