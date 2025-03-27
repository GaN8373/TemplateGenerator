package generator.data.table

import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasIndex
import com.intellij.database.psi.DbDataSource
import generator.data.TypeMappingUnit
import generator.interfaces.IRawDas
import generator.interfaces.IRawDb
@Suppress("unused")
class IndexData(
    private val datasource: DbDataSource?,
    private val rawDas: DasIndex,
    private val typeMappingUnits: Collection<TypeMappingUnit>
) : IRawDas<DasIndex>, IRawDb {
    override fun getRawDas(): DasIndex {
        return rawDas
    }

    override fun getDatasource(): DbDataSource? {
        return datasource
    }

    fun getColumns(): List<ColumnData> {
        val list = ArrayList<ColumnData>()
        rawDas.columnsRef.resolveObjects().map { it as DasColumn }.forEach {
            list.add(ColumnData(datasource, it, typeMappingUnits))
        }

        return list

    }

    fun hasUnique(): Boolean {
        return rawDas.isUnique
    }
}