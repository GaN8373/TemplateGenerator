<#assign PascalCaseName=NameUtil.toPascalCase(table.getRawName())>
<#assign SnakeCaseName=NameUtil.toSnakeCase(table.getRawName())>
<#assign primarys=table.getPrimaryColumns()>
<#assign DbStructData=table.getParent()>
#region config
fileName=Mutation${PascalCaseName}.cs
dir=MutationEndpoint
#endregion

using HotChocolate.Resolvers;
/// <summary>
/// 增删改 ${table.getRawComment()}
/// </summary>
[MutationType]
public static class Mutation${PascalCaseName}
{
    /// <summary>
    /// 新增 ${table.getRawComment()}
    /// </summary>
    /// <param name="context">auto injected</param>
    /// <param name="input">add param</param>
    /// <returns></returns>
    public static async Task<${PascalCaseName}> Add${PascalCaseName}([Service]IFreeSql context, ${PascalCaseName} input)
    {
        var result = await context.Insert(input).ExecuteInsertedAsync();
        return result.FirstOrDefault();
    }

    /// <summary>
    /// 修改 ${table.getRawComment()}
    /// </summary>
    /// <param name="context">auto injected</param>
    /// <param name="input">update param</param>
    /// <returns></returns>
    public static async Task<${PascalCaseName}> Update${PascalCaseName}([Service]IFreeSql context, ${PascalCaseName} input)
    {
        var result = await context.Update<${PascalCaseName}>().SetSource(input).ExecuteUpdatedAsync();
        return result.FirstOrDefault();
    }

    /// <summary>
    /// 删除 ${table.getRawComment()}
    /// </summary>
    /// <param name="context">auto injected</param>
    <#list primarys as column >
    /// <param name="${NameUtil.toCamelCase(column.getRawName())}">${column.getRawComment()}</param>
        </#list>
    /// <returns></returns> 
    public static async Task<${PascalCaseName}> Delete${PascalCaseName}([Service]IFreeSql context,<#list primarys as column >  ${column.getMapperType()} ${NameUtil.toCamelCase(column.getRawName())}</#list>)
    {
        var result = await context.Delete<${PascalCaseName}>()
        <#list primarys as column >
        .Where(x => x.${NameUtil.toPascalCase(column.getRawName())} == ${NameUtil.toCamelCase(column.getRawName())})
        </#list>
        .ExecuteDeletedAsync();
        return result.FirstOrDefault();
    }

}
