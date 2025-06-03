package generator.data.table

import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasForeignKey
import com.intellij.database.model.ObjectKind
import com.intellij.database.psi.DbDataSource
import generator.data.GenerateContext
import generator.interfaces.IRawDas
import generator.interfaces.IRawDb

@Suppress("unused")
class ForeignKeyData(
    private val rawDas: DasForeignKey,
    private val context: GenerateContext
) : IRawDas<DasForeignKey>, IRawDb {
    override fun getDatasource(): DbDataSource {
        return context.datasource
    }

    override fun getRawDas(): DasForeignKey {
        return rawDas
    }

    fun getColumns(): List<ColumnData>{
        return rawDas.columnsRef.resolveObjects().filter { it.kind == ObjectKind.COLUMN }
            .map { it as DasColumn }
            .map { ColumnData(it, context) }
            .toList()
    }

    fun getInverseColumns(): List<ColumnData> {
        return rawDas.refColumns.resolveObjects().filter { it.kind == ObjectKind.COLUMN }
            .map { it as DasColumn }
            .map { ColumnData(it, context) }
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
            .map { ColumnData(it, context) }
            .toList()
    }

}