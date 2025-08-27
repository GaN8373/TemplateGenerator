<#assign DbStructData=table.getParent()>
<#assign DbPascalName = NameUtil.toPascalCase(DbStructData.getRawName())>

<#assign TablePascalName=NameUtil.toPascalCase(table.getRawName())>
#region config
fileName=${TablePascalName}.cs
dir=${DbPascalName}/Entities
#endregion
<#---->
using FreeSql.DataAnnotations;

/// <summary>
/// ${table.getRawComment()}
/// </summary>
<#--填写schema和table，schema.tablename-->
[Table(Name = "${DbStructData.hasSchema()?then('${DbStructData.getRawName()}.','')}${table.getRawName()}")]
public partial class ${TablePascalName} : BaseEntity {
<#list columns as column >
    <#assign ColumnPascalName = NameUtil.toPascalCase(column.getRawName())>
    <#if context.containsKey("BaseEntity." + ColumnPascalName)><#continue ></#if>
<#---->
    /// <summary>
    /// ${column.getRawComment()}
    /// </summary>
    [Column(Name = "${column.getRawName()}"${column.hasPrimaryKey()?then(", IsPrimary = true","")})]
    public ${column.getMapperType()}${column.hasNotNull()?then("","?")} ${ColumnPascalName} { get; set; }
<#---->
    <#list column.getForeignKeys() as foreignKey>
        <#list  foreignKey.getInverseColumns() as RemoteColumn >
            <#assign RemoteTablePascalName=NameUtil.toPascalCase(RemoteColumn.getParent().getRawName())>
            /// <summary>
            /// ${column.getRawComment()}
            /// </summary>
            <#assign ColumnNameFindId=column.getRawName()?lowerCase?endsWith("_id")>
        <#--            避免和已生成的列名称一致-->
            <#assign ColumnPascalNameNoId = ColumnNameFindId?then(ColumnPascalName?substring(0,ColumnPascalName?length-2),RemoteTablePascalName)>
            [Navigate(nameof(${ColumnPascalName}))]
            public ${RemoteTablePascalName}? ${ColumnPascalNameNoId} {get;set;}
        </#list>
    </#list>
</#list>

<#--获取其他表的外键-->
<#list table.getInverseForeignKeys() as foreignKey>
<#--    为了不混淆，Local都指向当前表-->
    <#assign LocalColumn = foreignKey.getInverseColumns()[0] >
    <#assign RemoteColumn = foreignKey.getColumns()[0] >
    <#assign RemoteTablePascalName=NameUtil.toPascalCase(RemoteColumn.getParent().getRawName())>
    <#assign RemoteType=NameUtil.toPascalCase(RemoteColumn.getParent().getRawName())>
    <#assign LocalColumnPascalName = NameUtil.toPascalCase(LocalColumn.getRawName())>
    <#assign RemoteColumnPascalName = NameUtil.toPascalCase(RemoteColumn.getRawName())>
<#---->
    <#assign LocalColumnNameFindId=LocalColumn.getRawName()?lowerCase?endsWith("id")>
    <#assign LocalColumnPascalNameNoId = LocalColumnNameFindId?then(LocalColumnPascalName?substring(0,LocalColumnPascalName?length-2),LocalColumnPascalName)>
    <#assign RemoteColumnPascalNameNoId = RemoteColumnPascalName?endsWith("Id")?then(RemoteColumnPascalName?substring(0,RemoteColumnPascalName?length-2),RemoteColumnPascalName)>
<#---->
    /// <summary>
    /// ${RemoteColumn.getRawComment()}
    /// </summary>
    /// <ref>${RemoteColumn.getParent().getRawName()}</ref>
    [Navigate(nameof(${RemoteTablePascalName}.${RemoteColumnPascalName}))]
    public IList<${RemoteTablePascalName}> ${RemoteTablePascalName}${RemoteColumnPascalNameNoId}List {get;set;} = new List<${RemoteTablePascalName}>();
</#list>

    /// <inheritdoc />
    public override void FillInsertDefaultValue()
    {
<#list columns as column >
<#if column.hasPrimaryKey() && column.getMapperType() == "Guid" && !column.hasForeignKey()>
        this.${NameUtil.toPascalCase(column.getRawName())} = Guid.CreateVersion7();
</#if>
</#list>
    }

    /// <inheritdoc />
    public override void FillUpdateDefaultValue()
    {
<#--<#list columns as column >-->

<#--</#list>-->
    }

}
