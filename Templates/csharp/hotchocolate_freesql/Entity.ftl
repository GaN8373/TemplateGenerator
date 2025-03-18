#region config
fileName=${NameUtil.toPascalCase(table.getTableName())}.cs
dir=Entity
#endregion
using FreeSql.DataAnnotations;

/// <summary>
/// ${table.getComment()}
/// </summary>
/// <remarks>
/// @Date ${.now?string('yyyy-MM-dd HH:mm:ss')}
/// </remarks>
[Table(Name = "${table.getTableName()}")]
public partial class ${NameUtil.toPascalCase(table.getTableName())} {

<#list columns as column > 
    /// <summary>
    /// ${column.getColumnComment()}
    /// </summary>
    [Column(Name = "${column.getColumnName()}" <#if column.hasPrimaryKey()>, IsPrimary=true</#if>)
    private ${column.getMapperType()} ${NameUtil.toPascalCase(column.getColumnName())};
</#list>

}
