package generator.data.table

import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasIndex
import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import com.intellij.database.psi.DbDataSource
import generator.data.TypeMappingUnit
import generator.interfaces.IRawDas
import generator.interfaces.IRawDb

@Suppress("unused")
class TableData(
    private val datasource: DbDataSource?,
    private val rawDas: DasTable,
    private val typeMappingUnits: Collection<TypeMappingUnit>
) : IRawDas<DasTable>, IRawDb {
    override fun getDatasource(): DbDataSource? {
        return datasource
    }

    override fun getRawDas(): DasTable {
        return rawDas
    }

    fun getTypeMapper(): Collection<TypeMappingUnit> {
        return typeMappingUnits
    }

    /**
     * 获取父级结构数据。
     * 如果是 Pg，表示 schema；如果是 MySQL，表示 database。
     *
     * @return the parent structure data as a DbStructData object
     */
    fun getParent(): DbStructData {
        return DbStructData(datasource,rawDas.dasParent,typeMappingUnits)
    }

    private var columns: List<ColumnData>? = null

    fun getIndexList(): List<IndexData> {
        val indexList = ArrayList<IndexData>()
        rawDas.getDasChildren(ObjectKind.INDEX).forEach {
            if (it is DasIndex) {
                indexList.add(IndexData(datasource, it, typeMappingUnits))
            }
        }
        return indexList
    }

    /**
     * 获取该表所有列。
     *
     * @return get column from table
     */
    fun getColumns(): List<ColumnData> {
        if (columns != null) {
            return columns!!
        }

        val columns = ArrayList<ColumnData>()
        rawDas.getDasChildren(ObjectKind.COLUMN).forEach {
            if (it is DasColumn) {
                columns.add(ColumnData(datasource, it, typeMappingUnits))
            }
        }
        this.columns = columns
        return columns
    }

    /**
     * 获取主键列的数据列表。
     *
     * @return a list of primary key ColumnData objects
     */
    fun getPrimaryColumns(): List<ColumnData> {
        return getColumns().filter { it.hasPrimaryKey() }
    }
}
