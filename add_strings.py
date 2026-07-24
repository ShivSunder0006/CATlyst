with open("app/src/main/res/values/strings.xml", "r") as f:
    content = f.read()

strings_to_add = """
    <string name="widget_today_progress_name">Today\'s Progress</string>
    <string name="widget_today_progress_desc">View your daily solved questions and goal progress.</string>
    <string name="widget_current_session_name">Current Session</string>
    <string name="widget_current_session_desc">Quick glance at your active practice session.</string>
    <string name="widget_quick_stats_name">Quick Stats</string>
    <string name="widget_quick_stats_desc">Your CAT prep statistics at a glance.</string>
    <string name="widget_quick_actions_name">Quick Actions</string>
    <string name="widget_quick_actions_desc">Shortcuts to start a session, view history, and more.</string>
    <string name="widget_monthly_heatmap_name">Monthly Heatmap</string>
    <string name="widget_monthly_heatmap_desc">Your practice consistency calendar.</string>
    <string name="widget_weekly_summary_name">Weekly Summary</string>
    <string name="widget_weekly_summary_desc">Overview of your performance this week.</string>
    <string name="widget_motivation_name">Motivation</string>
    <string name="widget_motivation_desc">Daily prep progress and a motivational tip.</string>
"""

content = content.replace('</resources>', strings_to_add + '</resources>')

with open("app/src/main/res/values/strings.xml", "w") as f:
    f.write(content)
