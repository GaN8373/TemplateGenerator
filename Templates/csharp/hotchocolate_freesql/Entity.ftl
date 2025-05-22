<#assign DbStructData=table.getParent()>
<#assign TablePascalName=NameUtil.toPascalCase(table.getRawName())>
#region config
fileName=${TablePascalName}.cs
dir=Entity
#endregion
using FreeSql.DataAnnotations;

<#macro column_foreign_unwrap CurrentColumn,ForeignColumn,ProcessedForeignKey >
        <#assign ColumnName = NameUtil.toPascalCase(CurrentColumn.getRawName())>
        <#assign FcType=NameUtil.toPascalCase(ForeignColumn.getParent().getRawName())>

        <#if !ProcessedForeignKey[FcType]??>
            <#assign ProcessedForeignKey = ProcessedForeignKey + {FcType:true}>
            <#assign ColumnNameFindIdIndex=CurrentColumn.getRawName()?lowerCase?endsWith("_id")?then(ColumnName?length-2,-1)>
                /// <summary>
                /// ${CurrentColumn.getRawComment()}
                /// </summary>
            <#if ForeignColumn.hasUnique() || ForeignColumn.hasPrimaryKey()>
                [Navigate(nameof(${ColumnName}))]
                public ${FcType} ${(ColumnNameFindIdIndex<1)?then(FcType,ColumnName?substring(0,ColumnNameFindIdIndex))}  {get;set;}

            <#else>
                [Navigate(ManyToMany=typeof(${FcType}))]
                public IList<${FcType}> ${FcType}List {get;set;}

            </#if>
        </#if>
</#macro>

/// <summary>
/// ${table.getRawComment()}
/// </summary>
[Table(Name = "<#if DbStructData.hasSchema()>${DbStructData.getRawName()}.</#if>${table.getRawName()}")]
public partial class ${TablePascalName} {

<#list columns as column >
    /// <summary>
    /// ${column.getRawComment()}
    /// </summary>
    [Column(Name = "${column.getRawName()}"<#if column.hasPrimaryKey()>, IsPrimary=true</#if> )]
    public ${column.getMapperType()} ${NameUtil.toPascalCase(column.getRawName())} { get; set; }

    <#assign processedForeignKey = {TablePascalName:true}>
    <#list column.getForeignKeys() as foreignKey>
        <#list  foreignKey.getInverseColumns() as fc >
           <@column_foreign_unwrap column,fc,processedForeignKey />
        </#list>
    </#list>

    <#list column.getInverseForeignKeys() as foreignKey>
        <#list  foreignKey.getColumns() as fc >
           <@column_foreign_unwrap column,fc,processedForeignKey />
        </#list>
    </#list>

</#list>


}
