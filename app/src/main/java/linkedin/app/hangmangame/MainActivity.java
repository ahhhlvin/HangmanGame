package ahhhlvin.c4q.nyc.hangmangame;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    char[] guess;
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
        System.out.println(gameWords.size());
    }

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
            newGame();
        }
    }

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
                    checkSubmission(guessSubmission);
                    guessEditText.setText("");
                }
            }
        });

        incorrectGuessesTextView = (TextView) findViewById(R.id.incorrectGuessesTV);

        newWordButton = (Button) findViewById(R.id.newWordButton);
        newWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newGame();
            }
        });
    }

    public void newGame() {
        if (gameWords.size() > 0) {
            int wordIndex = randomGenerator.nextInt(gameWords.size());
            currWord = gameWords.get(wordIndex);
            remainingGuesses = 6;
            refreshTriesCount();

            // TODO DELETE: FOR TESTING !!
            System.out.println("current word: " + currWord);

            for (int i = 0; i < currWord.length(); i++) {
                wordSet.add(String.valueOf(currWord.charAt(i)));
            }

            guess = new char[currWord.length()];

            for (int i = 0; i < currWord.length(); i++) {
                guess[i] = '_';
            }

            guessWordTextView.setText(Arrays.toString(guess).replaceAll("\\[|\\]", "").replaceAll(",", " "));
            incorrectChars = "";
            incorrectGuessesTextView.setText(incorrectChars);
        }
    }

    public void checkSubmission(String input) {

        if (remainingGuesses > 0) {
            if (!wordSet.contains(input)) {
                incorrectChars += input + " ";
                incorrectGuessesTextView.setText(incorrectChars);
                remainingGuesses--;
                refreshTriesCount();

                if (remainingGuesses == 0) {
                    Toast.makeText(getApplicationContext(), "GAME OVER :(", Toast.LENGTH_SHORT).show();
                    newGame();
                }

            } else {

                for (int i = 0; i < currWord.length(); i++) {
                    if (input.equals(String.valueOf(currWord.charAt(i)))) {
                        guess[i] = currWord.charAt(i);
                        correctGuessSet.add(String.valueOf(currWord.charAt(i)));
                    }
                }

                guessWordTextView.setText(Arrays.toString(guess).replaceAll("\\[|\\]", "").replaceAll(",", " "));
            }
        }


        if (!correctGuessSet.contains("_")) {
            String result = "";

            for (Character c : guess) {
                result += c;
            }

            if (result.equals(currWord)) {
                Toast.makeText(getApplicationContext(), "YOU WON! :)", Toast.LENGTH_SHORT).show();
                newGame();
            }
        }

    }

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
            System.out.println(words.length);
            gameWords = Arrays.asList(words);

            responses.body().close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ("SUCCESS");

    }

    public void refreshTriesCount() {
        String remainingGuessText = String.format(Locale.US, "%1$s %2$d", getString(R.string.guess_remaining_text), remainingGuesses);
        triesCounterTextView.setText(remainingGuessText);
    }
}
