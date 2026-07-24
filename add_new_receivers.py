import re

with open("app/src/main/AndroidManifest.xml", "r") as f:
    content = f.read()

widgets = [
    ("DailyProgress", "daily_progress"),
    ("QuickSession", "quick_session"),
    ("Dashboard", "dashboard"),
    ("QuickLaunch", "quick_launch")
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

activity = """
        <activity
            android:name=".widgets.WidgetConfigActivity"
            android:exported="true"
            android:label="@string/widget_config_title"
            android:theme="@style/Theme.MyApplication">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_CONFIGURE" />
            </intent-filter>
        </activity>
"""

content = content.replace('</application>', receivers + activity + '    </application>')

with open("app/src/main/AndroidManifest.xml", "w") as f:
    f.write(content)
