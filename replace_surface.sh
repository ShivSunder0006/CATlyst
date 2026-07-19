sed -i 's/val surfaceVariant = Color/val surfaceSat = if (isMidnight \&\& isDark) 0f else saturation\n    val surfaceVariant = Color/' app/src/main/java/com/example/ui/theme/Theme.kt
sed -i 's/saturation \* 0\.15f/surfaceSat * 0.15f/g' app/src/main/java/com/example/ui/theme/Theme.kt
sed -i 's/saturation \* 0\.25f/surfaceSat * 0.25f/g' app/src/main/java/com/example/ui/theme/Theme.kt
sed -i 's/saturation \* 0\.05f/surfaceSat * 0.05f/g' app/src/main/java/com/example/ui/theme/Theme.kt
