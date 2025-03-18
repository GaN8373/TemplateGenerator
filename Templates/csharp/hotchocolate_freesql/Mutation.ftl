#region config
fileName=Mutation${NameUtil.toPascalCase(table.getTableName())}.cs
dir=MutationEndpoint
#endregion

using HotChocolate.Resolvers;
/// <summary>
/// 增删改 ${table.getComment()}
/// </summary>
[MutationType]
public static class Mutation${NameUtil.toPascalCase(table.getTableName())}
{
    /// <summary>
    /// 新增 ${table.getComment()}
    /// </summary>
    /// <param name="context"></param>
    /// <param name="input"></param>
    /// <returns></returns>
    public static async Task<${NameUtil.toPascalCase(table.getTableName())}> Add${NameUtil.toPascalCase(table.getTableName())}([Service]IFreeSql context, ${NameUtil.toPascalCase(table.getTableName())} input)
    {
        var result = await context.Insert(input).ExecuteInsertedAsync();
        return result.FirstOrDefault();
    }

    /// <summary>
    /// 修改 ${table.getComment()}
    /// </summary>
    /// <param name="context"></param>
    /// <param name="input"></param>
    /// <returns></returns>
    public static async Task<${NameUtil.toPascalCase(table.getTableName())}> Update${NameUtil.toPascalCase(table.getTableName())}([Service]IFreeSql context, ${NameUtil.toPascalCase(table.getTableName())} input)
    {
        var result = await context.Update<${NameUtil.toPascalCase(table.getTableName())}>().SetSource(input).ExecuteUpdatedAsync();
        return result.FirstOrDefault();
    }

    /// <summary>
    /// 删除 ${table.getComment()}
    /// </summary>
    /// <param name="context"></param>
    /// <param name="id"></param>
    /// <returns></returns> 
    public static async Task<${NameUtil.toPascalCase(table.getTableName())}> Delete${NameUtil.toPascalCase(table.getTableName())}([Service]IFreeSql context, long id)
    {
        var result = await context.Delete<${NameUtil.toPascalCase(table.getTableName())}>().Where(a => a.Id == id).ExecuteDeletedAsync();
        return result.FirstOrDefault();
    }

}
