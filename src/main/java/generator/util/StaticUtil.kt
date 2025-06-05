package generator.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.file.FileSystems

object StaticUtil {
    private const val NOTIFICATION_GROUP_ID = "TemplateGeneratorNotification"


    @JvmStatic
    fun showWarningNotification(title: String, content: String, project: Project? = ProjectManager.getInstance().defaultProject, notificationType: NotificationType = NotificationType.WARNING) {
        val notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup(NOTIFICATION_GROUP_ID)
        val notification = notificationGroup.createNotification(title, content, notificationType)
        Notifications.Bus.notify(notification, project)
    }


    @JvmStatic
    val JSON = ObjectMapper();

    @JvmStatic
    fun extractDirAfterName(filePath: String, replaceDir: String): String
    {
        val replaceDir2 = replaceDir.replace("/", FileSystems.getDefault().separator)

        // 排除掉文件前面的路径
        var replace: String = filePath.replace(replaceDir2,"",true)
        if (replace.startsWith(FileSystems.getDefault().separator)) {
            replace = replace.substring(1)
        }
        return replace
    }

    @JvmStatic
    fun extractFullStack(e: Exception): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        return sw.toString()
    }

}
