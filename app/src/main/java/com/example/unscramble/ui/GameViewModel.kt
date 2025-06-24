package com.example.unscramble.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.unscramble.data.allWords
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.update
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SHUFFLE_PENALTY

private lateinit var currentWord: String
private var usedWords: MutableSet<String> = mutableSetOf()

private fun shuffleCurrentWord(word: String): String {
    val tempWord = word.toCharArray()
    tempWord.shuffle()
    while (String(tempWord) == word) {
        tempWord.shuffle()
    }
    return String(tempWord)
}

private fun pickRandomWordAndShuffle(): String {
    currentWord = allWords.random()
    if (usedWords.contains(currentWord)) {
        return pickRandomWordAndShuffle()
    } else {
        usedWords.add(currentWord)
        return shuffleCurrentWord(currentWord)
    }
}

data class GameUiState(
    val currentScrambledWord: String = "",
    val currentWordCount: Int = 1,
    val score: Int = 0,
    val isGuessedWordWrong: Boolean = false,
    val isGameOver: Boolean = false
)


class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    var userGuess by mutableStateOf("")
        private set

    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    init {
        resetGame()
    }

    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }

    fun updateUserGuess(guessedWord: String){
        userGuess = guessedWord
    }

    fun updateScore(updatedScore: Int){
        _uiState.update { currentState ->
            currentState.copy(
                score = updatedScore,
            )
        }
    }

    private fun updateGameState(updatedScore: Int) {
        if (usedWords.size == MAX_NO_OF_WORDS){
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    score = updatedScore,
                    currentWordCount = currentState.currentWordCount.inc(),
                )
            }
        }
    }

    fun skipWord() {
        updateGameState(_uiState.value.score)
        updateUserGuess("")
    }

    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore)
        } else {
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }
        updateUserGuess("")
    }

    fun reshuffle() {
        _uiState.update { currentState ->
            currentState.copy(currentScrambledWord = shuffleCurrentWord(currentWord))
        }
        val updatedScore = _uiState.value.score.minus(SHUFFLE_PENALTY)
        updateScore(updatedScore)
    }
}

