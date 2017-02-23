package linkedin.app.hangmangame;

import android.content.Context;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

/**
 * Created by alvin2 on 2/17/17.
 */

class HangmanPresenter implements WordsAsyncTask.WordsAsyncTaskListener, HangmanInterface.Presenter {

    private HangmanInterface.View mView;
    private Context mContext;

    private Random randomGenerator;
    private int remainingGuesses = 6;
    String currWord;
    private char[] guessWordArr;
    private Set<String> wordSet;
    private Set<String> correctGuessSet;
    private List<String> incorrectChars;
    private List<String> gameWords;

    static final String API_URL = "http://linkedin-reach.hagbpyjegb.us-west-2.elasticbeanstalk.com/words";

    /**
     * Assigns the 'mContext' and 'mView' property of the HangmanPresenter object
     * @param view HangmanPresenter.UI the class that implements the "UI" interface to communicate with business logic
     * @param context Context used to access string values in the resources folder
     */
    HangmanPresenter(HangmanInterface.View view, Context context) {
        mView = view;
        mContext = context;
        setup();
    }

    /**
     * Initializes properties of the HangmanPresenter object
     */
    void setup() {
        HangmanModel mModel = new HangmanModel();
        randomGenerator = mModel.getRandomGenerator();
        remainingGuesses = mModel.getRemainingGuesses();
        currWord = mModel.getCurrWord();
        guessWordArr = mModel.getGuessWordArr();
        wordSet = mModel.getWordSet();
        correctGuessSet = mModel.getCorrectGuessSet();
        incorrectChars = mModel.getIncorrectChars();
        gameWords = mModel.getGameWords();
    }

    /**
     * Creates a new round by retrieving another word from the list of possible word
     * choices and resets necessary values back to their initial values.
     */
    @Override
    public void setupNewRound() {
        if (gameWords.size() > 0) {
            int wordIndex = randomGenerator.nextInt(gameWords.size());
            currWord = gameWords.get(wordIndex);
            remainingGuesses = 6;
            mView.refreshTriesCount(formatTriesString(remainingGuesses));
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

            mView.updateGuessWordTextView(formatGuessWord(guessWordArr));
            incorrectChars.clear();
            mView.updateIncorrectGuessesTextView(incorrectChars.toString());
            mView.displayNewWordSnackbar();
        }
    }

    /**
     * Includes the logic for checking whether the submitted character guess is one that
     * exists within the game word or submitted word guess matches game word.
     *
     * @param input the single letter character or full word the user submitted in the text field
     */
    @Override
    public void checkSubmission(String input) {

        // prevents submission checking if same incorrect guess is submitted multiple times
        if (!incorrectChars.contains(input)) {
            if (remainingGuesses > 0) {
                // case where current word does not contain guessed character or matches guessed
                // word, subtract from remaining guesses and add to incorrectly guessed characters list
                if (input.length() == 1 && !wordSet.contains(input) || input.length() > 1 && !currWord.equals(input)) {
                    incorrectChars.add(input);
                    mView.updateIncorrectGuessesTextView(incorrectChars.toString());
                    remainingGuesses--;
                    updateHangmanImage(remainingGuesses);
                    mView.refreshTriesCount(formatTriesString(remainingGuesses));
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
                    mView.updateGuessWordTextView(formatGuessWord(guessWordArr));
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
     * For every submission will update the hangman to display a new body part if the guess was incorrect
     * @param remainingGuesses int representing the number of guesses the user has remaining
     */
    @Override
    public void updateHangmanImage(int remainingGuesses) {
        switch (remainingGuesses) {
            case 0:
                mView.showLeftLeg();
                break;
            case 1:
                mView.showRightLeg();
                break;
            case 2:
                mView.showRightArm();
                break;
            case 3:
                mView.showLeftArm();
                break;
            case 4:
                mView.showBody();
                break;
            case 5:
                mView.showHead();
                break;
        }
    }

    /**
     * Responsible for spinner logic in selecting difficulty of guess words pulled from API
     */
    @Override
    public void spinnerClickSetup(List<String> difficultyLevels) {
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
    @Override
    public void changeLevelDifficulty(int difficultyLevel) {
        WordsAsyncTask task = new WordsAsyncTask();
        task.setWordsAsyncTaskListener(this);
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
    public void displayWinLoseMessage(boolean userDidWin) {
        if (userDidWin) {
            mView.updateGuessWordTextView("Y O U   W O N !  : )");
        } else {
            mView.updateGuessWordTextView("G A M E   O V E R !  : ( \n Word was: " + currWord);
        }
        mView.hideKeyboard();
        mView.displayWinLoseSnackbar(userDidWin);
    }

    /**
     * Correctly formats and concatenates text to display remaining guesses
     * @param remainingTries int representing the number of guesses the user has remaining
     * @return String concatenated and formatted text to display
     */
    private String formatTriesString(int remainingTries) {
        return String.format(Locale.US, "%1$s %2$d", mContext.getResources().getString(R.string.guess_remaining_text), remainingTries);
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
     * Interface method that assigns all downloaded game words to the property of the HangmanPresenter
     * object, communicates between WordsAsyncTask and HangmanPresenter by notifying when task has completed
     * @param gameWords List<String> an ArrayList of type String words representing guess words
     */
    @Override
    public void loadWords(List<String> gameWords) {
        this.gameWords = gameWords;
    }

    /**
     * Interface method that notifies HangmanPresenter that the WordsAsyncTask has completed on the background
     * thread and is ready to start a new game
     */
    @Override
    public void downloadTaskCompleted() {
        setupNewRound();
        hideProgressBar();
    }

    /**
     * Interface method that calls the view to display the progress bar
     */
    @Override
    public void hideProgressBar() {
        mView.hideProgressBar();
    }
}
