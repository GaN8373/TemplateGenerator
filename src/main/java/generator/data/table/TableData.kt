package generator.data.table

import com.intellij.database.model.DasColumn
import com.intellij.database.model.ObjectKind
import com.intellij.database.psi.DbTable
import generator.data.TypeMapper

class TableData(val dbTable: DbTable, private val typeMapper: Collection<TypeMapper>) {

    fun getRawName(): String {
        return dbTable.name
    }

    fun getParent(): DbStructData{
       return DbStructData(dbTable.parent)
    }


    fun getRawComment(): String {
        return dbTable.comment ?: ""
    }

    private var columns: List<ColumnData>? = null
    fun getColumns(): List<ColumnData> {
        if (columns != null) {
            return columns!!
        }

        val columns = ArrayList<ColumnData>()
        dbTable.getDasChildren(ObjectKind.COLUMN).forEach {
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
