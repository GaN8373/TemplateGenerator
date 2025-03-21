package generator

import com.intellij.database.model.DasObject
import com.intellij.database.model.ObjectKind
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import generator.ui.dialog.GenerateConfigDialog
import generator.util.DasUtil

class TemplateGeneratorPluginsAction() : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return super.getActionUpdateThread()
    }

    override fun update(e: AnActionEvent) {
        val dasObjectStream = DasUtil.extractDatabaseDas(e.dataContext)

        val b = dasObjectStream.anyMatch { x: DasObject -> SUPPORTED_KINDS.contains(x.kind) }
        e.presentation.isEnabledAndVisible = b
        super.update(e)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val tableGenerator = GenerateConfigDialog(e)

        tableGenerator.title = "TemplateGenerator"
        tableGenerator.setSize(800, 600)

        tableGenerator.show()
    }

    companion object {
        private val SUPPORTED_KINDS: Set<ObjectKind> = mutableSetOf(
            ObjectKind.TABLE,
            ObjectKind.VIEW,
            ObjectKind.SCHEMA,
            ObjectKind.DATABASE
        )
    }
}
