package org.zhian.commander.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import org.zhian.commander.navigation.NiriCloseEasing
import org.zhian.commander.navigation.NiriCloseDurationMillis

@Composable
fun SplashScreen(
    onNavigateToTerminal: () -> Unit = {}
) {
    val alpha = remember { Animatable(1f) }
    val color = MaterialTheme.colorScheme.onBackground

    LaunchedEffect(Unit) {
        // Wait a bit, then fade out and navigate
        kotlinx.coroutines.delay(1000)
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = NiriCloseDurationMillis,
                easing = NiriCloseEasing
            )
        )
        onNavigateToTerminal()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(120.dp)) {
            val width = size.width
            val height = size.height
            val strokeWidth = 8.dp.toPx()

            // Draw custom vector path representing "<>"
            val path = Path().apply {
                // Left angle bracket "<"
                moveTo(width * 0.35f, height * 0.25f)
                lineTo(width * 0.15f, height * 0.5f)
                lineTo(width * 0.35f, height * 0.75f)

                // Right angle bracket ">"
                moveTo(width * 0.65f, height * 0.25f)
                lineTo(width * 0.85f, height * 0.5f)
                lineTo(width * 0.65f, height * 0.75f)
            }

            drawPath(
                path = path,
                color = color.copy(alpha = alpha.value),
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}
