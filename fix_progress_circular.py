import os
with open("app/src/main/java/com/example/widgets/DailyProgressWidget.kt", "r") as f:
    content = f.read()

# I want to add CircularProgressIndicator but I need to position it correctly.
# The user asked for a Circular progress ring, Today's questions solved, Daily goal, Percentage complete.
# I can put the text INSIDE a Box that also contains the CircularProgressIndicator!
new_ui = """
                    Box(
                        modifier = GlanceModifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.glance.appwidget.CircularProgressIndicator(
                            color = GlanceTheme.colors.primary,
                            modifier = GlanceModifier.size(120.dp)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = todayQuestions.toString(),
                                style = TextStyle(
                                    color = GlanceTheme.colors.primary,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "of $dailyGoal",
                                style = TextStyle(
                                    color = GlanceTheme.colors.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
"""

content = content.replace('''Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = todayQuestions.toString(),
                                style = TextStyle(
                                    color = GlanceTheme.colors.primary,
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "of $dailyGoal",
                                style = TextStyle(
                                    color = GlanceTheme.colors.onSurfaceVariant,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                    
                    Spacer(modifier = GlanceModifier.height(24.dp))
                    
                    // Simple Linear Progress representation
                    androidx.glance.appwidget.LinearProgressIndicator(
                        progress = percent,
                        modifier = GlanceModifier.fillMaxWidth().height(12.dp).cornerRadius(6.dp),
                        color = GlanceTheme.colors.primary,
                        backgroundColor = GlanceTheme.colors.surfaceVariant
                    )''', new_ui)

with open("app/src/main/java/com/example/widgets/DailyProgressWidget.kt", "w") as f:
    f.write(content)
