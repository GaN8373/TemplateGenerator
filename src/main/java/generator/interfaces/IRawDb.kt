package generator.interfaces

import C.h.Da
import com.intellij.database.model.DasObject
import com.intellij.database.psi.DbDataSource

interface IRawDb {

    /**
     * 获取原始的 DasObject 对象。
     *
     * @return the raw [DasObject] instance wrapped by this interface.
     */
    fun getRawDas(): DasObject

    fun getDatasource(): DbDataSource?
    /**
     * 获取原始语句, 不同的das得到不同的结果
     *
     * pg when table: raw ddl sql
     *
     * pg when view: raw view ddl sql, but column statement is empty
     *
     * pg when column: alter table sys_role add role_id uuid not null; comment on column sys_role.role_id is '角色ID';
     *
     * pg when index: create index idx_your_table_your_column_tsvector on sys_role using gin (role_name);
     * @return Returns the raw SQL statement associated with this object.
     */
    fun getRawStatement(): String {
        return getDatasource()?.findElement(getRawDas())?.text ?: ""
    }
}