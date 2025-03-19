#region config
fileName=${NameUtil.toPascalCase(table.getRawName())}.cs
dir=Entity
#endregion
using FreeSql.DataAnnotations;

/// <summary>
/// ${table.getRawComment()}
/// </summary>
/// <remarks>
/// @Date ${.now?string('yyyy-MM-dd HH:mm:ss')}
/// </remarks>
[Table(Name = "${table.getRawName()}")]
public partial class ${NameUtil.toPascalCase(table.getRawName())} {

<#list columns as column > 
    /// <summary>
    /// ${column.getRawComment()}
    /// </summary>
    [Column(Name = "${column.getRawName()}" <#if column.hasPrimaryKey()>, IsPrimary=true</#if>)]
    private ${column.getMapperType()} ${NameUtil.toPascalCase(column.getRawName())};
</#list>

}
