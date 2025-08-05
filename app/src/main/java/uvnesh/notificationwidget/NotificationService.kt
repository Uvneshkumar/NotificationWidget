package uvnesh.notificationwidget

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotificationService : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        updateWidget()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        updateWidget()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        updateWidget()
    }

    private fun updateWidget() {
        myActiveNotifications = activeNotifications
        sendBroadcast(Intent("uvnesh.notification.UPDATE_WIDGET").apply {
            setPackage(packageName)
        })
    }

    companion object {
        var myActiveNotifications: Array<StatusBarNotification> = arrayOf()
    }
}