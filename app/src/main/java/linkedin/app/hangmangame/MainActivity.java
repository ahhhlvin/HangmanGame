package linkedin.app.hangmangame;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView triesCounterTextView;
    private TextView guessWordTextView;
    private EditText guessEditText;
    private ProgressBar progressBar;
    private Button submitButton;
    private TextView incorrectGuessesTextView;
    private Button newWordButton;

    Random randomGenerator;
    int remainingGuesses = 6;
    String currWord;
    char[] guessWordArr;
    HashSet<String> wordSet;
    HashSet<String> correctGuessSet;
    String incorrectChars = "";
    List<String> gameWords;

    public static final String API_URL = "http://linkedin-reach.hagbpyjegb.us-west-2.elasticbeanstalk.com/words";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setup();
        new WordsTask().execute();
    }

    /**
     * On a separate thread, will fetch the txt containing all possible hangman words,
     * and display a progress bar and disable the submit button and text field until network
     * call has completed.
     */
    class WordsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
           return fetchWords(API_URL);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            guessEditText.setEnabled(true);
            submitButton.setEnabled(true);
            newWordButton.setEnabled(true);
            newGame();
        }
    }

    /**
     * Initializes the variables as well as instantiating the views and any of their properties.
     */
    public void setup() {
        randomGenerator = new Random();
        gameWords = new ArrayList<>();

        wordSet = new HashSet<>();
        correctGuessSet = new HashSet<>();

        triesCounterTextView = (TextView) findViewById(R.id.triesCounterTV);
        refreshTriesCount();

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
                    checkSubmission(guessSubmission.toLowerCase());
                    guessEditText.setText("");
                }
            }
        });

        incorrectGuessesTextView = (TextView) findViewById(R.id.incorrectGuessesTV);

        newWordButton = (Button) findViewById(R.id.newWordButton);
        newWordButton.setEnabled(false);
        newWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newGame();
            }
        });
    }

    /**
     * Creates a new round by retrieving another word from the list of possible word
     * choices and resets necessary values back to their initial values.
     */
    public void newGame() {
        if (gameWords.size() > 0) {
            int wordIndex = randomGenerator.nextInt(gameWords.size());
            currWord = gameWords.get(wordIndex);
            remainingGuesses = 6;
            refreshTriesCount();
            correctGuessSet.clear();
            wordSet.clear();

            Log.i("GUESS WORD", currWord);

            for (int i = 0; i < currWord.length(); i++) {
                wordSet.add(String.valueOf(currWord.charAt(i)));
            }

            guessWordArr = new char[currWord.length()];

            for (int i = 0; i < currWord.length(); i++) {
                guessWordArr[i] = '_';
            }

            updateGuessWordTextView();
            incorrectChars = "";
            updateIncorrectGuessesTextView();
        }
    }

    /**
     * Includes the logic for checking whether the submitted character is one that
     * exists within the unknown word or not.
     *
     * @param input the single letter character the user entered in the text field
     */
    public void checkSubmission(String input) {

        // prevents submission checking if same incorrect character is guessed multiple times
        if (!incorrectChars.contains(input)) {
            if (remainingGuesses > 0) {
                // case where current word does not contain guessed character, subtract try and add to incorrect guesses
                if (!wordSet.contains(input)) {
                    incorrectChars += input + " ";
                    updateIncorrectGuessesTextView();
                    remainingGuesses--;
                    refreshTriesCount();
                    // if there are no guesses remaining then the game has ended and the user has lost
                    if (remainingGuesses == 0) {
                        Toast.makeText(getApplicationContext(), "GAME OVER :(", Toast.LENGTH_SHORT).show();
                        newGame();
                    }
                } else {
                    // case where the guess word does contain the guessed character, replace the "_" with character
                    for (int i = 0; i < currWord.length(); i++) {
                        if (input.equals(String.valueOf(currWord.charAt(i)))) {
                            guessWordArr[i] = currWord.charAt(i);
                            correctGuessSet.add(String.valueOf(currWord.charAt(i)));
                        }
                    }
                    updateGuessWordTextView();
                }
            }

            // correctGuessSet is used to determine that all "_" have been properly guessed
            if (!correctGuessSet.contains("_")) {
                String result = "";

                for (Character c : guessWordArr) {
                    result += c;
                }

                if (result.equals(currWord)) {
                    Toast.makeText(getApplicationContext(), "YOU WON! :)", Toast.LENGTH_SHORT).show();
                    newGame();
                }
            }
        }

    }

    /**
     * Makes the call to the API to retrieve the list of possible word choices and neatly returns
     * them all in a List of strings.
     *
     * @param url the string URL representing the word dictionary API that the network call will
     *            be made to
     * @return String represents the "SUCCESS" string that will be used to determine whether network
     * call was successful or not
     */
    public String fetchWords(String url) {
        try {

            progressBar.setVisibility(View.VISIBLE);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response responses;

            responses = client.newCall(request).execute();
            String jsonData = responses.body().string();

            String[] words = jsonData.split("\\r?\\n");
            gameWords = Arrays.asList(words);

            responses.body().close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ("SUCCESS");

    }

    /**
     * Formats and updates the number of guesses remaining the user has within the current round
     */
    public void refreshTriesCount() {
        String remainingGuessText = String.format(Locale.US, "%1$s %2$d", getString(R.string.guess_remaining_text), remainingGuesses);
        triesCounterTextView.setText(remainingGuessText);
    }

    /**
     * Formats the displaying guess word with a "_" representing every character of that word
     */
    public void updateGuessWordTextView() {
        guessWordTextView.setText(Arrays.toString(guessWordArr).replaceAll("\\[|\\]", "").replaceAll(",", " "));
    }

    /**
     * Refreshes with the latest incorrectly guessed characters
     */
    public void updateIncorrectGuessesTextView() {
        incorrectGuessesTextView.setText(incorrectChars);
    }
}
