with open("app/src/main/java/com/example/widgets/DashboardWidget.kt", "r") as f:
    content = f.read()

content = content.replace("@androidx.compose.runtime.Composable\nfun DashboardStatItem", "@androidx.compose.runtime.Composable\nfun DashboardStatItem")

with open("app/src/main/java/com/example/widgets/DashboardWidget.kt", "w") as f:
    f.write(content)
