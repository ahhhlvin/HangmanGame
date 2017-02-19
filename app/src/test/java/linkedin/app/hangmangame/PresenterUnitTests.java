package linkedin.app.hangmangame;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashSet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.junit.Assert.*;

public class PresenterUnitTests {

    private MainPresenter mockPresenter;
    private WordsAsyncTask testTask = new WordsAsyncTask();

    @Before
    public void testSetup() {
        mockPresenter = Mockito.mock(MainPresenter.class);
        mockPresenter.setup();
        mockPresenter.remainingGuesses = 6;
        mockPresenter.checkSubmission("a");
        testTask = new WordsAsyncTask();
        testTask.setWordsTaskListener(mockPresenter);
    }

    @After
    public void testCleanup(){

    }

    /**
     * Tests that the valid URL will return successfully
     */
    @Test
    public void testFetchWordsValidUrl() {
        String result = testTask.fetchWords(new String[]{MainPresenter.API_URL});
        assertEquals("SUCCESS", result);
        // TODO: assertNotEquals gameWords != 0
    }

    /**
     * Tests that an invalid URL will throw an IllegalArgumentException
     */
    @Test
            (expected=IllegalArgumentException.class) public void testFetchWordsInvalidUrl() {
        testTask.fetchWords(new String[]{"tp://linkedin-reach.hagbpyjegb.us-west-2.elasticbeanstalk.com/words"});
        // TODO: assertEquals gameWords == 0
    }

    /**
     * Tests that empty submission will not be accepted
     */
    @Test
    public void testCheckEmptySubmission() {
        verify(mockPresenter, times(0)).checkSubmission("");
    }

    /**
     * Tests that invalid submission will not be accepted
     */
    @Test
    public void testCheckInvalidSubmission() {
        verify(mockPresenter, times(0)).checkSubmission("asdjl");
    }

    /**
     * Tests that non-empty submission will be processed
     */
    @Test
    public void testCheckNonEmptySubmission() {
        verify(mockPresenter, times(1)).checkSubmission("a");
    }

    // TODO: how to mock and correctly access properties of MainPresenter class?
//    @Test
//    public void testTriesTextFormatting() {
//        mockPresenter.remainingGuesses -= 3;
//        String remainingGuessesText = mockPresenter.formatTriesString(mockPresenter.remainingGuesses);
//        assertEquals("Remaining guesses: 3", remainingGuessesText);
//    }

    // TODO: complicated method :(
//    @Test
//    public void testCheckSubmission() {
//        mockPresenter.checkSubmission("a");
//        assertEquals(5, mockPresenter.remainingGuesses);
//        assertEquals("a ", mockPresenter.incorrectChars);
//    }

//    @Test
//    public void testNewGame() {
//        mockPresenter.remainingGuesses -= 3;
//        mockPresenter.incorrectChars = "a b c d ";
//        mockPresenter.correctGuessSet.add("h");
//        mockPresenter.correctGuessSet.add("e");
//        mockPresenter.correctGuessSet.add("l");
//        mockPresenter.correctGuessSet.add("l");
//        mockPresenter.correctGuessSet.add("_");
//        mockPresenter.wordSet.add("h");
//        mockPresenter.wordSet.add("e");
//        mockPresenter.wordSet.add("l");
//        mockPresenter.wordSet.add("l");
//        mockPresenter.wordSet.add("o");
//        mockPresenter.newGame();
//        assertEquals(6, mockPresenter.remainingGuesses);
//        assertEquals("", mockPresenter.incorrectChars);
//        assertEquals(0, mockPresenter.correctGuessSet.size());
//        assertEquals(0, mockPresenter.wordSet.size());
//    }

}