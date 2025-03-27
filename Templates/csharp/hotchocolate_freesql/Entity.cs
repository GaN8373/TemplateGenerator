<#assign DbStructData=table.getParent()>
#region config
fileName=${NameUtil.toPascalCase(table.getRawName())}.cs
dir=Entity
#endregion
using FreeSql.DataAnnotations;


/// <summary>
/// ${table.getRawComment()}
/// </summary>
[Table(Name = "<#if DbStructData.hasSchema()>${DbStructData.getRawName()}.</#if>${table.getRawName()}")]
public partial class ${NameUtil.toPascalCase(table.getRawName())} {

<#list columns as column >
    /// <summary>
    /// ${column.getRawComment()}
    /// index:
    /// </summary>
    [Column(Name = "${column.getRawName()}" <#if column.hasPrimaryKey()>, IsPrimary=true</#if>)]
    public ${column.getMapperType()} ${NameUtil.toPascalCase(column.getRawName())} {get;set;}
</#list>

}
