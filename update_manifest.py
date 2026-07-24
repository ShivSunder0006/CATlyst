with open("app/src/main/AndroidManifest.xml", "r") as f:
    content = f.read()

activity = """
        <activity
            android:name=".widgets.WidgetConfigActivity"
            android:exported="true"
            android:label="Widget Settings"
            android:theme="@style/Theme.MyApplication">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_CONFIGURE" />
            </intent-filter>
        </activity>
"""

content = content.replace('</application>', activity + '    </application>')

with open("app/src/main/AndroidManifest.xml", "w") as f:
    f.write(content)
