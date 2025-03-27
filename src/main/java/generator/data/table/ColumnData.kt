package generator.data.table

import com.intellij.database.model.*
import com.intellij.database.psi.DbDataSource
import generator.data.TypeMappingUnit
import generator.interfaces.IRawDas
import generator.interfaces.IRawDb
import generator.util.DasUtil
import generator.util.TemplateUtil

@Suppress("unused")
class ColumnData(
    private val datasource: DbDataSource?,
    private val rawDas: DasColumn,
    val typeMappingUnits: Collection<TypeMappingUnit>
) : IRawDas<DasColumn>, IRawDb {

    override fun getRawDas(): DasColumn {
        return rawDas
    }

    override fun getDatasource(): DbDataSource? {
        return datasource
    }

    fun tryTransformTo(v: String): String? {
        return TemplateUtil.convertType(v, typeMappingUnits)
    }

    /**
     * 获取映射类型。若转换失败则返回"unknown"
     *
     * @return the mapped type string or "unknown" if conversion fails
     */
    fun getMapperType(): String {
        return TemplateUtil.convertType(DasUtil.getDataType(rawDas), typeMappingUnits) ?: "unknown"
    }

    fun tryMapValue(): String?{
        return TemplateUtil.convertType(DasUtil.getDataType(rawDas), typeMappingUnits)
    }

    /**
     * 获取原始默认值字符串。
     * @return the raw default value string
     */
    fun getRawDefaultValue(): String {
        return rawDas.default ?: ""
    }

    /**
     * 获取原始数据类型字符串。
     *
     * @return the raw data type string
     */
    fun getRawType(): String {
        return DasUtil.getDataType(rawDas)
    }

    /**
     * 获取索引数据列表。
     *
     * @return a list of ColumnIndexData objects created from the dasIndex elements
     */
    fun getIndexList(): List<IndexWithColumnData> {
        return dasIndex.map { IndexWithColumnData(datasource, it, this) }
    }

    fun getForeignKeyList(): List<ForeignKeyWithColumnData> {
        val dasParent = getRawDas().dasParent as DasTable
        return dasParent.getDasChildren(ObjectKind.FOREIGN_KEY).map { it as DasForeignKey }
            .filter { it.columnsRef.names().contains(getRawDas().name) }
            .map{ ForeignKeyWithColumnData(datasource,it, this)}.toList()
    }

    fun hasNotNull(): Boolean {
        return getRawDas().isNotNull
    }

    fun hasPrimaryKey(): Boolean {
        return DasUtil.hasAttribute(getRawDas(), DasColumn.Attribute.PRIMARY_KEY)
    }

    fun hasForeignKey(): Boolean {
        return DasUtil.hasAttribute(getRawDas(), DasColumn.Attribute.FOREIGN_KEY)
    }


    fun hasIndex(): Boolean {
        return DasUtil.hasAttribute(getRawDas(), DasColumn.Attribute.INDEX)
    }

    private lateinit var dasIndex: List<DasIndex>
    fun hasUnique(): Boolean {
        val isUnique = dasIndex.map { it.isUnique }.first { it }
        return isUnique
    }


    fun hasAutoGenerated(): Boolean {
        return DasUtil.hasAttribute(getRawDas(), DasColumn.Attribute.AUTO_GENERATED)
    }

    init {
        run {
            val dasParent = getRawDas().dasParent as DasTable
            val toList = dasParent.getDasChildren(ObjectKind.INDEX).map { it as DasIndex }
                .filter { it.columnsRef.names().contains(getRawDas().name) }
                .toList()
            this.dasIndex = toList
        }
    }
}
