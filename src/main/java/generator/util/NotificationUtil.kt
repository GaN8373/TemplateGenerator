package generator.util

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager

object NotificationUtil {
    private const val NOTIFICATION_GROUP_ID = "TemplateGeneratorNotification"


    @JvmStatic
    fun showWarningNotification(title: String, content: String, project: Project? = ProjectManager.getInstance().defaultProject, notificationType: NotificationType = NotificationType.WARNING) {
        val notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup(NOTIFICATION_GROUP_ID)
        val notification = notificationGroup.createNotification(title, content, notificationType)
        Notifications.Bus.notify(notification, project)
    }

}
