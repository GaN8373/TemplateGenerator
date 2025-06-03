<#assign DbStructData=table.getParent()>
<#assign DbPascalName = NameUtil.toPascalCase(DbStructData.getRawName())>
<#assign DbContext = DbPascalName + "Context">
<#assign TablePascalCaseName=NameUtil.toPascalCase(table.getRawName())>
<#assign TableSnakeCaseName=NameUtil.toSnakeCase(table.getRawName())>
<#assign primarys=table.getPrimaryColumns()>

#region config
fileName=Mutation${TablePascalCaseName}.cs
dir=Endpoint
#endregion

using HotChocolate.Resolvers;
/// <summary>
/// 增删改 ${table.getRawComment()}
/// </summary>
[MutationType]
public static class Mutation${TablePascalCaseName}
{
    /// <summary>
    /// 新增 ${table.getRawComment()}
    /// </summary>
    /// <param name="context">auto injected</param>
    /// <param name="input">add param</param>
    /// <returns></returns>
    public static async Task<${TablePascalCaseName}?> Add${TablePascalCaseName}([Service]${DbContext} context, ${TablePascalCaseName} input)
    {
        input.fillInsertDefaultValue();
        var result = await context.AddAsync(input);
        await context.SaveChangesAsync();
        return result.Entity;
    }

    /// <summary>
    /// 修改 ${table.getRawComment()}
    /// </summary>
    /// <param name="context">auto injected</param>
    /// <param name="input">update param</param>
    /// <returns></returns>
    public static async Task<${TablePascalCaseName}?> Update${TablePascalCaseName}([Service]${DbContext} context, ${TablePascalCaseName} input)
    {
        input.fillUpdateDefaultValue();
        var result = context.Update(input);
        await context.SaveChangesAsync();
        return result.Entity;
    }

    /// <summary>
    /// 删除 ${table.getRawComment()}
    /// </summary>
    /// <param name="context">auto injected</param>
    <#list primarys as column >
    /// <param name="${NameUtil.toCamelCase(column.getRawName())}">${column.getRawComment()}</param>
        </#list>
    /// <returns></returns> 
    public static async Task<int?> Delete${TablePascalCaseName}([Service]IFreeSql context,
<#list primarys as column >  ${column.getMapperType()} ${NameUtil.toCamelCase(column.getRawName())}</#list>)
    {
        return await context.${TablePascalCaseName}Set
        <#list primarys as column >
        .Where(x => x.${NameUtil.toPascalCase(column.getRawName())} == ${NameUtil.toCamelCase(column.getRawName())})
        </#list>
        .DeleteAsync();
 }

}
