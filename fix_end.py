import re

for filename in ["app/src/main/java/com/example/widgets/QuickStatsWidget.kt", "app/src/main/java/com/example/widgets/WeeklySummaryWidget.kt"]:
    with open(filename, "r") as f:
        content = f.read()
    content = content.replace("@androidx.compose.runtime.Composable\n", "")
    with open(filename, "w") as f:
        f.write(content.strip() + "\n")
