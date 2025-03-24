package generator.util

import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasObject
import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import com.intellij.database.psi.DbTable
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.DataKey
import com.intellij.openapi.actionSystem.LangDataKeys
import java.util.*
import java.util.stream.Stream


/**
 * 兼容工具
 *
 * @author makejava
 * @date 2023/04/04 17:08
 */
object DasUtil {
    fun getDataType(dasColumn: DasColumn): String {
        return dasColumn.dasType.specification
        //        try {
//            // 兼容2022.3.3及以上版本
//            Method getDasTypeMethod = dasColumn.getClass().getMethod("getDasType");
//            Object dasType = getDasTypeMethod.invoke(dasColumn);
//            Method toDataTypeMethod = dasType.getClass().getMethod("toDataType");
//            return (DataType) toDataTypeMethod.invoke(dasType);
//        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
//            // 兼容2022.3.3以下版本
//            try {
//                Method getDataTypeMethod = dasColumn.getClass().getMethod("getDataType");
//                return (DataType) getDataTypeMethod.invoke(dasColumn);
//            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
//                throw new RuntimeException(ex);
//            }
//        }
    }

    @JvmStatic
    fun hasAttribute(column: DasColumn, attribute: DasColumn.Attribute): Boolean {
        val table = column.table
        return table != null && table.getColumnAttrs(column).contains(attribute)
    }


    @JvmStatic
    fun extractSelectTablesFromPsiElement(event: DataContext): Stream<DbTable> {
        val psiElements = event.getData(LangDataKeys.PSI_ELEMENT_ARRAY)
        if (psiElements.isNullOrEmpty()) {
            return Stream.empty()
        }

        return Arrays.stream(psiElements).filter { it is DbTable }
            .map { it as DbTable }
    }

    @JvmStatic
    fun extractDatabaseDas(context: DataContext): Stream<DasObject> {
        val databaseElements = context.getData(DataKey.create<Any>("DATABASE_ELEMENTS"))
        if (databaseElements is Array<*> && databaseElements.isArrayOf<Any>()) {
            return Arrays.stream(databaseElements).filter { it is DasObject }.map { it as DasObject }
        }

        return Stream.empty()
    }

    @JvmStatic
    fun extractTableFromDatabase(context: DataContext): Stream<DasTable> {
        val databaseElements = extractDatabaseDas(context)
        return databaseElements.flatMap { extractAllTableFromDas(it) }
    }

    @JvmStatic
    fun extractAllTableFromDas(namespace: DasObject): Stream<DasTable> {
        return when (namespace.kind) {
            ObjectKind.DATABASE -> Stream.concat(
                namespace.getDasChildren(ObjectKind.SCHEMA).toStream().flatMap { extractChildTable(it) },
                extractChildTable(namespace)
            )

            ObjectKind.SCHEMA -> extractChildTable(namespace)
            ObjectKind.TABLE, ObjectKind.VIEW -> when (namespace.dasParent) {
                null -> Stream.empty()
                else -> extractChildTable(namespace.dasParent!!)
            }

            else -> Stream.empty()
        }
    }

    /**
     * 获取子表
     * @param namespace
     * @return
     */
    @JvmStatic
    private fun extractChildTable(namespace: DasObject): Stream<DasTable> {
        return Stream.concat(
            namespace.getDasChildren(ObjectKind.TABLE).toStream(),
            namespace.getDasChildren(ObjectKind.VIEW).toStream()
        )
            .filter { it is DasTable }
            .map { it as DasTable }
    }
}
