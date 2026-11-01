@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.sabix.vpn

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// ───────────────────────── Icons (drawn, no extra dependency) ─────────────────────────

enum class Ico {
    BACK, SETTINGS, LOGS, POWER, CHECK, CHEVRON, LOCK, DOWN, UP, BOLT, GLOBE, REFRESH,
    COPY, SHARE, TRASH, INFO, CODE, SEND, PAUSE, PLAY
}

@Composable
fun Icon8(ico: Ico, tint: Color, sz: Dp = 22.dp, modifier: Modifier = Modifier, strokeW: Float = 2f) {
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Canvas(modifier.size(sz)) {
        val u = this.size.width / 24f
        val flip = rtl && (ico == Ico.BACK)
        fun p(x: Float, y: Float) = Offset((if (flip) 24f - x else x) * u, y * u)
        val st = Stroke(width = strokeW * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
        fun line(x1: Float, y1: Float, x2: Float, y2: Float) =
            drawLine(tint, p(x1, y1), p(x2, y2), strokeWidth = strokeW * u, cap = StrokeCap.Round)
        fun poly(close: Boolean, vararg pts: Float) {
            val path = Path()
            path.moveTo(p(pts[0], pts[1]).x, p(pts[0], pts[1]).y)
            var i = 2
            while (i < pts.size) {
                path.lineTo(p(pts[i], pts[i + 1]).x, p(pts[i], pts[i + 1]).y)
                i += 2
            }
            if (close) path.close()
            drawPath(path, tint, style = st)
        }
        when (ico) {
            Ico.BACK -> { line(20f, 12f, 5f, 12f); poly(false, 11f, 6f, 5f, 12f, 11f, 18f) }
            Ico.SETTINGS -> {
                line(4f, 7f, 20f, 7f); line(4f, 12f, 20f, 12f); line(4f, 17f, 20f, 17f)
                drawCircle(tint, radius = 2.6f * u, center = p(9f, 7f))
                drawCircle(tint, radius = 2.6f * u, center = p(15f, 12f))
                drawCircle(tint, radius = 2.6f * u, center = p(8f, 17f))
            }
            Ico.LOGS -> {
                drawRoundRect(tint, topLeft = p(5f, 3.5f), size = Size(14f * u, 17f * u), cornerRadius = CornerRadius(3.5f * u), style = st)
                line(8.5f, 9f, 15.5f, 9f); line(8.5f, 13f, 15.5f, 13f); line(8.5f, 17f, 12.5f, 17f)
            }
            Ico.POWER -> {
                drawArc(tint, startAngle = -55f, sweepAngle = 290f, useCenter = false, topLeft = p(4.5f, 5.5f), size = Size(15f * u, 15f * u), style = st)
                line(12f, 3f, 12f, 12f)
            }
            Ico.CHECK -> poly(false, 5f, 12.5f, 10f, 17f, 19f, 7f)
            Ico.CHEVRON -> poly(false, 6f, 9f, 12f, 15f, 18f, 9f)
            Ico.LOCK -> {
                drawRoundRect(tint, topLeft = p(5.5f, 11f), size = Size(13f * u, 9.5f * u), cornerRadius = CornerRadius(3f * u), style = st)
                drawArc(tint, startAngle = 180f, sweepAngle = 180f, useCenter = false, topLeft = p(8.5f, 4.5f), size = Size(7f * u, 7f * u), style = st)
                line(8.5f, 8f, 8.5f, 11f); line(15.5f, 8f, 15.5f, 11f)
            }
            Ico.DOWN -> { line(12f, 4f, 12f, 16f); poly(false, 6f, 11f, 12f, 17f, 18f, 11f); line(5f, 21f, 19f, 21f) }
            Ico.UP -> { line(12f, 20f, 12f, 8f); poly(false, 6f, 13f, 12f, 7f, 18f, 13f); line(5f, 3f, 19f, 3f) }
            Ico.BOLT -> poly(true, 13f, 3f, 5f, 13.5f, 11f, 13.5f, 10f, 21f, 19f, 10f, 13f, 10f)
            Ico.GLOBE -> {
                drawCircle(tint, radius = 9f * u, center = p(12f, 12f), style = st)
                drawOval(tint, topLeft = p(8f, 3f), size = Size(8f * u, 18f * u), style = st)
                line(3f, 12f, 21f, 12f)
            }
            Ico.REFRESH -> {
                drawArc(tint, startAngle = 0f, sweepAngle = 270f, useCenter = false, topLeft = p(4.5f, 5.5f), size = Size(15f * u, 15f * u), style = st)
                poly(false, 8.5f, 2.5f, 12f, 5.5f, 8.5f, 8.5f)
            }
            Ico.COPY -> {
                drawRoundRect(tint, topLeft = p(8.5f, 8.5f), size = Size(11f * u, 11f * u), cornerRadius = CornerRadius(2.5f * u), style = st)
                poly(false, 15f, 5f, 5f, 5f, 5f, 15f, 8f, 15f)
            }
            Ico.SHARE -> {
                drawCircle(tint, radius = 2.6f * u, center = p(6f, 12f), style = st)
                drawCircle(tint, radius = 2.6f * u, center = p(18f, 6f), style = st)
                drawCircle(tint, radius = 2.6f * u, center = p(18f, 18f), style = st)
                line(8.2f, 10.8f, 15.8f, 7.2f); line(8.2f, 13.2f, 15.8f, 16.8f)
            }
            Ico.TRASH -> {
                line(4f, 7f, 20f, 7f)
                poly(false, 6f, 7f, 7f, 20f, 17f, 20f, 18f, 7f)
                poly(false, 9f, 7f, 9f, 4f, 15f, 4f, 15f, 7f)
                line(10f, 11f, 10f, 16f); line(14f, 11f, 14f, 16f)
            }
            Ico.INFO -> {
                drawCircle(tint, radius = 9f * u, center = p(12f, 12f), style = st)
                drawCircle(tint, radius = 1.2f * u, center = p(12f, 7.8f))
                line(12f, 11.5f, 12f, 16.5f)
            }
            Ico.CODE -> { poly(false, 8f, 7f, 3f, 12f, 8f, 17f); poly(false, 16f, 7f, 21f, 12f, 16f, 17f); line(13.5f, 5f, 10.5f, 19f) }
            Ico.PAUSE -> { line(9f, 5f, 9f, 19f); line(15f, 5f, 15f, 19f) }
            Ico.PLAY -> poly(true, 8f, 5f, 19f, 12f, 8f, 19f)
            Ico.SEND -> { poly(true, 21f, 3f, 3f, 10.5f, 10f, 13f, 13f, 20f); line(10f, 13f, 21f, 3f) }
        }
    }
}

// ───────────────────────── SABI-X brand ─────────────────────────

/** Pointy-top hexagon around (cx, cy) with circumradius r. */
fun hexPath(cx: Float, cy: Float, r: Float, path: Path = Path()): Path {
    for (i in 0 until 6) {
        val a = Math.toRadians(60.0 * i - 90.0)
        val px = cx + r * cos(a).toFloat()
        val py = cy + r * sin(a).toFloat()
        if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
    }
    path.close()
    return path
}

val HexShape = GenericShape { size, _ ->
    hexPath(size.width / 2f, size.height / 2f, min(size.width, size.height) / 2f, this)
}

/** Paints the drawn content (text) with a brush. */
fun Modifier.brushText(brush: Brush): Modifier = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithCache { onDrawWithContent { drawContent(); drawRect(brush, blendMode = BlendMode.SrcAtop) } }

/** Hexagon mark with a neon "X". */
@Composable
fun LogoMark(sz: Dp) {
    val x = LocalExtra.current
    Canvas(Modifier.size(sz)) {
        val r = this.size.minDimension / 2f
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        drawPath(hexPath(cx, cy, r), x.tri)
        drawPath(hexPath(cx, cy, r * 0.84f), Color(0xFF0B0916))
        val s = r * 0.33f
        val w = r * 0.17f
        drawLine(
            Brush.linearGradient(listOf(x.a, x.b), Offset(cx - s, cy - s), Offset(cx + s, cy + s)),
            Offset(cx - s, cy - s), Offset(cx + s, cy + s), strokeWidth = w, cap = StrokeCap.Round
        )
        drawLine(
            Brush.linearGradient(listOf(x.c, x.b), Offset(cx + s, cy - s), Offset(cx - s, cy + s)),
            Offset(cx + s, cy - s), Offset(cx - s, cy + s), strokeWidth = w, cap = StrokeCap.Round
        )
    }
}

/** "SABI-X" wordmark; always left-to-right, "-X" painted with the neon gradient. */
@Composable
fun Wordmark(size: TextUnit, modifier: Modifier = Modifier) {
    val x = LocalExtra.current
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(modifier, verticalAlignment = Alignment.CenterVertically) {
            Text(
                "SABI", fontSize = size, fontWeight = FontWeight.Black,
                letterSpacing = (size.value * 0.10f).sp, color = MaterialTheme.colorScheme.onSurface, maxLines = 1
            )
            Text(
                "-X", fontSize = size, fontWeight = FontWeight.Black, letterSpacing = (size.value * 0.04f).sp,
                color = Color.White, maxLines = 1, modifier = Modifier.brushText(x.tri)
            )
        }
    }
}

