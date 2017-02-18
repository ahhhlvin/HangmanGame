package linkedin.app.hangmangame;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by alvin2 on 2/17/17.
 */

class MainPresenter implements WordsAsyncTask.WordsAsyncTaskListener {

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
                // case where current word does not contain guessed character, subtract from
                // remaining guesses and add to incorrectly guessed characters list
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

    void changeLevelDifficulty(int difficultyLevel) {
        WordsAsyncTask task = new WordsAsyncTask();
        task.setWordsTaskListener(this);
        if (difficultyLevel == 0) {
            task.execute(API_URL);
        } else {
            task.execute(API_URL + "?difficulty=" + difficultyLevel);
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

    @Override
    public void loadWords(List<String> gameWords) {
        this.gameWords = gameWords;
    }

    @Override
    public void downloadTaskCompleted() {
        newGame();
        view.setGameReadyUI();
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
