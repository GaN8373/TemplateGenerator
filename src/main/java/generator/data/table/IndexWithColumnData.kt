package generator.data.table

import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasIndex
import com.intellij.database.model.ObjectKind
import com.intellij.database.psi.DbDataSource
import generator.interfaces.IRawDas
import generator.interfaces.IRawDb

@Suppress("unused")
class IndexWithColumnData(
    private val datasource: DbDataSource?,
    private val rawDas: DasIndex,
    private val column: ColumnData
) : IRawDas<DasIndex>, IRawDb {
    override fun getDatasource(): DbDataSource? {
        return datasource
    }

    override fun getRawDas(): DasIndex {
        return rawDas
    }

    fun getColumn(): ColumnData {
        return column
    }

    /**
     * 获取其他列的相关信息。
     *
     * @return the information of other columns as a specific object
     */
    fun getOtherColumn(): List<ColumnData> {
        return rawDas.columnsRef.resolveObjects().filter { it.kind == ObjectKind.COLUMN }
            .map { it as DasColumn }
            .filter { it.name != column.getRawDas().name }
            .map { ColumnData(datasource, it, column.typeMappingUnits) }
            .toList()
    }

    fun hasUnique(): Boolean {
        return rawDas.isUnique
    }

    fun hasPrimary(): Boolean {
        return column.hasPrimaryKey()
    }

}