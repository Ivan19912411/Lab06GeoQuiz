package com.example.lab06geoquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab06geoquiz.ui.theme.Lab06GeoQuizTheme
import kotlinx.coroutines.launch

// ==================== ДАННЫЕ ====================

data class Question(val text: String, val answer: Boolean)

val questionList = listOf(
    Question("Canberra is the capital of Australia.", true),
    Question("The Pacific Ocean is larger than the Atlantic Ocean.", true),
    Question("The Suez Canal connects the Red Sea and the Indian Ocean.", false),
    Question("The source of the Nile River is in Egypt.", false),
    Question("The Amazon River is the longest river in the Americas.", true),
    Question("Lake Baikal is the world's oldest and deepest freshwater lake.", true)
)

// ==================== ACTIVITY ====================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab06GeoQuizTheme {
                val snackbarHostState = remember { SnackbarHostState() }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { innerPadding ->
                    QuizScreen(
                        snackbarHostState = snackbarHostState,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// ==================== UI ====================

@Composable
fun QuizScreen(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var answered by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val currentQuestion = questionList[currentIndex]
    val isLastQuestion = currentIndex == questionList.lastIndex

    fun handleAnswer(userAnswer: Boolean) {
        if (userAnswer == currentQuestion.answer) {
            score++
        }
        answered = true

        if (isLastQuestion) {
            val finalScore = if (userAnswer == currentQuestion.answer) score else score
            scope.launch {
                snackbarHostState.showSnackbar(
                    "Правильных ответов: $finalScore из ${questionList.size}"
                )
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Вопрос ${currentIndex + 1} из ${questionList.size}",
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = currentQuestion.text,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (!answered) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { handleAnswer(true) }) {
                    Text("True")
                }
                Button(onClick = { handleAnswer(false) }) {
                    Text("False")
                }
            }
        } else {
            if (!isLastQuestion) {
                Button(onClick = {
                    currentIndex++
                    answered = false
                }) {
                    Text("Next")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuizScreenPreview() {
    Lab06GeoQuizTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { innerPadding ->
            QuizScreen(
                snackbarHostState = snackbarHostState,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}