package com.example.happybirthday

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.example.happybirthday.ui.theme.HappyBirthdayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HappyBirthdayTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BirthdayCardPreview()
                }
            }
        }
    }
}

@Composable
fun BirthdayCard(message: String, from: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isDarkMode = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    val backgroundImage = if (isDarkMode) {
        R.drawable.birthdaybgdark
    } else {
        R.drawable.birthdaybglight
    }
    Box( modifier = modifier) {
        Image(
            painter = painterResource(backgroundImage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 1f
        )
        GreetingText(message = message, from = from, modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun GreetingText(message: String, from: String, modifier: Modifier = Modifier) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val context = LocalContext.current
    val isDarkMode = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    val topPadding = if (isDarkMode){
         screenHeight * 0.3f
    }
    else{
        screenHeight * 0.2f
    }
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = modifier
    ) {
        Text(
            text = message,
            fontSize = 62.sp,
            modifier = Modifier.padding(top = topPadding),
            lineHeight = 70.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = from,
            fontSize = 32.sp,
            modifier = Modifier
                .padding(16.dp)
                .align(alignment = Alignment.End),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DarkMode"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun BirthdayCardPreview() {
    HappyBirthdayTheme {
        BirthdayCard(
            message = "🎂\nHappy Birthday Billy!🥳🥳",
            from = "From Jonathan",
            modifier = Modifier.fillMaxSize()
        )
    }
}