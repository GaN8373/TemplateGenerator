package generator.util;

import com.intellij.database.model.*;
import com.intellij.database.psi.*;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * 兼容工具
 *
 * @author makejava
 * @date 2023/04/04 17:08
 */
public class DasUtil {

    public static String getDataType(DasColumn dasColumn) {
        return dasColumn.getDasType().getSpecification();
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

    public static boolean hasAttribute(@Nullable DasColumn column, @NotNull DasColumn.@NotNull Attribute attribute) {
        DasTable table = column == null ? null : column.getTable();
        return table != null && table.getColumnAttrs(column).contains(attribute);
    }


    public static Stream<DbTable> extractSelectTablesFromPsiElement(@NotNull DataContext event) {
        PsiElement[] psiElements = event.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            return Stream.empty();
        }

        return Arrays.stream(psiElements).filter(element -> element instanceof DbTable).map(element -> (DbTable) element);
    }

    public static Stream<DasObject> extractDatabaseDas(DataContext context) {
        var databaseElements = context.getData(DataKey.create("DATABASE_ELEMENTS"));
        if (databaseElements instanceof Object[] array) {
            return Arrays.stream(array).filter(o -> o instanceof DasObject).map(o -> (DasObject) o);
        }

        return Stream.empty();
    }

    public static Stream<DasTable> extractTableFromDatabase(DataContext context) {
        var databaseElements = extractDatabaseDas(context);
        return databaseElements.flatMap(DasUtil::extractAllTableFromDas);
    }

    public static Stream<DasTable> extractAllTableFromDas(DasObject namespace) {
        if (namespace.getKind() == ObjectKind.DATABASE) {
            var set = namespace.getDasChildren(ObjectKind.SCHEMA).flatMap(s -> s.getDasChildren(ObjectKind.TABLE).filter(DasTable.class)).toStream();
            return Stream.concat(set, namespace.getDasChildren(ObjectKind.TABLE).filter(DbTable.class).toStream());
        } else if (namespace.getKind() == ObjectKind.SCHEMA) {
            return namespace.getDasChildren(ObjectKind.TABLE).filter(DasTable.class).toStream();
        } else if (namespace.getKind() == ObjectKind.TABLE && namespace.getDasParent() != null) {
            return namespace.getDasParent().getDasChildren(ObjectKind.TABLE).filter(DasTable.class).toStream();
        }

        return Stream.empty();
    }

}
