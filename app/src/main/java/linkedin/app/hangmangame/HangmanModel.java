package linkedin.app.hangmangame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by alvin2 on 2/17/17.
 */

class HangmanModel {
    private Random randomGenerator;
    private int remainingGuesses;
    private String currWord;
    private char[] guessWordArr;
    private HashSet<String> wordSet;
    private HashSet<String> correctGuessSet;
    private List<String> incorrectChars;
    private List<String> gameWords;

    HangmanModel() {
        randomGenerator = new Random();
        remainingGuesses = 6;
        gameWords = new ArrayList<>();
        wordSet = new HashSet<>();
        correctGuessSet = new HashSet<>();
        currWord = "";
        guessWordArr = new char[]{};
        incorrectChars = new ArrayList<>();
    }

    Random getRandomGenerator() {
        return randomGenerator;
    }

    int getRemainingGuesses() {
        return remainingGuesses;
    }

    String getCurrWord() {
        return currWord;
    }

    char[] getGuessWordArr() {
        return guessWordArr;
    }

    Set<String> getWordSet() {
        return wordSet;
    }

    Set<String> getCorrectGuessSet() {
        return correctGuessSet;
    }

    List<String> getIncorrectChars() {
        return incorrectChars;
    }

    List<String> getGameWords() {
        return gameWords;
    }
}
