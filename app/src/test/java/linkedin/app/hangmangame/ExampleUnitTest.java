//package linkedin.app.hangmangame;
//
//import org.junit.Test;
//
//import java.io.IOException;
//import java.util.ArrayList;
//
//import static org.junit.Assert.*;
//
///**
// * Example local unit test, which will execute on the development machine (host).
// *
// * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
// */
//public class ExampleUnitTest {
////    @Test
////    public void addition_isCorrect() throws Exception {
////        assertEquals(4, 2 + 2);
////    }
//
//    private MainActivity testActivity = new MainActivity();
//
//    @Test
//    public void testFetchWordsValidUrl() {
//        String result = testActivity.fetchWords("http://linkedin-reach.hagbpyjegb.us-west-2.elasticbeanstalk.com/words");
//        assertEquals("SUCCESS", result);
//    }
//
//    @Test
//            (expected=IllegalArgumentException.class) public void testFetchWordsInvalidUrl() {
//        testActivity.fetchWords("htt://linkedin-reach.hagbpyjegbasdfsfd");
//    }
//
//
//    // TODO: extract out UI update methods into interface and then reformat methods to be testable?
////    @Test
////    public void testRefreshTries() {
////        testActivity.remainingGuesses -= 3;
////        testActivity.refreshTriesCount();
////        assertEquals(6, testActivity.remainingGuesses);
////    }
//
////    @Test
////    public void testUpdateGuessWordTextView() {
////
////    }
//
//
//    // TODO: complicated method :(
////    @Test
////    public void testCheckSubmission() {
////        testActivity.checkSubmission("a");
////        assertEquals(5, testActivity.remainingGuesses);
////        assertEquals("a ", testActivity.incorrectChars);
////    }
//
//    @Test
//    public void testNewGame() {
//        testActivity.remainingGuesses = 4;
//        testActivity.incorrectChars = "a b c d ";
//        testActivity.correctGuessSet.add("h");
//        testActivity.correctGuessSet.add("e");
//        testActivity.correctGuessSet.add("l");
//        testActivity.correctGuessSet.add("l");
//        testActivity.correctGuessSet.add("_");
//        testActivity.wordSet.add("h");
//        testActivity.wordSet.add("e");
//        testActivity.wordSet.add("l");
//        testActivity.wordSet.add("l");
//        testActivity.wordSet.add("o");
//        testActivity.newGame();
//        assertEquals(6, testActivity.remainingGuesses);
//        assertEquals("", testActivity.incorrectChars);
//        assertEquals(0, testActivity.correctGuessSet.size());
//        assertEquals(0, testActivity.wordSet.size());
//    }
//
//}