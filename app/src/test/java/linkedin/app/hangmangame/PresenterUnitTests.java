package linkedin.app.hangmangame;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.junit.Assert.*;

public class PresenterUnitTests {

    private HangmanPresenter mockPresenter;
    private WordsAsyncTask testTask = new WordsAsyncTask();

    @Before
    public void testSetup() {
        mockPresenter = Mockito.mock(HangmanPresenter.class);
        mockPresenter.checkSubmission("a");
        testTask = new WordsAsyncTask();
        testTask.setWordsAsyncTaskListener(mockPresenter);
    }

    @After
    public void testCleanup(){
    }

    /**
     * Tests that the valid URL will return successfully
     */
    @Test
    public void testFetchWordsValidUrl() {
        String result = testTask.fetchWords(new String[]{HangmanPresenter.API_URL});
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
        verify(mockPresenter, times(0)).checkSubmission("asdjl?");
    }

    /**
     * Tests that non-empty submission will be processed
     */
    @Test
    public void testCheckNonEmptySubmission() {
        verify(mockPresenter, times(1)).checkSubmission("a");
    }
}