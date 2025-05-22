package generator.data.table

import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasForeignKey
import com.intellij.database.model.ObjectKind
import com.intellij.database.psi.DbDataSource
import generator.interfaces.IRawDas
import generator.interfaces.IRawDb

@Suppress("unused")
class ForeignKeyWithColumnData(
    private val datasource: DbDataSource?,
    private val rawDas: DasForeignKey,
    private val column: ColumnData
) : IRawDas<DasForeignKey>, IRawDb {
    override fun getDatasource(): DbDataSource? {
        return datasource
    }

    override fun getRawDas(): DasForeignKey {
        return rawDas
    }

    private fun getColumn(): ColumnData {
        return column
    }

    fun getColumns(): List<ColumnData>{
        return rawDas.columnsRef.resolveObjects().filter { it.kind == ObjectKind.COLUMN }
            .map { it as DasColumn }
            .map { ColumnData(datasource, it, column.typeMappingUnits) }
            .toList()
    }

    fun getInverseColumns(): List<ColumnData> {
        return rawDas.refColumns.resolveObjects().filter { it.kind == ObjectKind.COLUMN }
            .map { it as DasColumn }
            .map { ColumnData(datasource, it, column.typeMappingUnits) }
            .toList()
    }

    /**
     * 获取其他列的相关信息。
     *
     * @return the information of other columns as a specific object
     */
    @Deprecated("Unclear meaning")
    fun getOtherColumn(): List<ColumnData> {
        return rawDas.refColumns.resolveObjects().filter { it.kind == ObjectKind.COLUMN }
            .map { it as DasColumn }
            .map { ColumnData(datasource, it, column.typeMappingUnits) }
            .toList()
    }

}