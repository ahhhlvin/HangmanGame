package linkedin.app.hangmangame;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

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
    private HangmanModel hangmanModel;
    private String currWord;
    private char[] guessWordArr;
    private String incorrectChars;
    private int remainingGuesses;
    private Context context;

    static final String API_URL = "http://linkedin-reach.hagbpyjegb.us-west-2.elasticbeanstalk.com/words";

    /**
     * Assigns the 'context' and 'view' property of the MainPresenter object
     * @param view MainPresenter.UI the class that implements the "UI" interface to communicate with business logic
     * @param context Context used to access string values in the resources folder
     */
    void setView(MainPresenter.UI view, Context context) {
        this.view = view;
        this.context = context;
    }

    /**
     * Initializes properties of the MainPresenter object
     */
    void setup() {
        hangmanModel = new HangmanModel();
    }

    /**
     * Creates a new round by retrieving another word from the list of possible word
     * choices and resets necessary values back to their initial values.
     */
    void newGame() {
        List<String> gameWords = hangmanModel.getGameWords();
        currWord = hangmanModel.getCurrWord();
        guessWordArr = hangmanModel.getGuessWordArr();
        remainingGuesses = hangmanModel.getRemainingGuesses();
        incorrectChars = hangmanModel.getIncorrectChars();


        if (hangmanModel.getGameWords().size() > 0) {
            int wordIndex = hangmanModel.getRandomGenerator().nextInt(gameWords.size());
            hangmanModel.setCurrWord(gameWords.get(wordIndex));
            hangmanModel.resetGuessesCount();
            view.refreshTriesCount(formatTriesString(remainingGuesses));
            hangmanModel.clearCorrectGuessSet();
            hangmanModel.clearWordSet();

            Log.i("GUESS WORD", currWord);

            for (int i = 0; i < currWord.length(); i++) {
                hangmanModel.getWordSet().add(String.valueOf(currWord.charAt(i)));
            }

            hangmanModel.setGuessWordArr(new char[currWord.length()]);

            for (int i = 0; i < currWord.length(); i++) {
                guessWordArr[i] = '_';
            }

            view.updateGuessWordTextView(formatGuessWord(guessWordArr));
            hangmanModel.setIncorrectChars("");
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
        HashSet<String> correctGuessSet = hangmanModel.getCorrectGuessSet();

        // prevents submission checking if same incorrect character is guessed multiple times
        if (!incorrectChars.contains(input)) {
            if (remainingGuesses > 0) {
                // case where current word does not contain guessed character, subtract from
                // remaining guesses and add to incorrectly guessed characters list
                if (!hangmanModel.getWordSet().contains(input)) {
                    incorrectChars += input + " ";
                    view.updateIncorrectGuessesTextView(incorrectChars);
                    remainingGuesses--;
                    view.refreshTriesCount(formatTriesString(remainingGuesses));
                    // if there are no guesses remaining then the game has ended and the user has lost
                    if (hangmanModel.getRemainingGuesses() == 0) {
                        view.displayLoseToast();
                        newGame();
                    }
                } else {
                    String currWord = hangmanModel.getCurrWord();
                    // case where the current word does contain the guessed character, replace the "_" with character
                    for (int i = 0; i < currWord.length(); i++) {
                        if (input.equals(String.valueOf(currWord.charAt(i)))) {
                            guessWordArr[i] = currWord.charAt(i);
                            correctGuessSet.add(String.valueOf(currWord.charAt(i)));
                        }
                    }
                    view.updateGuessWordTextView(formatGuessWord(guessWordArr));
                }
            }

            // correctGuessSet is used to determine that all "_" have been accurately guessed
            // and determine if the user has won by guessing all the right letters
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
     * Responsible for spinner logic in selecting difficulty of guess words pulled from API
     */
    void spinnerClickSetup(ArrayList<String> difficultyLevels) {
        difficultyLevels.add("Random");
        for (int i = 1; i <= 10; i++) {
            if (i == 1) {
                difficultyLevels.add(i + " (easy)");
            } else if (i == 10) {
                difficultyLevels.add(i + " (hard)");
            } else {
                difficultyLevels.add(String.valueOf(i));
            }
        }
    }

    void changeLevelDifficulty(int positionLevel) {
        if (positionLevel == 0) {
            new WordsTask().execute(API_URL);
        } else {
            new WordsTask().execute(API_URL + "?difficulty=" + positionLevel);
        }
        view.setGameReadyUI();
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
    String fetchWords(String[] url) {
        try {
            String urlString = url[0];
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(urlString)
                    .build();
            Response responses;

            responses = client.newCall(request).execute();
            String jsonData = responses.body().string();

            String[] words = jsonData.split("\\r?\\n");
            hangmanModel.setGameWords(Arrays.asList(words));

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
        new WordsTask().execute(API_URL);
    }

    /**
     * On a separate thread, will fetch the text containing all possible hangman words,
     * display a progress bar, and disable the submit & new word buttons and text field until network
     * call has completed.
     */
    private class WordsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return fetchWords(strings);
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
     * Interface used to communicate between the Presenter and View to update UI components
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
