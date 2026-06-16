import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import zenki.shared.generated.resources.Res
import zenki.shared.generated.resources.google_sans_bold
import zenki.shared.generated.resources.google_sans_italic
import zenki.shared.generated.resources.google_sans_medium
import zenki.shared.generated.resources.google_sans_normal
import zenki.shared.generated.resources.google_sans_semibold

@Composable
fun studyTypography(): Typography {
    val googleSans = FontFamily(
        Font(Res.font.google_sans_normal, FontWeight.Normal),
        Font(Res.font.google_sans_italic, FontWeight.Normal, FontStyle.Italic),
        Font(Res.font.google_sans_medium, FontWeight.Medium),
        Font(Res.font.google_sans_semibold, FontWeight.SemiBold),
        Font(Res.font.google_sans_bold, FontWeight.Bold),
    )

    val base = TextStyle(fontFamily = googleSans)

    return Typography(
        displayLarge = base.copy(
            fontSize = 57.sp, lineHeight = 64.sp,
            letterSpacing = (-0.25).sp, fontWeight = FontWeight.Normal,
        ),
        displayMedium = base.copy(
            fontSize = 45.sp, lineHeight = 52.sp,
            letterSpacing = 0.sp, fontWeight = FontWeight.Normal,
        ),
        displaySmall = base.copy(
            fontSize = 36.sp, lineHeight = 44.sp,
            letterSpacing = 0.sp, fontWeight = FontWeight.Normal,
        ),
        headlineLarge = base.copy(
            fontSize = 32.sp, lineHeight = 40.sp,
            letterSpacing = 0.sp, fontWeight = FontWeight.Normal,
        ),
        headlineMedium = base.copy(
            fontSize = 28.sp, lineHeight = 36.sp,
            letterSpacing = 0.sp, fontWeight = FontWeight.Normal,
        ),
        headlineSmall = base.copy(
            fontSize = 24.sp, lineHeight = 32.sp,
            letterSpacing = 0.sp, fontWeight = FontWeight.Normal,
        ),
        titleLarge = base.copy(
            fontSize = 22.sp, lineHeight = 28.sp,
            letterSpacing = 0.sp, fontWeight = FontWeight.Medium,
        ),
        titleMedium = base.copy(
            fontSize = 16.sp, lineHeight = 24.sp,
            letterSpacing = 0.15.sp, fontWeight = FontWeight.Medium,
        ),
        titleSmall = base.copy(
            fontSize = 14.sp, lineHeight = 20.sp,
            letterSpacing = 0.1.sp, fontWeight = FontWeight.Medium,
        ),
        bodyLarge = base.copy(
            fontSize = 16.sp, lineHeight = 24.sp,
            letterSpacing = 0.5.sp, fontWeight = FontWeight.Normal,
        ),
        bodyMedium = base.copy(
            fontSize = 14.sp, lineHeight = 20.sp,
            letterSpacing = 0.25.sp, fontWeight = FontWeight.Normal,
        ),
        bodySmall = base.copy(
            fontSize = 12.sp, lineHeight = 16.sp,
            letterSpacing = 0.4.sp, fontWeight = FontWeight.Normal,
        ),
        labelLarge = base.copy(
            fontSize = 14.sp, lineHeight = 20.sp,
            letterSpacing = 0.1.sp, fontWeight = FontWeight.Medium,
        ),
        labelMedium = base.copy(
            fontSize = 12.sp, lineHeight = 16.sp,
            letterSpacing = 0.5.sp, fontWeight = FontWeight.Medium,
        ),
        labelSmall = base.copy(
            fontSize = 11.sp, lineHeight = 16.sp,
            letterSpacing = 0.5.sp, fontWeight = FontWeight.Medium,
        ),
    )
}
