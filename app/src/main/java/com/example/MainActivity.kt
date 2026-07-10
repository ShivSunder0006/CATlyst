package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.data.AppDatabase
import com.example.repository.SessionRepository
import com.example.data.ActiveSessionPreferences
import com.example.ui.AppViewModel
import com.example.ui.AppViewModelFactory
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    val database = AppDatabase.getDatabase(this)
    val repository = SessionRepository(database.sessionDao())
    val activeSessionPreferences = ActiveSessionPreferences(this.applicationContext)
    
    val viewModel: AppViewModel by viewModels {
        AppViewModelFactory(repository, activeSessionPreferences)
    }

    setContent {
      MyApplicationTheme {
        AppNavigation(viewModel = viewModel)
      }
    }
  }
}
