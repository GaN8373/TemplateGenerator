<#assign DbStructData=table.getParent()>
<#assign TablePascalName=NameUtil.toPascalCase(table.getRawName())>
#region config
fileName=${TablePascalName}.cs
dir=Entities
#endregion
<#---->
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

/// <summary>
/// ${table.getRawComment()}
/// </summary>
[Table("${table.getRawName()}" ${DbStructData.hasSchema()?then(', Schema = "${DbStructData.getRawName()}"','')})]
public partial class ${TablePascalName} : BaseEntity {
<#list columns as column >
    <#assign ColumnPascalName = NameUtil.toPascalCase(column.getRawName())>
    /// <summary>
    /// ${column.getRawComment()}
    /// </summary>
    <#if column.hasPrimaryKey()>[Key]</#if>
    [Column("${column.getRawName()}")]
    public ${column.getMapperType()} ${ColumnPascalName} { get; set; }
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
            [ForeignKey(nameof(${ColumnPascalName}))]
            [InverseProperty(nameof(${RemoteTablePascalName}.${TablePascalName}${ColumnPascalNameNoId}List))]
            public ${RemoteTablePascalName}? ${ColumnPascalNameNoId} {get;set;}
        </#list>
    </#list>

<#--&lt;#&ndash;    当前列没有外键，外键在声明的表&ndash;&gt;-->
<#--    <#list column.getInverseForeignKeys() as foreignKey>-->
<#--&lt;#&ndash;        获取外键在声明的表的列&ndash;&gt;-->
<#--        <#list  foreignKey.getColumns() as fc >-->
<#--            <#if processedForeignKey[fc.getParent().getRawName() + fc.getRawName()]??>-->
<#--                <#continue >-->
<#--            </#if>-->
<#--            <#assign processedForeignKey = processedForeignKey + {fc.getParent().getRawName() + fc.getRawName():true}>-->

<#--            <#assign FcType=NameUtil.toPascalCase(fc.getParent().getRawName())>-->
<#--            <#assign ForeignKeyColumnPascalName = NameUtil.toPascalCase(fc.getRawName())>-->
<#--            <#assign ColumnNameFindId=fc.getRawName()?lowerCase?endsWith("_id")>-->
<#--            <#assign ForeignKeyColumnPascalNameNoId = ColumnNameFindId?then(ForeignKeyColumnPascalName?substring(0,ColumnPascalName?length-2),ForeignKeyColumnPascalName)>-->
<#--            /// <summary>-->
<#--            /// ${column.getRawComment()}-->
<#--            /// </summary>-->
<#--            /// <ref>${fc.getParent().getRawName()}</ref>-->
<#--            [InverseProperty(nameof(${FcType}.${ForeignKeyColumnPascalNameNoId}))]-->
<#--            public IList<${FcType}> ${FcType}${ForeignKeyColumnPascalNameNoId}List {get;set;} = new List<${FcType}>();-->
<#--        </#list>-->
<#--    </#list>-->

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
    [InverseProperty(nameof(${RemoteTablePascalName}.${RemoteColumnPascalNameNoId}))]
    public IList<${RemoteTablePascalName}> ${RemoteTablePascalName}${RemoteColumnPascalNameNoId}List {get;set;} = new List<${RemoteTablePascalName}>();
</#list>

    public void  fillInsertDefaultValue()
    {
<#list columns as column >
<#if column.hasPrimaryKey() && column.getMapperType() == "Guid">
        this.${NameUtil.toPascalCase(column.getRawName())} = Guid.CreateVersion7();
</#if>
</#list>
    }

    public void  fillUpdateDefaultValue()
    {
<#list columns as column >

</#list>
    }

}
