<#assign PascalCaseName=NameUtil.toPascalCase(table.getRawName())>

#region config
fileName=Query${PascalCaseName}.cs
dir=QueryEndpoint
#endregion

${table.getRawStatement()}
#region Columns
<#list columns as column>
    ${column.getRawStatement()}

    #region index
    <#list column.getIndexList() as indexData>
        <#assign matchName = dbms.getName() + indexData.getRawStatement()>
        ${MapperUtil.tryTransformTo(matchName) ! "// fail"}
        ${indexData.getRawStatement()}
    </#list>
    #endregion
</#list>
#endregion