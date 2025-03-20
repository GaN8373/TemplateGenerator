package generator.config

import com.intellij.database.model.DasTable
import com.intellij.database.psi.DbTable
import com.intellij.openapi.project.Project
import generator.ui.components.ListCheckboxComponent
import java.nio.file.Path
import javax.swing.JComboBox
import javax.swing.JTextField

class ScopeState(
    val project: Project?,
    val pathInput: JTextField?,
    val templateGroup: JComboBox<String>?,
    val typeMappingSelected: JComboBox<String>) {
    private var allTables: Map<String, DasTable>? = null
    private var selectTableComponent: ListCheckboxComponent? = null

    fun getTemplateGroupTextField(): JTextField {
        return templateGroup!!.editor.editorComponent as JTextField

    }
    var templateGroupPath: String?
        get() = getTemplateGroupTextField().text
        set(template) {
            getTemplateGroupTextField().text = template
        }

    val path: String
        get() = pathInput!!.text

    fun setGenerateFileStorePath(path: String) {
        pathInput!!.text = path
    }

    fun getSelectTypeMapping(): String? {
        return typeMappingSelected.selectedItem?.toString()
    }
    fun setAllTableAndComponent(allTables: Map<String, DasTable>, component: ListCheckboxComponent) {

        this.allTables = allTables
        this.selectTableComponent = component
    }

    fun getSelectedTables(): Set<DasTable> {
        val selectedItems = selectTableComponent?.selectedItems;

        return selectedItems?.mapNotNull {
            allTables?.get(it)
        }?.toSet() ?: setOf()
    }

    private var templateFilePath: Map<String, Path>? = null
    private var selectTemplateComponent: ListCheckboxComponent? = null

    fun setTemplateFilePath(paths: Map<String, Path>, component: ListCheckboxComponent) {
        templateFilePath = paths
        selectTemplateComponent = component
    }

    fun getSelectedTemplatePath(): Set<Path> {
        val selectedItems = selectTemplateComponent?.selectedItems;

        return selectedItems?.mapNotNull {
            templateFilePath?.get(it)
        }?.toSet() ?: setOf()
    }
}