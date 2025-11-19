package com.example.splitmoney

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.splitmoney.ui.theme.SplitMoneyTheme
import com.example.splitmoney.viewModels.ExpenseShareViewModel
import com.example.splitmoney.viewModels.ExpenseViewModel
import com.example.splitmoney.viewModels.GroupUserViewModel
import com.example.splitmoney.viewModels.GroupViewModel
import com.example.splitmoney.viewModels.UserViewModel
import com.example.splitmoney.views.RootNavigation
import android.Manifest // 追加
import android.content.pm.PackageManager // 追加
import android.os.Build // 追加
import android.util.Log // 追加
import android.widget.Toast // 追加
import com.google.android.gms.tasks.OnCompleteListener // 追加
import com.google.firebase.messaging.FirebaseMessaging // 追加
import androidx.activity.result.contract.ActivityResultContracts // 追加
import androidx.core.content.ContextCompat // 追加
import androidx.compose.runtime.LaunchedEffect // 追加
import androidx.activity.compose.rememberLauncherForActivityResult // これが必要です
import androidx.compose.ui.platform.LocalContext // これが必要です

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SplitMoneyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val groupViewModel: GroupViewModel = viewModel()
                    val expenseShareViewModel: ExpenseShareViewModel = viewModel()
                    val expenseViewModel: ExpenseViewModel = viewModel()
                    val groupUserViewModel: GroupUserViewModel = viewModel()
                    val userViewModel: UserViewModel = viewModel()

                    val context = LocalContext.current

                    // 1. 権限リクエストの結果を受け取るランチャーを作成（Composeの中で作る）
                    val notificationPermissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = { isGranted ->
                            if (isGranted) {
                                // 許可されたらトークン取得＆送信
                                fetchFCMToken(userViewModel)
                            } else {
                                Toast.makeText(
                                    context,
                                    "通知が許可されませんでした",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )

                    // 2. 画面表示時に権限チェックとリクエストを実行
                    LaunchedEffect(Unit) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val permission = Manifest.permission.POST_NOTIFICATIONS
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    permission
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                // 既に許可されている場合
                                fetchFCMToken(userViewModel)
                            } else {
                                // 許可されていない場合、リクエストを表示
                                notificationPermissionLauncher.launch(permission)
                            }
                        } else {
                            // Android 12以下は権限不要
                            fetchFCMToken(userViewModel)
                        }
                    }

                    // ΕΔΩ πρέπει να μπει το RootNavigation
                    RootNavigation(
                        groupViewModel,
                        expenseShareViewModel,
                        expenseViewModel,
                        groupUserViewModel,
                        userViewModel
                    )
                }
            }
        }
    }

    // トークン取得関数（userViewModelを受け取れるように引数を設定）
    private fun fetchFCMToken(userViewModel: UserViewModel) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            Log.d("FCM", "Token: $token")

            // ViewModel経由でサーバーに送信
            userViewModel.registerFcmToken(token)
        })
    }
}
