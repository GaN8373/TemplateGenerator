package generator.data.table

import com.intellij.codeInsight.daemon.impl.HighlightInfo.convertType
import com.intellij.database.model.DasColumn
import generator.MapperAction
import generator.data.TypeMapper
import generator.util.DasUtil
import generator.util.TemplateUtil

class ColumnData(private val dbColumn: DasColumn, private val typeMappers: Collection<TypeMapper>) {
    private fun getRawData(): DasColumn {
        return dbColumn
    }

    fun getMapperType(): String {
        return TemplateUtil.convertType(dbColumn, typeMappers) ?: "unknown"
    }

    fun getRawType(): String {
        return DasUtil.getDataType(dbColumn).toString()
    }

    fun getRawName(): String {
        return dbColumn.name
    }

    fun getRawComment(): String {
        return dbColumn.comment ?: ""
    }


    fun hasPrimaryKey(): Boolean {
        return DasUtil.hasAttribute(getRawData(), DasColumn.Attribute.PRIMARY_KEY)
    }
    fun hasForeignKey(): Boolean {
        return DasUtil.hasAttribute(getRawData(), DasColumn.Attribute.FOREIGN_KEY)
    }

    fun hasIndex(): Boolean {
        return DasUtil.hasAttribute(getRawData(), DasColumn.Attribute.INDEX)
    }

}