/** Animated aurora glow + dot grid behind every screen. */
@Composable
fun Backdrop() {
    val x = LocalExtra.current
    val cs = MaterialTheme.colorScheme
    val dark = cs.background.luminance() < 0.5f
    val inf = rememberInfiniteTransition(label = "bg")
    val t by inf.animateFloat(
        0f, 1f, infiniteRepeatable(tween(16000, easing = LinearEasing), RepeatMode.Reverse), label = "bgT"
    )
    val dot = cs.onSurface.copy(alpha = if (dark) 0.05f else 0.07f)
    Canvas(Modifier.fillMaxSize()) {
        val w = this.size.width
        val h = this.size.height
        val k = if (dark) 1f else 0.6f
        fun blob(col: Color, cx: Float, cy: Float, r: Float, a: Float) {
            val ctr = Offset(cx, cy)
            drawCircle(
                Brush.radialGradient(listOf(col.copy(alpha = a * k), Color.Transparent), center = ctr, radius = r),
                radius = r, center = ctr
            )
        }
        blob(x.a, w * (0.10f + 0.30f * t), h * 0.10f, w * 0.85f, 0.20f)
        blob(x.b, w * (0.95f - 0.35f * t), h * (0.30f + 0.05f * t), w * 0.80f, 0.22f)
        blob(x.c, w * (0.30f + 0.40f * t), h * 0.95f, w * 0.90f, 0.12f)
        val step = 26.dp.toPx()
        val rr = 0.9.dp.toPx()
        var yy = step / 2f
        while (yy < h) {
            var xx = step / 2f
            while (xx < w) {
                drawCircle(dot, rr, Offset(xx, yy))
                xx += step
            }
            yy += step
        }
    }
}

