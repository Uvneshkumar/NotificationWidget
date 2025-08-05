package uvnesh.notificationwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.widget.RemoteViews
import androidx.core.graphics.drawable.toBitmap

class NotificationWidget : AppWidgetProvider() {

    private val appListItems: MutableSet<Pair<String, Drawable>> = mutableSetOf()
    private fun loadAppIcons(context: Context, postAction: () -> Unit = {}) {
        if (appListItems.isEmpty()) {
            val appList: List<ApplicationInfo> =
                context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            for (appInfo in appList) {
                val appName: String = appInfo.packageName
                val appIcon: Drawable = context.packageManager.getApplicationIcon(appInfo)
                appListItems.add(Pair(appName, appIcon))
            }
        }
        postAction()
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.notification_widget)
        views.removeAllViews(R.id.notification)
        loadAppIcons(context) {
            val appNames: MutableSet<String> = mutableSetOf()
            NotificationService.myActiveNotifications.mapTo(appNames) { it.packageName }
            appNames.forEach { appName ->
                val notificationIcon = RemoteViews(context.packageName, R.layout.notification_icon)
//                notificationIcon.setImageViewIcon(R.id.notificationIcon, it.notification.smallIcon)
                notificationIcon.setImageViewBitmap(
                    R.id.notificationIcon,
                    appListItems.find { it.first == appName }?.second?.toBitmap()
                )
                views.addView(R.id.notification, notificationIcon)
            }
        }
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds =
            appWidgetManager.getAppWidgetIds(ComponentName(context, NotificationWidget::class.java))
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
}
