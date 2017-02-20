package linkedin.app.hangmangame;

import android.content.Context;
import android.os.Bundle;
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
    int remainingGuesses = 6;
    private String currWord;
    private char[] guessWordArr;
    private HashSet<String> wordSet;
    private HashSet<String> correctGuessSet;
    private ArrayList<String> incorrectChars;
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
        incorrectChars = new ArrayList<>();
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
            incorrectChars.clear();
            view.updateIncorrectGuessesTextView(incorrectChars.toString());
        }
    }

    /**
     * Includes the logic for checking whether the submitted character guess is one that
     * exists within the game word or submitted word guess matches game word.
     *
     * @param input the single letter character or full word the user submitted in the text field
     */
    void checkSubmission(String input) {

        // prevents submission checking if same incorrect guess is submitted multiple times
        if (!incorrectChars.contains(input)) {
            if (remainingGuesses > 0) {
                // case where current word does not contain guessed character or matches guessed
                // word, subtract from remaining guesses and add to incorrectly guessed characters list
                if (input.length() == 1 && !wordSet.contains(input) || input.length() > 1 && !currWord.equals(input)) {
                    incorrectChars.add(input);
                    view.updateIncorrectGuessesTextView(incorrectChars.toString());
                    remainingGuesses--;
                    view.refreshTriesCount(formatTriesString(remainingGuesses));
                    // if there are no guesses remaining then the game has ended and the user has lost
                    if (remainingGuesses == 0) {
                        displayWinLoseMessage(false);
                    }
                } else if (input.length() == 1 && wordSet.contains(input)) {
                    // case where the current word does contain the guessed character, replace the "_" with character
                    for (int i = 0; i < currWord.length(); i++) {
                        if (input.equals(String.valueOf(currWord.charAt(i)))) {
                            guessWordArr[i] = currWord.charAt(i);
                            correctGuessSet.add(String.valueOf(currWord.charAt(i)));
                        }
                    }
                    view.updateGuessWordTextView(formatGuessWord(guessWordArr));
                    checkWordMatchesGuess();
                } else if (input.length() > 1 && currWord.equals(input)) {
                    // case where the current submitted guess word does match the actual game word
                    // and user has won
                    displayWinLoseMessage(true);
                }
            }
        }

    }

    /**
     * Responsible for spinner logic in selecting difficulty of guess words pulled from API
     */
    void spinnerClickSetup(ArrayList<String> difficultyLevels) {
        difficultyLevels.add("Random difficulty");
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

    /**
     * Refreshes game words list with appropriate difficulty level words by changing the API
     * parameter that is fetched and making a new network call
     * @param difficultyLevel int represents the difficulty level that the user selected from the
     *                        spinner
     */
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
     * Uses correctGuessSet to determine if all "_" have been accurately guessed
     * and determines if the user has won
     */
    private void checkWordMatchesGuess() {
        if (!correctGuessSet.contains("_")) {
            String result = "";

            for (Character c : guessWordArr) {
                result += c;
            }

            if (result.equals(currWord)) {
                displayWinLoseMessage(true);
            }
        }
    }

    /**
     * Displays a message to notify that the user has lost the current round
     */
    private void displayWinLoseMessage(boolean userDidWin) {
        if (userDidWin) {
            view.updateGuessWordTextView("Y O U   W O N !  : )");
        } else {
            view.updateGuessWordTextView("G A M E   O V E R !  : ( \n Word was: " + currWord);
        }
        view.hideKeyboard();
        view.displayWinLoseSnackbar(userDidWin);
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
     * Interface method that assigns all downloaded game words to the property of the MainPresenter
     * object, communicates between WordsTask and MainPresenter by notifying when task has completed
     * @param gameWords List<String> an ArrayList of type String words representing guess words
     */
    @Override
    public void loadWords(List<String> gameWords) {
        this.gameWords = gameWords;
    }

    /**
     * Interface method that notifies MainPresenter that the WordsTask has completed on the background
     * thread and is ready to start a new game
     */
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

        void displayWinLoseSnackbar(boolean userDidWin);

        void updateIncorrectGuessesTextView(String incorrectGuesses);

        void setGameReadyUI();

        void hideKeyboard();
    }
}
