package generator.config

import com.intellij.database.model.DasTable
import com.intellij.openapi.project.Project
import generator.ui.components.ListCheckboxComponent
import java.nio.file.Path
import javax.swing.JComboBox
import javax.swing.JTextField

class ScopeState(
    val project: Project?,
    /**
     * 输出文件的路径
     */
    private val pathInput: JTextField?,
    /**
     * 模板文件的路径
     * 支持历史选择
     */
    private val templateGroup: JComboBox<String>?,
    /**
     * 文本映射信息
     */
    private val typeMappingSelected: JComboBox<String>) {
    /**
     * 数据源中所有的表
     */
    private var allTables: Map<String, DasTable>? = null

    /**
     * 用来选择生成哪些表的组件
     */
    private var selectTableComponent: ListCheckboxComponent? = null

    var templateGroupPath: String?
        get() = templateGroup?.selectedItem.toString()
        set(template) {
            templateGroup?.addItem(template)
            templateGroup?.selectedItem = template
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
        val selectedItems = selectTableComponent?.selectedItems

        return selectedItems?.mapNotNull {
            allTables?.get(it)
        }?.toSet() ?: setOf()
    }

    /**
     * 显示模板文件的名称和模板的路径
     * @key 展示在模板中的名称
     * @value 模板的路径
     */
    private var templateFilePath: Map<String, Path>? = null

    /**
     * 选择模板文件的组件
     */
    private var selectTemplateComponent: ListCheckboxComponent? = null

    fun setTemplateFilePath(paths: Map<String, Path>, component: ListCheckboxComponent) {
        templateFilePath = paths
        selectTemplateComponent = component
    }

    fun getSelectedTemplatePath(): Set<Path> {
        val selectedItems = selectTemplateComponent?.selectedItems

        return selectedItems?.mapNotNull {
            templateFilePath?.get(it)
        }?.toSet() ?: setOf()
    }
}