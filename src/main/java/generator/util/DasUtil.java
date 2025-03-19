package generator.util;

import com.intellij.database.model.DasColumn;
import com.intellij.database.model.DasTable;
import com.intellij.database.model.DataType;
import com.intellij.database.model.ObjectKind;
import com.intellij.database.psi.DbElement;
import com.intellij.database.psi.DbNamespaceImpl;
import com.intellij.database.psi.DbTable;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * 兼容工具
 *
 * @author makejava
 * @date 2023/04/04 17:08
 */
public class DasUtil {

    public static DataType getDataType(DasColumn dasColumn) {
        try {
            // 兼容2022.3.3及以上版本
            Method getDasTypeMethod = dasColumn.getClass().getMethod("getDasType");
            Object dasType = getDasTypeMethod.invoke(dasColumn);
            Method toDataTypeMethod = dasType.getClass().getMethod("toDataType");
            return (DataType) toDataTypeMethod.invoke(dasType);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            // 兼容2022.3.3以下版本
            try {
                Method getDataTypeMethod = dasColumn.getClass().getMethod("getDataType");
                return (DataType) getDataTypeMethod.invoke(dasColumn);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static boolean hasAttribute(@Nullable DasColumn column, @NotNull DasColumn.@NotNull Attribute attribute) {
        DasTable table = column == null ? null : column.getTable();
        return table != null && table.getColumnAttrs(column).contains(attribute);
    }

    /**
     * collection no self
     * @param element
     * @return
     */
    public static Set<DbTable> extractTables(PsiElement element) {
        if (element instanceof DbTable table) {
            var parent = table.getParent();
            if (parent != null) {
                return parent.getDasChildren(ObjectKind.TABLE).filter(DbTable.class).filter(x-> !x.equals(element)).toSet();
            }
        }

        if (element instanceof DbNamespaceImpl dbNamespace) {
            return dbNamespace.getDasChildren(ObjectKind.TABLE).filter(DbTable.class).toSet();
        }

        return Set.of();
    }
}
