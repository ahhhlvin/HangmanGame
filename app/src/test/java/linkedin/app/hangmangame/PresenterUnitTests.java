package linkedin.app.hangmangame;

import org.junit.Test;

import static org.junit.Assert.*;

public class PresenterUnitTests {

    private MainPresenter testPresenter = new MainPresenter();

    /**
     * Tests that the valid URL will return successfully
     */
    @Test
    public void testFetchWordsValidUrl() {
        String result = testPresenter.fetchWords(new String[]{MainPresenter.API_URL});
        assertEquals("SUCCESS", result);
        // TODO: assertNotEquals gameWords != 0
    }

    /**
     * Tests that an invalid URL will throw an IllegalArgumentException
     */
    @Test
            (expected=IllegalArgumentException.class) public void testFetchWordsInvalidUrl() {
        testPresenter.fetchWords(new String[]{"tp://linkedin-reach.hagbpyjegb.us-west-2.elasticbeanstalk.com/words"});
        // TODO: assertEquals gameWords == 0
    }

    // TODO: how to mock and correctly access properties of MainPresenter class?
//    @Test
//    public void testRefreshTries() {
//        testPresenter.remainingGuesses -= 3;
//        testPresenter.refreshTriesCount();
//        assertEquals(6, testPresenter.remainingGuesses);
//    }

//    @Test
//    public void testUpdateGuessWordTextView() {
//
//    }


    // TODO: complicated method :(
//    @Test
//    public void testCheckSubmission() {
//        testPresenter.checkSubmission("a");
//        assertEquals(5, testPresenter.remainingGuesses);
//        assertEquals("a ", testPresenter.incorrectChars);
//    }

//    @Test
//    public void testNewGame() {
//        testPresenter.remainingGuesses = 4;
//        testPresenter.incorrectChars = "a b c d ";
//        testPresenter.correctGuessSet.add("h");
//        testPresenter.correctGuessSet.add("e");
//        testPresenter.correctGuessSet.add("l");
//        testPresenter.correctGuessSet.add("l");
//        testPresenter.correctGuessSet.add("_");
//        testPresenter.wordSet.add("h");
//        testPresenter.wordSet.add("e");
//        testPresenter.wordSet.add("l");
//        testPresenter.wordSet.add("l");
//        testPresenter.wordSet.add("o");
//        testPresenter.newGame();
//        assertEquals(6, testPresenter.remainingGuesses);
//        assertEquals("", testPresenter.incorrectChars);
//        assertEquals(0, testPresenter.correctGuessSet.size());
//        assertEquals(0, testPresenter.wordSet.size());
//    }

}