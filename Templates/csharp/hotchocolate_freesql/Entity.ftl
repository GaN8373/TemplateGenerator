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
    /// </summary>
    [Column(Name = "${column.getRawName()}"<#if column.hasPrimaryKey()>, IsPrimary=true</#if> )]
    public ${column.getMapperType()} ${NameUtil.toPascalCase(column.getRawName())} {get;set;}


    <#list column.getForeignKeyList() as foreignKey>
        <#list  foreignKey.getOtherColumn() as fc >
            <#assign FcType=NameUtil.toPascalCase(fc.getParent().getRawName())>
            /// <summary>
            /// ${column.getRawComment()}
            /// </summary>
            <#if fc.hasUnique() && fc.hasPrimaryKey()>

            [Navigate(nameof(${NameUtil.toPascalCase(column.getRawName())}))]
            public ${FcType} ${FcType}  {get;set;}

            <#else>

            [Navigate(ManyToMany=typeof(${fc.getParent().getRawName()}))]
            public IEnumerable<${FcType}> ${FcType} {get;set;}

            </#if>
        </#list>
    </#list>

</#list>


}
