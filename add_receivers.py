import re

with open("app/src/main/AndroidManifest.xml", "r") as f:
    content = f.read()

widgets = [
    ("TodayProgress", "today_progress"),
    ("CurrentSession", "current_session"),
    ("QuickStats", "quick_stats"),
    ("QuickActions", "quick_actions"),
    ("MonthlyHeatmap", "monthly_heatmap"),
    ("WeeklySummary", "weekly_summary"),
    ("Motivation", "motivation")
]

receivers = ""
for class_prefix, file_suffix in widgets:
    receivers += f"""
        <receiver
            android:name=".widgets.{class_prefix}WidgetReceiver"
            android:exported="true"
            android:label="@string/widget_{file_suffix}_name">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
            </intent-filter>
            <meta-data
                android:name="android.appwidget.provider"
                android:resource="@xml/widget_{file_suffix}_info" />
        </receiver>
"""

content = content.replace('</application>', receivers + '    </application>')

with open("app/src/main/AndroidManifest.xml", "w") as f:
    f.write(content)
