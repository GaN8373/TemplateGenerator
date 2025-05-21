<#assign PascalCaseName=NameUtil.toPascalCase(table.getRawName())>

#region config
fileName=Query${PascalCaseName}.cs
dir=QueryEndpoint
#endregion

${table.getRawStatement()}

#region Columns
<#list columns as column>
    // statement
    ${column.getRawStatement()}
    // -----statement-----

    <#list column.getIndexList() as indexData>
        <#assign matchName = dbms.getName() + indexData.getRawStatement()>
        // tryTransformTo
        ${MapperUtil.tryTransformTo(matchName) ! "// fail"}
        ${indexData.getRawStatement()}
        // -----tryTransformTo---------
    </#list>

    <#list column.getForeignKeyList() as foreignKey>
        foreignKey: ${foreignKey.getRawName()}

        <#list foreignKey.getOtherColumn() as oc>
            name: ${oc.getRawName()}
            unique: ${oc.hasUnique()?c}
            primary: ${oc.hasPrimaryKey()?c}
            table: ${oc.getParent().getRawName()}
        </#list>
    </#list>
</#list>
#endregion