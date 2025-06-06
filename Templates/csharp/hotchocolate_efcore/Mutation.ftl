<#assign DbStructData=table.getParent()>
<#assign DbPascalName = NameUtil.toPascalCase(DbStructData.getRawName())>
<#assign DbContext = "BizContext">
<#assign TablePascalCaseName=NameUtil.toPascalCase(table.getRawName())>
<#assign TableSnakeCaseName=NameUtil.toSnakeCase(table.getRawName())>
<#assign primarys=table.getPrimaryColumns()>

#region config
fileName=Mutation${TablePascalCaseName}.cs
dir=${DbPascalName}/Endpoint
#endregion
using HotChocolate.Resolvers;
using LinqToDB;
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
        input.FillInsertDefaultValue();
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
        input.FillUpdateDefaultValue();
        var result = context.Update(input);
        await context.SaveChangesAsync();
        return result.Entity;
    }

    /// <summary>
    /// 删除 ${table.getRawComment()}
    /// </summary>
    /// <param name="context">auto injected</param>
    /// <param name="input">delete condition</param>
    /// <returns></returns>
    public static async Task<int> Delete${TablePascalCaseName}([Service]${DbContext} context,
    <#if primarys?size == 1>
        ${primarys[0].getMapperType()} input)
    <#else>
        List<Delete${TablePascalCaseName}Input> input)
    </#if>
    {
        return await context.${TablePascalCaseName}Set
    <#if primarys?size == 1>
        .Where(x => input == x.${NameUtil.toPascalCase(primarys[0].getRawName())})
    <#else>
        .Where(x => input.Contains(
            new (){ <#list primarys as column >${NameUtil.toPascalCase(column.getRawName())} = x.${NameUtil.toPascalCase(column.getRawName())}<#if !column?is_last>, </#if></#list> }
        ))
    </#if>
        .DeleteAsync();
    }
}

<#if primarys?size gt 1>
    public class Delete${TablePascalCaseName}Input
    {
        <#list primarys as column >
           public ${column.getMapperType()} ${NameUtil.toPascalCase(column.getRawName())} {get; set;}
        </#list>
    }
</#if>