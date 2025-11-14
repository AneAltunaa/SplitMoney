package com.example.splitmoney.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.splitmoney.ui.theme.SplitMoneyTheme

class UserProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Εδώ εφαρμόζουμε το app theme σου
            SplitMoneyTheme(
                // αν θες μπορείς αργότερα να το κάνεις dynamic με isSystemInDarkTheme()
                // darkTheme = isSystemInDarkTheme()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        UserProfileScreen()

                        // Back button πάνω-αριστερά
                        BackButton(activity = this@UserProfileActivity)
                    }
                }
            }
        }
    }

    companion object
}

@Composable
fun BackButton(activity: Activity) {
    IconButton(
        onClick = { activity.finish() },
        modifier = Modifier
            .padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Go Back",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun UserProfileScreen() {

    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // Top bar / τίτλος
            Text(
                text = "SnapRec",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Serif,
                color = colorScheme.onPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorScheme.primary)
                    .padding(16.dp)
            )

            // Κεντρικό περιεχόμενο με border στο primary
            Box(
                modifier = Modifier
                    .border(
                        width = 0.5.dp,
                        color = colorScheme.primary
                    )
                    .weight(1f) // για να αφήσει χώρο στο bottom bar
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(all = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    // TODO: Retrieve the user information
                    // TODO: make the icon so people can put their own picture (?)

                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Account",
                        tint = colorScheme.onBackground,
                        modifier = Modifier.size(200.dp)
                    )

                    Text(
                        text = "Name",
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 15.sp,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Start,
                        color = colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, start = 26.dp)
                    )

                    Text(
                        text = "Last name",
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 15.sp,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Start,
                        color = colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, start = 26.dp)
                    )

                    Text(
                        text = "Date of birth",
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 15.sp,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Start,
                        color = colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, start = 26.dp)
                    )

                    Spacer(modifier = Modifier.height(50.dp))

                    Text(
                        text = "Mail",
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 15.sp,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Start,
                        color = colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, start = 26.dp)
                    )

                    Text(
                        text = "Phone number",
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 15.sp,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Start,
                        color = colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, start = 26.dp)
                    )

                    Spacer(modifier = Modifier.height(50.dp))

                    Button(
                        onClick = { /* TODO: change password */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Change Password")
                    }
                }
            }
        }

        // Bottom navigation bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.primary)
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val context = LocalContext.current

            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Home",
                tint = colorScheme.onPrimary
            )

            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Account",
                tint = colorScheme.onPrimary,
                modifier = Modifier.clickable {
                    context.startActivity(Intent(context, UserProfileActivity::class.java))
                }
            )

            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings",
                tint = colorScheme.onPrimary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfileScreenPreview() {
    SplitMoneyTheme(darkTheme = false) {
        UserProfileScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfileScreenDarkPreview() {
    SplitMoneyTheme(darkTheme = true) {
        UserProfileScreen()
    }
}
