package org.zhian.commander.ui.locale

import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

data class Strings(
    val appName: String = "Commander",
    val settings: String = "Settings",
    val newSession: String = "New Session",
    val restartSession: String = "Restart Session",
)

private val English = Strings()
private val Japanese = Strings(
    appName = "コマンダー",
    settings = "設定",
    newSession = "新しいセッション",
    restartSession = "セッションを再起動",
)
private val SimplifiedChinese = Strings(
    appName = "终端",
    settings = "设置",
    newSession = "新建会话",
    restartSession = "重启会话",
)
private val TraditionalChinese = Strings(
    appName = "終端",
    settings = "設定",
    newSession = "新建會話",
    restartSession = "重啟會話",
)

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
