package generator.data.table

import com.intellij.database.model.DasObject
import com.intellij.database.model.ObjectKind
import generator.data.GenerateContext
import generator.util.DasUtil

@Suppress("unused")
class DbStructData(
    private val das: DasObject?,
    private val context: GenerateContext
) {

    fun getRawName(): String {
        return das?.name ?: ""
    }

    fun hasSchema(): Boolean {
        return das?.kind == ObjectKind.SCHEMA
    }

    fun hasDatabase(): Boolean {
        return das?.kind == ObjectKind.DATABASE
    }

    fun getDatabaseFullTable(): List<TableData> {
        var db = das
        if (hasSchema() && db?.dasParent?.kind == ObjectKind.DATABASE) {
            db = das?.dasParent
        }
        if (db == null) {
            return emptyList()
        }

        val extractAllTableFromDas = DasUtil.extractAllTableFromDas(db)
        return extractAllTableFromDas.map { TableData(it, context) }.toList()
    }

}
