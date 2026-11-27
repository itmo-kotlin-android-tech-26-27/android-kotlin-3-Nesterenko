package ru.performanceugc.lr3.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale
import ru.performanceugc.lr3.domain.model.Category
import ru.performanceugc.lr3.domain.model.Creator
import ru.performanceugc.lr3.domain.model.Rubles

val Paper = Color(0xFFF6F5F9)

val Ink = Color(0xFF191529)

val Muted = Color(0xFF635E73)

val Violet = Color(0xFF6035D5)

val Pale = Color(0xFFEEE8FF)

val Line = Color(0xFFE4E0EC)

val White = Color.White

val Mint = Color(0xFFDBF6E9)

val Pine = Color(0xFF14563C)

val Night = Color(0xFF211638)

private fun type(size: Int, line: Int, weight: FontWeight = FontWeight.Normal, tracking: Double = 0.0) =
    TextStyle(fontSize = size.sp, lineHeight = line.sp, fontWeight = weight, letterSpacing = tracking.sp)

private val editorialType = Typography(
    displaySmall = type(42, 44, FontWeight.ExtraBold, -1.6),
    headlineLarge = type(34, 38, FontWeight.ExtraBold, -1.1),
    headlineMedium = type(27, 32, FontWeight.Bold, -0.55),
    titleLarge = type(21, 27, FontWeight.Bold, -0.25),
    titleMedium = type(16, 22, FontWeight.Bold, -0.1),
    bodyLarge = type(16, 24),
    bodyMedium = type(14, 20),
    bodySmall = type(12, 17),
    labelLarge = type(15, 20, FontWeight.Bold),
    labelMedium = type(12, 16, FontWeight.SemiBold),
)

@Composable
fun StudioTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Violet, onPrimary = White, primaryContainer = Pale, onPrimaryContainer = Ink,
            background = Paper, onBackground = Ink, surface = White, onSurface = Ink,
            onSurfaceVariant = Muted, outline = Line, error = Color(0xFF9F263D),
        ),
        typography = editorialType,
        content = content,
    )
}

fun rubles(value: Rubles): String = "${NumberFormat.getIntegerInstance(Locale.forLanguageTag("ru-RU")).format(value.amount)} ₽"

@Composable
fun StudioGlyph(glyph: Glyph, color: Color, modifier: Modifier = Modifier.size(22.dp)) {
    Canvas(modifier) {
        val step = size.minDimension / 24f
        val stroke = Stroke(width = 1.75f * step, cap = StrokeCap.Round, join = StrokeJoin.Round)
        fun line(vararg points: Pair<Int, Int>) {
            val path = Path()
            points.forEachIndexed { index, (x, y) ->
                if (index == 0) path.moveTo(x * step, y * step) else path.lineTo(x * step, y * step)
            }
            drawPath(path, color, style = stroke)
        }
        fun circle(x: Int, y: Int, radius: Float, style: Stroke = stroke) {
            drawCircle(color, radius * step, Offset(x * step, y * step), style = style)
        }
        when (glyph) {
            Glyph.BACK -> { line(13 to 5, 6 to 12, 13 to 19); line(6 to 12, 20 to 12) }
            Glyph.FORWARD -> { line(11 to 5, 18 to 12, 11 to 19); line(4 to 12, 18 to 12) }
            Glyph.PLUS -> { line(5 to 12, 19 to 12); line(12 to 5, 12 to 19) }
            Glyph.SEARCH -> { circle(10, 10, 5.5f); line(14 to 14, 20 to 20) }
            Glyph.CHECK -> line(5 to 12, 10 to 17, 19 to 7)
            Glyph.CLOSE -> { line(6 to 6, 18 to 18); line(6 to 18, 18 to 6) }
            Glyph.BRIEF -> {
                line(7 to 3, 16 to 3, 19 to 6, 19 to 21, 5 to 21, 5 to 5, 7 to 3)
                line(8 to 11, 16 to 11); line(8 to 15, 14 to 15)
            }
            Glyph.MARK -> {
                line(12 to 3, 12 to 21); line(3 to 12, 21 to 12)
                line(6 to 6, 18 to 18); line(6 to 18, 18 to 6)
            }
        }
    }
}

@Composable
fun StudioHeader(title: String, step: String?, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.semantics { contentDescription = "Назад" },
        ) { StudioGlyph(Glyph.BACK, Ink) }
        Text(title, style = MaterialTheme.typography.titleMedium, color = Ink, modifier = Modifier.weight(1f))
        if (step != null) Badge(step, Pale, Violet)
        Spacer(Modifier.width(6.dp))
    }
}

@Composable
fun Badge(text: String, background: Color = Pale, color: Color = Violet) {
    Text(
        text,
        color = color,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.clip(CircleShape).background(background).padding(horizontal = 11.dp, vertical = 6.dp),
    )
}

@Composable
fun StudioButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    glyph: Glyph = Glyph.FORWARD,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick, enabled = enabled,
        modifier = modifier.fillMaxWidth().heightIn(min = 54.dp),
        shape = RoundedCornerShape(17.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Violet, contentColor = White,
            disabledContainerColor = Line, disabledContentColor = Muted,
        ),
        contentPadding = PaddingValues(horizontal = 17.dp, vertical = 12.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            StudioGlyph(glyph, if (enabled) White else Muted)
        }
    }
}

@Composable
fun Avatar(creator: Creator, size: Int = 52) {
    val (background, foreground) = when (creator.category) {
        Category.BEAUTY -> Color(0xFFF9DEEA) to Color(0xFF843D5E)
        Category.FOOD -> Color(0xFFFFE8D4) to Color(0xFF81451D)
        Category.LIFESTYLE -> Color(0xFFE0F1E8) to Color(0xFF27634C)
        Category.TECH -> Color(0xFFE5DDFF) to Color(0xFF5831A0)
    }
    Box(
        Modifier.size(size.dp).clip(RoundedCornerShape((size / 3).dp)).background(background),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            creator.name.split(' ').joinToString("") { it.take(1) },
            color = foreground,
            style = if (size > 60) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleMedium,
        )
    }
}

fun Modifier.studioCard(radius: Int = 21): Modifier {
    val shape = RoundedCornerShape(radius.dp)
    return clip(shape).background(White).border(1.dp, Line, shape)
}
