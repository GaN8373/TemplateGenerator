package generator.data.table

import com.intellij.database.model.DasColumn
import com.intellij.database.model.ObjectKind
import com.intellij.database.psi.DbTable
import generator.data.TypeMapper

class TableData(val dbTable: DbTable, val typeMapper: Collection<TypeMapper>) {

    fun getTableName(): String {
        return dbTable.name
    }


    fun getColumns(): Collection<ColumnData> {
        val columns = ArrayList<ColumnData>()
        dbTable.getDasChildren(ObjectKind.COLUMN).forEach {
                if (it is DasColumn) {
                    columns.add(ColumnData(it,typeMapper))
                }
            }

        return columns
    }
}
