package generator.data.table

import com.intellij.database.model.DasObject
import com.intellij.database.model.ObjectKind
import com.intellij.database.psi.DbDataSource
import generator.data.TypeMappingUnit
import generator.util.DasUtil

@Suppress("unused")
class DbStructData(
    private val datasource: DbDataSource?,
    private val das: DasObject?,
    private val typeMappingUnits: Collection<TypeMappingUnit>
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
        return extractAllTableFromDas.map{ TableData(datasource, it, typeMappingUnits)}.toList()
    }

}