@Composable
fun IconBtn(ico: Ico, onClick: () -> Unit, enabled: Boolean = true, tint: Color = MaterialTheme.colorScheme.onSurface) {
    val c = MaterialTheme.colorScheme
    val x = LocalExtra.current
    val shape = RoundedCornerShape(14.dp)
    Box(
        Modifier.size(42.dp).clip(shape).background(c.surfaceContainerHigh.copy(alpha = 0.85f))
            .border(1.dp, Brush.linearGradient(listOf(x.a.copy(alpha = 0.55f), x.b.copy(alpha = 0.15f), x.c.copy(alpha = 0.45f))), shape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) { Icon8(ico, tint, 20.dp) }
}

// ───────────────────────── Containers ─────────────────────────

@Composable
fun AppCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val c = MaterialTheme.colorScheme
    val x = LocalExtra.current
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier.fillMaxWidth().clip(shape).background(c.surfaceContainer.copy(alpha = 0.80f))
            .border(
                1.dp,
                Brush.linearGradient(listOf(x.a.copy(alpha = 0.30f), c.outlineVariant, c.outlineVariant, x.c.copy(alpha = 0.25f))),
                shape
            ),
        content = content
    )
}

/** A titled settings group: small heading + one card holding the rows. */
@Composable
fun Group(title: String, content: @Composable ColumnScope.() -> Unit) {
    SectionTitle(title)
    AppCard {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp), content = content)
    }
}

