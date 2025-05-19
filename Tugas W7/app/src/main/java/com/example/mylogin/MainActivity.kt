package com.example.mylogin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mylogin.ui.theme.MyLoginTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyLoginTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AuthenticationScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AuthenticationScreen(modifier: Modifier = Modifier) {
    var showLogin by remember { mutableStateOf(true) }

    if (showLogin) {
        LoginScreen(
            modifier = modifier,
            onRegisterClicked = { showLogin = false },
        )
    } else {
        RegisterScreen(
            modifier = modifier,
            onLoginClicked = { showLogin = true }
        )
    }
}

@Composable
fun LoginScreen(modifier: Modifier = Modifier, onRegisterClicked: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showNotImplementedDialog by remember { mutableStateOf(false) }

    if (showNotImplementedDialog) {
        AlertDialog(
            onDismissRequest = {
                showNotImplementedDialog = false
            },
            title = {
                Text(text = "Feature Unavailable!")
            },
            text = {
                Text("Sorry 🙏🏻, this feature hasn't been implemented yet.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showNotImplementedDialog = false
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.login), contentDescription = "Login image", modifier = Modifier.size(200.dp))
        Text(text = "Welcome Back to myApp!", style = MaterialTheme.typography.headlineSmall)
        Text(text = "Login to your account!")

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") },
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {showNotImplementedDialog = true},
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text(text = "Login")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Forgot Password?",
            color = MaterialTheme.colorScheme.primary,
            textDecoration= TextDecoration.Underline,
            modifier = Modifier.clickable{
            showNotImplementedDialog = true
        })
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Don't have an account? ")
            Text(
                text = "Register",
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    onRegisterClicked()
                }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Or sign in with:")
        Row(
            modifier = Modifier.fillMaxWidth().padding(30.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(painter = painterResource(id = R.drawable.yahoo),
                contentDescription = "Yahoo Mail",
                modifier = Modifier.padding(horizontal = 10.dp).size(50.dp).clickable{
                    showNotImplementedDialog = true
            })
            Image(painter = painterResource(id = R.drawable.google),
                contentDescription = "Google",
                modifier = Modifier.padding(horizontal = 10.dp).size(50.dp).clickable{
                    showNotImplementedDialog = true
            })
            Image(painter = painterResource(id = R.drawable.github),
                contentDescription = "Github",
                modifier = Modifier.padding(horizontal = 10.dp).size(50.dp).clickable{
                    showNotImplementedDialog = true
            })
        }
    }

}

@Composable
fun RegisterScreen(modifier: Modifier = Modifier, onLoginClicked: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showNotImplementedDialog by remember { mutableStateOf(false) }

    if (showNotImplementedDialog) {
        AlertDialog(
            onDismissRequest = { showNotImplementedDialog = false },
            title = { Text("Feature Not Implemented") },
            text = { Text("This feature is not yet available in this demo.") },
            confirmButton = {
                Button(onClick = { showNotImplementedDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.register),
            contentDescription = "Register image",
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Create New Account", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Sign up to get started!")

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(text = "Full Name") },
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") },
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(text = "Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            isError = password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword,
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { showNotImplementedDialog = true },
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text(text = "Register")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Already have an account? ")
            Text(
                text = "Login",
                color =  MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    onLoginClicked()
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Or sign up with:")
        Row(
            modifier = Modifier
                .fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(painter = painterResource(id = R.drawable.yahoo),
                contentDescription = "Yahoo Mail",
                modifier = Modifier.padding(horizontal = 15.dp).size(40.dp).clickable{
                    showNotImplementedDialog = true
                })
            Image(painter = painterResource(id = R.drawable.google),
                contentDescription = "Google",
                modifier = Modifier.padding(horizontal = 15.dp).size(40.dp).clickable{
                    showNotImplementedDialog = true
                })
            Image(painter = painterResource(id = R.drawable.github),
                contentDescription = "Github",
                modifier = Modifier.padding(horizontal = 15.dp).size(40.dp).clickable{
                    showNotImplementedDialog = true
                })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    MyLoginTheme {
        LoginScreen(modifier = Modifier, onRegisterClicked = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegisterScreen() {
    MyLoginTheme {
        RegisterScreen(modifier = Modifier, onLoginClicked = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAuthenticationScreen() {
    MyLoginTheme {
        AuthenticationScreen()
    }
}
