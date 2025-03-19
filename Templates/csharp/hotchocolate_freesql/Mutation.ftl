#region config
fileName=Mutation${NameUtil.toPascalCase(table.getRawName())}.cs
dir=MutationEndpoint
#endregion

using HotChocolate.Resolvers;
/// <summary>
/// 增删改 ${table.getRawComment()}
/// </summary>
[MutationType]
public static class Mutation${NameUtil.toPascalCase(table.getRawName())}
{
    /// <summary>
    /// 新增 ${table.getRawComment()}
    /// </summary>
    /// <param name="context"></param>
    /// <param name="input"></param>
    /// <returns></returns>
    public static async Task<${NameUtil.toPascalCase(table.getRawName())}> Add${NameUtil.toPascalCase(table.getRawName())}([Service]IFreeSql context, ${NameUtil.toPascalCase(table.getRawName())} input)
    {
        var result = await context.Insert(input).ExecuteInsertedAsync();
        return result.FirstOrDefault();
    }

    /// <summary>
    /// 修改 ${table.getRawComment()}
    /// </summary>
    /// <param name="context"></param>
    /// <param name="input"></param>
    /// <returns></returns>
    public static async Task<${NameUtil.toPascalCase(table.getRawName())}> Update${NameUtil.toPascalCase(table.getRawName())}([Service]IFreeSql context, ${NameUtil.toPascalCase(table.getRawName())} input)
    {
        var result = await context.Update<${NameUtil.toPascalCase(table.getRawName())}>().SetSource(input).ExecuteUpdatedAsync();
        return result.FirstOrDefault();
    }

    /// <summary>
    /// 删除 ${table.getRawComment()}
    /// </summary>
    /// <param name="context"></param>
    /// <param name="id"></param>
    /// <returns></returns> 
    public static async Task<${NameUtil.toPascalCase(table.getRawName())}> Delete${NameUtil.toPascalCase(table.getRawName())}([Service]IFreeSql context, long id)
    {
        var result = await context.Delete<${NameUtil.toPascalCase(table.getRawName())}>().Where(a => a.Id == id).ExecuteDeletedAsync();
        return result.FirstOrDefault();
    }

}
