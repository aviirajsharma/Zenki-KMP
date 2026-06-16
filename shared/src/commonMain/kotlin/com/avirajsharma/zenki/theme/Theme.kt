package com.avirajsharma.zenki.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import studyTypography

private val LightStudyColorScheme = lightColorScheme(
    primary = StudyPurple40,
    onPrimary = StudyPurple100,
    primaryContainer = StudyPurple90,
    onPrimaryContainer = StudyPurple10,
    secondary = StudyIndigo40,
    onSecondary = StudyIndigo100,
    secondaryContainer = StudyIndigo90,
    onSecondaryContainer = StudyIndigo10,
    tertiary = StudyTeal40,
    onTertiary = StudyTeal100,
    tertiaryContainer = StudyTeal90,
    onTertiaryContainer = StudyTeal10,
    error = StudyError40,
    onError = StudyError100,
    errorContainer = StudyError90,
    onErrorContainer = StudyError10,
    background = StudyPurple99,
    onBackground = StudyNeutral10,
    surface = StudyPurple99,
    onSurface = StudyNeutral10,
    surfaceVariant = StudyNeutralVariant90,
    onSurfaceVariant = StudyNeutralVariant30,
    outline = StudyNeutralVariant50,
    outlineVariant = StudyNeutralVariant80,
    scrim = StudyNeutral0,
    inverseSurface = StudyNeutral20,
    inverseOnSurface = StudyNeutral95,
    inversePrimary = StudyPurple80,
    surfaceDim = StudyNeutralVariant80,
    surfaceBright = StudyPurple99,
    surfaceContainerLowest = StudyPurple100,
    surfaceContainerLow = StudyPurple99,
    surfaceContainer = StudyNeutral95,
    surfaceContainerHigh = StudyNeutral90,
    surfaceContainerHighest = StudyNeutralVariant90,
    surfaceTint = StudyPurple40,
)

private val DarkStudyColorScheme = darkColorScheme(
    primary = StudyPurple80,
    onPrimary = StudyPurple20,
    primaryContainer = StudyPurple30,
    onPrimaryContainer = StudyPurple90,
    secondary = StudyIndigo80,
    onSecondary = StudyIndigo20,
    secondaryContainer = StudyIndigo30,
    onSecondaryContainer = StudyIndigo90,
    tertiary = StudyTeal80,
    onTertiary = StudyTeal20,
    tertiaryContainer = StudyTeal30,
    onTertiaryContainer = StudyTeal90,
    error = StudyError80,
    onError = StudyError20,
    errorContainer = StudyError30,
    onErrorContainer = StudyError90,
    background = StudyNeutral10,
    onBackground = StudyNeutral90,
    surface = StudyNeutral10,
    onSurface = StudyNeutral90,
    surfaceVariant = StudyNeutralVariant30,
    onSurfaceVariant = StudyNeutralVariant80,
    outline = StudyNeutralVariant60,
    outlineVariant = StudyNeutralVariant30,
    scrim = StudyNeutral0,
    inverseSurface = StudyNeutral90,
    inverseOnSurface = StudyNeutral10,
    inversePrimary = StudyPurple40,
    surfaceDim = StudyNeutral10,
    surfaceBright = StudyNeutral20,
    surfaceContainerLowest = StudyNeutral0,
    surfaceContainerLow = StudyNeutral10,
    surfaceContainer = StudyNeutralVariant10,
    surfaceContainerHigh = StudyNeutral20,
    surfaceContainerHighest = StudyNeutralVariant20,
    surfaceTint = StudyPurple80,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppTheme(
    themeMode: ThemeMode = ThemeMode.System,
    content: @Composable () -> Unit,
) {
    val darkTheme = resolveDarkTheme(themeMode)
    val colorScheme = selectColorScheme(darkTheme)
    val typography = studyTypography()

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = StudyShapes,
        motionScheme = MotionScheme.expressive(),
        content = content,
    )
}

@Composable
private fun resolveDarkTheme(themeMode: ThemeMode): Boolean =
    when (themeMode) {
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
        ThemeMode.System -> isSystemInDarkTheme()
    }

private fun selectColorScheme(darkTheme: Boolean): ColorScheme =
    if (darkTheme) DarkStudyColorScheme else LightStudyColorScheme
