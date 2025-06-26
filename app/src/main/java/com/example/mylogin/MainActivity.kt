package com.example.mylogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mylogin.ui.theme.MyLoginTheme
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private var user by mutableStateOf<FirebaseUser?>(null)

    private val signInLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                Log.d("MainActivity", "Sign-in flow successful.")
                this.user = auth.currentUser
            } else {
                Log.w("MainActivity", "Sign-in flow cancelled or failed.")
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        this.user = auth.currentUser
        enableEdgeToEdge()

        setContent {
            MyLoginTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (user != null) {
                        HomeScreen(
                            displayName = user!!.displayName,
                            email = user!!.email,
                            onSignOut = {
                                AuthUI.getInstance().signOut(this).addOnCompleteListener {
                                    this.user = null
                                }
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        AuthenticationScreen(
                            modifier = Modifier.padding(innerPadding),
                            onSignInRequested = {
                                val intent = Intent(this, SignInActivity::class.java)
                                signInLauncher.launch(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AuthenticationScreen(modifier: Modifier = Modifier, onSignInRequested: () -> Unit) {
    var showLogin by remember { mutableStateOf(true) }

    if (showLogin) {
        LoginScreen(
            modifier = modifier,
            onRegisterClicked = { showLogin = false },
            onSignInRequested = onSignInRequested
        )
    } else {
        RegisterScreen(
            modifier = modifier,
            onLoginClicked = { showLogin = true },
            onSignInRequested = onSignInRequested
        )
    }
}

@Composable
fun HomeScreen(displayName: String?, email: String?, onSignOut: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = displayName ?: "No display name", style = MaterialTheme.typography.bodyLarge)
        Text(text = email ?: "No email", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onSignOut) {
            Text(text = "Sign Out")
        }
    }
}

@Composable
fun LoginScreen(modifier: Modifier = Modifier, onRegisterClicked: () -> Unit, onSignInRequested: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.login), contentDescription = "Login image", modifier = Modifier.size(200.dp))
        Text(text = "Welcome Back to myApp!", style = MaterialTheme.typography.headlineSmall)
        Text(text = "Login to your account!")

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onSignInRequested() },
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text(text = "Login")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Don't have an account? ")
            Text(
                text = "Register Now",
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { onRegisterClicked() }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Or sign in with:")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(painter = painterResource(id = R.drawable.google),
                contentDescription = "Google",
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .size(50.dp)
                    .clickable { onSignInRequested() }
            )
        }
    }
}

@Composable
fun RegisterScreen(modifier: Modifier = Modifier, onLoginClicked: () -> Unit, onSignInRequested: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.register), contentDescription = "Register image", modifier = Modifier.size(200.dp))
        Text(text = "Create New Account", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onSignInRequested() },
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text(text = "Register")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Already have an account? ")
            Text(
                text = "Login",
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { onLoginClicked() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    MyLoginTheme {
        LoginScreen(modifier = Modifier, onRegisterClicked = {}, onSignInRequested = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegisterScreen() {
    MyLoginTheme {
        RegisterScreen(modifier = Modifier, onLoginClicked = {}, onSignInRequested = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAuthenticationScreen() {
    MyLoginTheme {
        AuthenticationScreen(
            modifier = Modifier.padding(),
            onSignInRequested = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    MyLoginTheme {
        HomeScreen(
            displayName = "John Doe",
            email = "John.doe@gmail.com",
            onSignOut = { },
            modifier = Modifier.padding(),
        )
    }
}