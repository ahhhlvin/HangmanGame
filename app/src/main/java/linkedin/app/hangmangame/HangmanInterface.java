package linkedin.app.hangmangame;

import java.util.List;

/**
 * Created by alvin2 on 2/20/17.
 */

public class HangmanInterface {

    /**
     * Interface used to communicate between the Presenter and View to update UI components
     */
    interface View {
        void refreshTriesCount(String triesFormattedInput);
        void updateGuessWordTextView(String guessWord);
        void displayWinLoseSnackbar(boolean userDidWin);
        void updateIncorrectGuessesTextView(String incorrectGuesses);
        void hideKeyboard();
        void showHead();
        void showLeftArm();
        void showBody();
        void showRightArm();
        void showLeftLeg();
        void showRightLeg();
        void showProgressBar();
        void hideProgressBar();
        void displayNewWordSnackbar();
    }

    interface Presenter {
        void hideProgressBar();
        void setupNewRound();
        void spinnerClickSetup(List<String> difficultyLevels);
        void checkSubmission(String input);
        void updateHangmanImage(int remainingGuesses);
        void displayWinLoseMessage(boolean userDidWin);
        void changeLevelDifficulty(int difficultyLevel);
    }
}
