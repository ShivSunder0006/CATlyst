package com.example.widgets

import android.content.Context
import android.content.Intent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.MainActivity
import com.example.data.AppDatabase
import com.example.repository.SessionRepository
import kotlinx.coroutines.flow.first
import java.util.Calendar

class DashboardWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DashboardWidget()
}

class DashboardWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val widgetPrefs = WidgetPreferences(context).getConfigFlow(id.hashCode()).first()
        val corner = widgetPrefs.cornerRadius.dp
        
        val database = AppDatabase.getDatabase(context)
        val repository = SessionRepository(database.sessionDao())
        
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfDay = cal.timeInMillis
        
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        val startOfWeek = cal.timeInMillis
        
        val todayQuestions = repository.getTotalQuestionsBetween(startOfDay, Long.MAX_VALUE).first() ?: 0
        val weekQuestions = repository.getTotalQuestionsBetween(startOfWeek, Long.MAX_VALUE).first() ?: 0
        val totalQuestions = repository.totalQuestionsSolved.first() ?: 0
        
        // Mock average and most practiced
        val averagePerDay = if (totalQuestions > 0) totalQuestions / 30 else 0
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("destination", "statistics")
        }
        
        provideContent {
            GlanceTheme {
                val size = LocalSize.current
                val isTall = size.height >= 140.dp
                
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.surface)
                        .cornerRadius(corner)
                        .clickable(actionStartActivity(intent))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dashboard",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = GlanceModifier.fillMaxWidth()
                    )
                    Spacer(modifier = GlanceModifier.height(12.dp))
                    
                    Row(modifier = GlanceModifier.fillMaxWidth().defaultWeight()) {
                        DashboardCard("Today", todayQuestions.toString(), GlanceModifier.defaultWeight())
                        Spacer(modifier = GlanceModifier.width(8.dp))
                        DashboardCard("This Week", weekQuestions.toString(), GlanceModifier.defaultWeight())
                        Spacer(modifier = GlanceModifier.width(8.dp))
                        DashboardCard("Overall", totalQuestions.toString(), GlanceModifier.defaultWeight())
                    }
                    
                    if (isTall) {
                        Spacer(modifier = GlanceModifier.height(8.dp))
                        Row(modifier = GlanceModifier.fillMaxWidth().defaultWeight()) {
                            DashboardCard("Avg / Day", averagePerDay.toString(), GlanceModifier.defaultWeight())
                            Spacer(modifier = GlanceModifier.width(8.dp))
                            DashboardCard("Best", "VARC", GlanceModifier.defaultWeight())
                        }
                    }
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
fun DashboardCard(label: String, value: String, modifier: GlanceModifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GlanceTheme.colors.secondaryContainer)
            .cornerRadius(12.dp)
            .padding(12.dp),
        horizontalAlignment = Alignment.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = value,
            style = TextStyle(
                color = GlanceTheme.colors.onSecondaryContainer,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = GlanceModifier.height(4.dp))
        Text(
            text = label,
            style = TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
