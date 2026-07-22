package org.zhian.commander

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.zhian.commander.navigation.CommanderHost
import org.zhian.commander.ui.locale.LocalStrings
import org.zhian.commander.ui.locale.stringsFor
import org.zhian.commander.ui.theme.CommanderTheme
import org.zhian.commander.ui.theme.ThemeMode
import java.util.Locale

class CommanderActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)

        setContent {
            var selectedLocale by remember { mutableStateOf(Locale.getDefault()) }

            var themeMode by remember { mutableStateOf(ThemeMode.System) }
            val isDark = when (themeMode) {
                ThemeMode.Dark -> true
                ThemeMode.Light -> false
                ThemeMode.System -> isSystemInDarkTheme()
            }

            CompositionLocalProvider(LocalStrings provides stringsFor(selectedLocale)) {
                CommanderTheme(darkTheme = isDark) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        CommanderHost(
                            selectedLocale = selectedLocale,
                            onLocaleSelected = { selectedLocale = it },
                            isDarkTheme = isDark,
                            onDarkThemeChange = { wantDark ->
                                themeMode = if (wantDark) ThemeMode.Dark else ThemeMode.Light
                            }
                        )
                    }
                }
            }
        }
    }
}