@Composable
fun Field(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Label(title)
        content()
    }
}

/** Page frame: safe-area padding, round back button, title and optional actions. */
@Composable
fun Page(title: String, onBack: () -> Unit, actions: @Composable RowScope.() -> Unit = {}, content: @Composable () -> Unit) {
    Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(horizontal = 16.dp)) {
        Row(Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconBtn(Ico.BACK, onBack)
            Spacer(Modifier.width(12.dp))
            Text(
                title, Modifier.weight(1f), style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) { actions() }
        }
        content()
    }
}

// ───────────────────────── Text ─────────────────────────

@Composable
fun Label(text: String) {
    Text(text, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
fun SectionTitle(text: String) {
    Row(Modifier.padding(start = 4.dp, top = 22.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(4.dp).height(14.dp).clip(RoundedCornerShape(2.dp)).background(LocalExtra.current.gradient))
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun Hint(text: String, error: Boolean = false) {
    Text(
        text, style = MaterialTheme.typography.bodySmall,
        color = if (error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
    )
}

/** Soft tinted note with an info icon. */
@Composable
fun Banner(text: String) {
    val c = MaterialTheme.colorScheme
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(c.primary.copy(alpha = 0.10f)).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon8(Ico.INFO, c.primary, 20.dp)
        Spacer(Modifier.width(10.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = c.onSurface)
    }
}

// ───────────────────────── Selection ─────────────────────────

@Composable
fun Pill(text: String, selected: Boolean, enabled: Boolean = true, onClick: () -> Unit) {
    val c = MaterialTheme.colorScheme
    val x = LocalExtra.current
    val shape = RoundedCornerShape(12.dp)
    val selA by animateFloatAsState(if (selected) 1f else 0f, label = "pillA")
    val fg = if (selected) c.onSurface else c.onSurfaceVariant
    Box(
        Modifier.alpha(if (enabled) 1f else 0.5f).clip(shape).background(c.surfaceContainerHigh)
            .border(1.dp, if (selected) x.a.copy(alpha = 0.55f) else c.outlineVariant, shape)
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Box(
            Modifier.matchParentSize().alpha(selA)
                .background(Brush.linearGradient(listOf(x.a.copy(alpha = 0.18f), x.b.copy(alpha = 0.26f))))
        )
        Row(Modifier.padding(horizontal = 14.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
            if (selected) {
                Icon8(Ico.CHECK, x.a, 14.dp, strokeW = 2.6f)
                Spacer(Modifier.width(6.dp))
            }
            Text(text, color = fg, fontSize = 13.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
        }
    }
}

@Composable
fun <T> Chips(options: List<Pair<T, String>>, selected: T, enabled: Boolean = true, onSelect: (T) -> Unit) {
    // Wraps onto the next line instead of running off the screen.
    FlowRow(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (v, label) -> Pill(label, v == selected, enabled) { onSelect(v) } }
    }
}

/** Full-width segmented switch (2–3 options) used on the home screen. */
@Composable
fun <T> Segmented(options: List<Pair<T, String>>, selected: T, enabled: Boolean = true, onSelect: (T) -> Unit) {
    val c = MaterialTheme.colorScheme
    val x = LocalExtra.current
    val shape = RoundedCornerShape(16.dp)
    Row(
        Modifier.fillMaxWidth().alpha(if (enabled) 1f else 0.6f).clip(shape)
            .background(c.surfaceContainerHigh).border(1.dp, c.outlineVariant, shape).padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEach { (v, label) ->
            val sel = v == selected
            val selA by animateFloatAsState(if (sel) 1f else 0f, label = "segA")
            Box(
                Modifier.weight(1f).height(40.dp).clip(RoundedCornerShape(12.dp))
                    .clickable(enabled = enabled) { onSelect(v) },
                contentAlignment = Alignment.Center
            ) {
                Box(Modifier.matchParentSize().alpha(selA).background(x.hot))
                Text(
                    label, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis, fontSize = 13.sp,
                    fontWeight = if (sel) FontWeight.Bold else FontWeight.Medium,
                    color = if (sel) Color.White else c.onSurfaceVariant
                )
            }
        }
    }
}

// ───────────────────────── Buttons ─────────────────────────

@Composable
fun TonalPill(ico: Ico, text: String, enabled: Boolean = true, danger: Boolean = false, onClick: () -> Unit) {
    val c = MaterialTheme.colorScheme
    val tone = if (danger) c.error else c.primary
    Row(
        Modifier.alpha(if (enabled) 1f else 0.5f).clip(RoundedCornerShape(14.dp)).background(tone.copy(alpha = 0.13f))
            .clickable(enabled = enabled, onClick = onClick).padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon8(ico, tone, 18.dp)
        Spacer(Modifier.width(8.dp))
        Text(text, color = tone, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

/** Full-width action row used in settings (battery, logs, clear cache…). */
@Composable
fun ActionRow(ico: Ico, text: String, enabled: Boolean = true, danger: Boolean = false, onClick: () -> Unit) {
    val c = MaterialTheme.colorScheme
    val tone = if (danger) c.error else c.onSurface
    Row(
        Modifier.fillMaxWidth().alpha(if (enabled) 1f else 0.5f).clip(RoundedCornerShape(16.dp))
            .background(c.surfaceContainerHigh).clickable(enabled = enabled, onClick = onClick).padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon8(ico, if (danger) c.error else c.primary, 20.dp)
        Spacer(Modifier.width(12.dp))
        Text(text, Modifier.weight(1f), color = tone, style = MaterialTheme.typography.bodyMedium)
    }
}

// ───────────────────────── Inputs ─────────────────────────

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = Color.Transparent,
    errorContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
)

@Composable
fun SearchField(value: String, onChange: (String) -> Unit, hint: String) {
    OutlinedTextField(
        value = value, onValueChange = onChange, singleLine = true, shape = RoundedCornerShape(16.dp),
        placeholder = { Text(hint) }, colors = fieldColors(), modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SwitchRow(title: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { onChange(!checked) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, Modifier.weight(1f).padding(end = 12.dp), style = MaterialTheme.typography.bodyMedium)
        Switch(
            checked = checked, onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
            )
        )
    }
}

@Composable
fun TextRow(title: String, value: String, hint: String = "", singleLine: Boolean = true, isError: Boolean = false, onChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Label(title)
        OutlinedTextField(
            value = value, onValueChange = onChange, singleLine = singleLine, isError = isError,
            shape = RoundedCornerShape(14.dp), colors = fieldColors(),
            placeholder = { if (hint.isNotEmpty()) Text(hint) }, modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun NumberRow(title: String, value: Int, onChange: (Int) -> Unit) {
    var t by remember(value) { mutableStateOf(value.toString()) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Label(title)
        OutlinedTextField(
            value = t,
            onValueChange = { v ->
                t = v.filter { it.isDigit() }.take(5)
                t.toIntOrNull()?.let(onChange)
            },
            singleLine = true, shape = RoundedCornerShape(14.dp), colors = fieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Port field. The text is kept exactly as typed and a value is saved only once it is a valid, free
 * port, so a half-typed number never gets rewritten under the user's fingers.
 */
@Composable
fun PortRow(title: String, value: Int, otherPort: Int, errorText: String, onValid: (Int) -> Unit) {
    var t by remember { mutableStateOf(value.toString()) }
    val n = t.toIntOrNull()
    val bad = n == null || n !in 1024..65535 || n == TOR_PORT || n == PSIPHON_PORT || n == otherPort
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Label(title)
        OutlinedTextField(
            value = t,
            onValueChange = { v ->
                t = v.filter { it.isDigit() }.take(5)
                val x = t.toIntOrNull()
                if (x != null && x in 1024..65535 && x != TOR_PORT && x != PSIPHON_PORT && x != otherPort) onValid(x)
            },
            isError = bad,
            supportingText = if (bad) ({ Text(errorText) }) else null,
            singleLine = true, shape = RoundedCornerShape(14.dp), colors = fieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
