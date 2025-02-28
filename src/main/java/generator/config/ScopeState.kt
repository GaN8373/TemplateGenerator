package generator.config

import com.intellij.database.psi.DbDataSource
import com.intellij.database.psi.DbTable
import com.intellij.openapi.project.Project
import javax.swing.JTextField

class ScopeState {
    var selectTypeMapping: String? = null
    private var templateGroup: JTextField? = null
    private var pathInput: JTextField? = null
    var dataSourceList: Collection<DbDataSource>? = null
    var allTables: Map<String, DbTable>? = null
    var selectedTables: Set<String>? = null

    var templateGroupPath: String?
        get() = templateGroup!!.text
        set(template) {
            templateGroup!!.text = template
        }

    val path: String
        get() = pathInput!!.text

    fun setGenerateFileStorePath(path: String) {
        pathInput!!.text = path
    }

    companion object {
        @JvmStatic
        fun newInstance(project: Project?, pathInput: JTextField?, templateGroup: JTextField?): ScopeState {
            val scopeState = ScopeState()
            scopeState.templateGroup = templateGroup
            scopeState.pathInput = pathInput
            return scopeState
        }
    }
}