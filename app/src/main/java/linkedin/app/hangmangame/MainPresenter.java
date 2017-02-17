package linkedin.app.hangmangame;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

/**
 * Created by alvin2 on 2/17/17.
 */

class MainPresenter {

    private MainPresenter.UI view;
    private Context context;

    private Random randomGenerator;
    private int remainingGuesses = 6;
    private String currWord;
    private char[] guessWordArr;
    private HashSet<String> wordSet;
    private HashSet<String> correctGuessSet;
    private String incorrectChars = "";
    private List<String> gameWords;

    private static final String API_URL = "http://linkedin-reach.hagbpyjegb.us-west-2.elasticbeanstalk.com/words";

    void setView(MainPresenter.UI view, Context context) {
        this.view = view;
        this.context = context;
    }

    void setup() {
        randomGenerator = new Random();
        gameWords = new ArrayList<>();

        wordSet = new HashSet<>();
        correctGuessSet = new HashSet<>();
    }


    /**
     * Creates a new round by retrieving another word from the list of possible word
     * choices and resets necessary values back to their initial values.
     */
    void newGame() {
        if (gameWords.size() > 0) {
            int wordIndex = randomGenerator.nextInt(gameWords.size());
            currWord = gameWords.get(wordIndex);
            remainingGuesses = 6;
            view.refreshTriesCount(formatTriesString(remainingGuesses));
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

            view.updateGuessWordTextView(formatGuessWord(guessWordArr));
            incorrectChars = "";
            view.updateIncorrectGuessesTextView(incorrectChars);
        }
    }

    /**
     * Includes the logic for checking whether the submitted character is one that
     * exists within the unknown word or not.
     *
     * @param input the single letter character the user entered in the text field
     */
    void checkSubmission(String input) {

        // prevents submission checking if same incorrect character is guessed multiple times
        if (!incorrectChars.contains(input)) {
            if (remainingGuesses > 0) {
                // case where current word does not contain guessed character, subtract try and add to incorrect guesses
                if (!wordSet.contains(input)) {
                    incorrectChars += input + " ";
                    view.updateIncorrectGuessesTextView(incorrectChars);
                    remainingGuesses--;
                    view.refreshTriesCount(formatTriesString(remainingGuesses));
                    // if there are no guesses remaining then the game has ended and the user has lost
                    if (remainingGuesses == 0) {
                        view.displayLoseToast();
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
                    view.updateGuessWordTextView(formatGuessWord(guessWordArr));
                }
            }

            // correctGuessSet is used to determine that all "_" have been properly guessed
            if (!correctGuessSet.contains("_")) {
                String result = "";

                for (Character c : guessWordArr) {
                    result += c;
                }

                if (result.equals(currWord)) {
                    view.displayWinToast();
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
    String fetchWords(String url) {
        try {
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
            return ("SUCCESS");

        } catch (IOException e) {
            e.printStackTrace();
            return ("EXCEPTION CAUGHT");
        }
    }

    /**
     * Correctly formats and concatenates text to display remaining guesses
     * @param remainingTries int representing the number of guesses the user has remaining
     * @return String concatenated and formatted text to display
     */
    private String formatTriesString(int remainingTries) {
        return String.format(Locale.US, "%1$s %2$d", context.getResources().getString(R.string.guess_remaining_text), remainingTries);
    }

    /**
     * Correctly formats the guess word text by removing array brackets and replacing commas with
     * spaces
     * @param correctGuesses char[] representing the correctly guessed characters
     * @return String formatted guess word to display
     */
    private String formatGuessWord(char[] correctGuesses) {
        return Arrays.toString(correctGuesses).replaceAll("\\[|\\]", "").replaceAll(",", " ");
    }

    /**
     * Creates a new AsyncTask to retrieve the guess words from network call to API
     */
    void runWordsTask() {
        new WordsTask().execute();
    }

    /**
     * On a separate thread, will fetch the txt containing all possible hangman words,
     * and display a progress bar and disable the submit button and text field until network
     * call has completed.
     */
    private class WordsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            return fetchWords(API_URL);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equalsIgnoreCase("EXCEPTION CAUGHT")) {
                Log.e("NETWORK CALL STATUS", "Incorrect URL");
            } else {
                newGame();
                view.setGameReadyUI();
            }
        }
    }

    /**
     * Interface used to communicate between the Presenter and View to update UI
     */
    interface UI {
        void refreshTriesCount(String triesFormattedInput);

        void updateGuessWordTextView(String guessWord);

        void updateIncorrectGuessesTextView(String incorrectGuesses);

        void setGameReadyUI();

        void displayLoseToast();

        void displayWinToast();
    }
}
