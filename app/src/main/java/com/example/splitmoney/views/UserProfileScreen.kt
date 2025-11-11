package com.example.splitmoney.views

import android.R.attr.navigationIcon
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitmoney.MainActivity


class UserProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UserProfileScreen()
            BackButton(this)
        }
    }

    companion object
}
@Composable
fun BackButton(activity: Activity) {
    IconButton(
        onClick = { activity.finish() },
        modifier = Modifier.padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Go Back",
            tint = Color.Black
        )
    }
}

@Composable
fun UserProfileScreen(){

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        )
        {

                Text(
                    text = "SnapRec",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF42EFC4))
                        .padding(16.dp)

                )


            Box(modifier = Modifier.border(width = 0.5.dp, color = Color(0xFF42EFC4))){
                Column(
                    modifier = Modifier.fillMaxSize().padding(all = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {


                    //TODO: Retrieve the user information

                    //TODO: make the icon so people can put their own picture (?)
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Account",
                        tint = Color.Black,
                        modifier = Modifier.size(200.dp
                        )

                    )

                    Text(text = "Name:", style = MaterialTheme.typography.headlineLarge, fontSize = 12.sp)
                    Text(text = "Last name:", style = MaterialTheme.typography.headlineLarge, fontSize = 12.sp)
                    Text(text = "Date of Birth:", style = MaterialTheme.typography.headlineLarge, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(height = 50.dp))
                    Text(text = "Mail:", style = MaterialTheme.typography.headlineLarge, fontSize = 12.sp)
                    Text(text = "Phone Number:", style = MaterialTheme.typography.headlineLarge, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(height = 50.dp))
                    Button(onClick = {}, colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF42EFC4),
                        contentColor = Color.Black
                    ),
                        shape = RoundedCornerShape(16.dp)) { Text("Change Password")}
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF42EFC4))
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),

                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically

            )
            {
                val context = LocalContext.current

                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home",
                    tint = Color.Black
                )

                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Account",
                    tint = Color.Black,
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(context, UserProfileActivity::class.java))
                    }

                )
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = Color.Black
                )
            }
        }
    

}

