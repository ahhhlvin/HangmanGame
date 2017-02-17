package linkedin.app.hangmangame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Created by alvin2 on 2/17/17.
 */

class HangmanModel {
    private Random randomGenerator;
    private int remainingGuesses = 6;
    private String currWord;
    private char[] guessWordArr;
    private HashSet<String> wordSet;
    private HashSet<String> correctGuessSet;
    private String incorrectChars = "";
    private List<String> gameWords;

    HangmanModel() {
        this.randomGenerator = new Random();
        this.gameWords = new ArrayList<>();
        this.wordSet = new HashSet<>();
        this.correctGuessSet = new HashSet<>();
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

    void setCurrWord(String currWord) {
        this.currWord = currWord;
    }

    char[] getGuessWordArr() {
        return guessWordArr;
    }

    void setGuessWordArr(char[] guessWordArr) {
        this.guessWordArr = guessWordArr;
    }

    HashSet<String> getWordSet() {
        return wordSet;
    }

    HashSet<String> getCorrectGuessSet() {
        return correctGuessSet;
    }

    String getIncorrectChars() {
        return incorrectChars;
    }

    void setIncorrectChars(String incorrectChars) {
        this.incorrectChars = incorrectChars;
    }

    List<String> getGameWords() {
        return gameWords;
    }

    void setGameWords(List<String> gameWords) {
        this.gameWords = gameWords;
    }

    void resetGuessesCount() {
        this.remainingGuesses = 6;
    }

    void clearCorrectGuessSet() {
        this.correctGuessSet.clear();
    }

    void clearWordSet() {
        this.wordSet.clear();
    }
}
