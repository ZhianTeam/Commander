package org.zhian.commander.ui.l10n

import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

data class Strings(
  // val ...
)

/* private val English, Japanese, SimplifiedChinese, TraditionalChinese... */

private val TraditionalChineseRegions = setOf("TW", "HK", "MO")

fun stringsFor(locale: Locale): Strings = when (locale.language) {
    "zh" -> when {
        locale.script.equals("Hant", ignoreCase = true) -> TraditionalChinese
        locale.script.equals("Hans", ignoreCase = true) -> SimplifiedChinese
        locale.country.uppercase(Locale.ROOT) in TraditionalChineseRegions -> TraditionalChinese
        else -> SimplifiedChinese
    }
    "ja" -> Japanese
    else -> English
}

val LocalStrings = staticCompositionLocalOf { English }
