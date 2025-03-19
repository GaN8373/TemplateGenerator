package generator.data.table

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
        for (typeMapper in typeMappers) {
            val columnNameLowercase = DasUtil.getDataType(dbColumn).toString().lowercase()
            if (typeMapper.action == MapperAction.Regex
                && typeMapper.type.contains("$1")
                && typeMapper.action.convertor.match(typeMapper.rule, columnNameLowercase)
            ) {
                return TemplateUtil.replaceWithRegexGroups(
                    typeMappers,
                    typeMapper.rule.lowercase(),
                    columnNameLowercase,
                    typeMapper.type
                )
            }

            if (typeMapper.action.convertor.match(typeMapper.rule.lowercase(), columnNameLowercase)) {
                return typeMapper.type
            }
        }

        return "unknown"
    }

    fun getColumnName(): String {
        return dbColumn.name
    }

    fun getColumnComment(): String {
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
