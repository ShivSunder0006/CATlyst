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

import android.provider.Settings
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.example.ui.theme.AppThemeType

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
      val themeString by viewModel.currentTheme.collectAsState()
      val useSystemTheme by viewModel.useSystemTheme.collectAsState()
      val themeType = try {
          AppThemeType.valueOf(themeString)
      } catch (e: Exception) {
          AppThemeType.PASTEL
      }
      
      val context = LocalContext.current
      val transitionScale = Settings.Global.getFloat(context.contentResolver, Settings.Global.TRANSITION_ANIMATION_SCALE, 1f)
      val animatorScale = Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
      val isReducedMotion = transitionScale == 0f || animatorScale == 0f

      val hapticsEnabled by viewModel.hapticsEnabled.collectAsState()
      val systemHaptic = androidx.compose.ui.platform.LocalHapticFeedback.current

      MyApplicationTheme(themeType = themeType, isReducedMotion = isReducedMotion, darkTheme = if (useSystemTheme) androidx.compose.foundation.isSystemInDarkTheme() else false) {
        androidx.compose.runtime.CompositionLocalProvider(
            androidx.compose.ui.platform.LocalHapticFeedback provides com.example.ui.navigation.CustomHapticFeedback(systemHaptic, hapticsEnabled)
        ) {
            AppNavigation(viewModel = viewModel)
        }
      }
    }
  }
}
