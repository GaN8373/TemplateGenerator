package generator.data.table

import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import generator.data.TypeMapper
import generator.interfaces.IRawDas

@Suppress("unused")
class TableData(private val rawDas: DasTable, private val typeMapper: Collection<TypeMapper>): IRawDas<DasTable> {

    override fun getRawDas(): DasTable {
        return rawDas
    }
    fun getTypeMapper(): Collection<TypeMapper> {
        return typeMapper
    }

    fun getParent(): DbStructData{
       return DbStructData(rawDas.dasParent)
    }

    private var columns: List<ColumnData>? = null
    fun getColumns(): List<ColumnData> {
        if (columns != null) {
            return columns!!
        }

        val columns = ArrayList<ColumnData>()
        rawDas.getDasChildren(ObjectKind.COLUMN).forEach {
            if (it is DasColumn) {
                columns.add(ColumnData(it, typeMapper))
            }
        }
        this.columns = columns
        return columns
    }

    fun getPrimaryColumns(): List<ColumnData> {
        return getColumns().filter { it.hasPrimaryKey() }
    }
}
