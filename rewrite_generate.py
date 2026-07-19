import re

with open('app/src/main/java/com/example/ui/theme/Theme.kt', 'r') as f:
    content = f.read()

new_func = """fun generateColorScheme(themeType: AppThemeType, isDark: Boolean): ColorScheme {
    val primary = themeType.seedColor
    val isForest = themeType == AppThemeType.FOREST
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(primary.toArgb(), hsl)

    val hue = hsl[0]
    val saturation = hsl[1]

    val secondaryHue = (hue + 30) % 360f
    val tertiaryHue = (hue + 60) % 360f

    val primaryColor = if (isForest) { if (isDark) Color(0xFF81C784) else Color(0xFF2E8B57) } else Color(ColorUtils.HSLToColor(floatArrayOf(hue, saturation, if (isDark) 0.65f else 0.45f)))
    val onPrimaryColor = if (isDark) Color(0xFF1A1A1A) else Color.White
    val primaryContainer = if (isForest) { if (isDark) Color(0xFF1B5E20) else Color(0xFFA5D6A7) } else Color(ColorUtils.HSLToColor(floatArrayOf(hue, saturation, if (isDark) 0.3f else 0.9f)))
    val onPrimaryContainer = if (isForest) { if (isDark) Color(0xFFA5D6A7) else Color(0xFF003300) } else Color(ColorUtils.HSLToColor(floatArrayOf(hue, saturation, if (isDark) 0.9f else 0.15f)))

    val secondaryColor = if (isForest) { if (isDark) Color(0xFFA1B59C) else Color(0xFF75906E) } else Color(ColorUtils.HSLToColor(floatArrayOf(secondaryHue, saturation * 0.8f, if (isDark) 0.7f else 0.45f)))
    val onSecondaryColor = if (isDark) Color(0xFF1A1A1A) else Color.White
    val secondaryContainer = if (isForest) { if (isDark) Color(0xFF3B4D36) else Color(0xFFD4E3D0) } else Color(ColorUtils.HSLToColor(floatArrayOf(secondaryHue, saturation * 0.8f, if (isDark) 0.3f else 0.9f)))
    val onSecondaryContainer = if (isForest) { if (isDark) Color(0xFFD4E3D0) else Color(0xFF1D2919) } else Color(ColorUtils.HSLToColor(floatArrayOf(secondaryHue, saturation * 0.8f, if (isDark) 0.9f else 0.15f)))

    // Accent: Warm Amber or Gold
    val tertiaryColor = if (isForest) { if (isDark) Color(0xFFFFCA28) else Color(0xFFFFA000) } else Color(ColorUtils.HSLToColor(floatArrayOf(tertiaryHue, saturation * 0.7f, if (isDark) 0.7f else 0.45f)))
    val onTertiaryColor = if (isDark) Color(0xFF1A1A1A) else Color.White
    val tertiaryContainer = if (isForest) { if (isDark) Color(0xFF8F5A00) else Color(0xFFFFECB3) } else Color(ColorUtils.HSLToColor(floatArrayOf(tertiaryHue, saturation * 0.7f, if (isDark) 0.3f else 0.9f)))
    val onTertiaryContainer = if (isForest) { if (isDark) Color(0xFFFFECB3) else Color(0xFF402500) } else Color(ColorUtils.HSLToColor(floatArrayOf(tertiaryHue, saturation * 0.7f, if (isDark) 0.9f else 0.15f)))

    val errorColor = Color(0xFFB3261E)
    val errorContainer = Color(0xFFF9DEDC)
    val onErrorColor = Color.White
    val onErrorContainer = Color(0xFF410E0B)

    val errorColorDark = Color(0xFFF2B8B5)
    val errorContainerDark = Color(0xFF8C1D18)
    val onErrorColorDark = Color(0xFF601410)
    val onErrorContainerDark = Color(0xFFF9DEDC)

    val background = if (isDark) { if (isForest) Color(0xFF171A18) else Color(0xFF121212) } else { if (isForest) Color(0xFFF9FBF9) else Color(0xFFFDFDFD) }
    val onBackground = if (isDark) Color(0xFFE3E3E3) else Color(0xFF1C1C1C)
    
    val surface = if (isDark) { if (isForest) Color(0xFF1D211F) else Color(0xFF1E1E1E) } else { if (isForest) Color(0xFFF5F8F5) else Color(0xFFFFFFFF) }
    val onSurface = if (isDark) Color(0xFFE3E3E3) else Color(0xFF1C1C1C)

    val surfaceSat = if (isForest && isDark) 0f else saturation

    val surfaceVariant = if (isForest) { if (isDark) Color(0xFF3A423C) else Color(0xFFE3EAE4) } else Color(ColorUtils.HSLToColor(floatArrayOf(hue, surfaceSat * 0.15f, if (isDark) 0.25f else 0.92f)))
    val onSurfaceVariant = if (isForest) { if (isDark) Color(0xFFB3BEB5) else Color(0xFF404943) } else Color(ColorUtils.HSLToColor(floatArrayOf(hue, surfaceSat * 0.25f, if (isDark) 0.75f else 0.35f)))

    val outline = if (isForest) { if (isDark) Color(0xFF758177) else Color(0xFF717D73) } else Color(ColorUtils.HSLToColor(floatArrayOf(hue, surfaceSat * 0.15f, if (isDark) 0.45f else 0.6f)))
    val outlineVariant = if (isForest) { if (isDark) Color(0xFF3A423C) else Color(0xFFC3CEC5) } else Color(ColorUtils.HSLToColor(floatArrayOf(hue, surfaceSat * 0.15f, if (isDark) 0.25f else 0.8f)))
    
    val surfaceContainer = if (isForest) { if (isDark) Color(0xFF1A1E1C) else Color(0xFFEFF3F0) } else Color(ColorUtils.HSLToColor(floatArrayOf(hue, surfaceSat * 0.05f, if (isDark) 0.15f else 0.96f)))
    val surfaceContainerHigh = if (isForest) { if (isDark) Color(0xFF222623) else Color(0xFFE9EEEA) } else Color(ColorUtils.HSLToColor(floatArrayOf(hue, surfaceSat * 0.05f, if (isDark) 0.18f else 0.92f)))
    val surfaceContainerHighest = if (isForest) { if (isDark) Color(0xFF282D2A) else Color(0xFFE4E9E5) } else Color(ColorUtils.HSLToColor(floatArrayOf(hue, surfaceSat * 0.05f, if (isDark) 0.22f else 0.88f)))
    val surfaceContainerLow = if (isForest) { if (isDark) Color(0xFF141715) else Color(0xFFF3F7F4) } else Color(ColorUtils.HSLToColor(floatArrayOf(hue, surfaceSat * 0.05f, if (isDark) 0.12f else 0.98f)))
    val surfaceContainerLowest = if (isForest) { if (isDark) Color(0xFF0F1110) else Color(0xFFFFFFFF) } else Color(ColorUtils.HSLToColor(floatArrayOf(hue, surfaceSat * 0.05f, if (isDark) 0.08f else 1.0f)))"""

content = re.sub(r'fun generateColorScheme\(.*?\): ColorScheme \{.*?return if \(isDark\)', new_func + '\n\n    return if (isDark)', content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/theme/Theme.kt', 'w') as f:
    f.write(content)
