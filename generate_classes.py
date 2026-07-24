import os

widgets = [
    ("TodayProgress", "Today's Progress"),
    ("CurrentSession", "Current Session"),
    ("QuickStats", "Quick Stats"),
    ("QuickActions", "Quick Actions"),
    ("MonthlyHeatmap", "Monthly Heatmap"),
    ("WeeklySummary", "Weekly Summary"),
    ("Motivation", "Motivation")
]

src_dir = "app/src/main/java/com/example/widgets"

for class_prefix, label in widgets:
    content = f"""package com.example.widgets

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.background
import androidx.glance.layout.padding
import androidx.glance.appwidget.cornerRadius

class {class_prefix}WidgetReceiver : GlanceAppWidgetReceiver() {{
    override val glanceAppWidget: GlanceAppWidget = {class_prefix}Widget()
}}

class {class_prefix}Widget : GlanceAppWidget() {{
    override suspend fun provideGlance(context: Context, id: GlanceId) {{
        provideContent {{
            Column(
                modifier = GlanceModifier.fillMaxSize()
                    .background(androidx.compose.ui.graphics.Color(0xFF1E1E1E))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {{
                Text(
                    text = "{label} Widget",
                    style = TextStyle(color = androidx.glance.unit.ColorProvider(androidx.compose.ui.graphics.Color.White))
                )
            }}
        }}
    }}
}}
"""
    with open(f"{src_dir}/{class_prefix}Widget.kt", "w") as f:
        f.write(content)

print("Generated Classes")
