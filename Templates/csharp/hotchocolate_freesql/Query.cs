<#assign PascalCaseName=NameUtil.toPascalCase(table.getRawName())>
<#assign SnakeCaseName=NameUtil.toSnakeCase(table.getRawName())>
<#assign DbStructData=table.getParent()>
#region config
fileName=Query${PascalCaseName}.cs
dir=Endpoint
#endregion

using HotChocolate.Resolvers;

/// <summary>
/// 查询 ${table.getRawComment()}
/// </summary>

[QueryType]
public static class Query${PascalCaseName}
{
    /// <summary>
    /// 查询 ${table.getRawComment()}
    /// </summary>
    [UsePaging]
    [UseProjection]
    [UseFiltering]
    [UseSorting]
    public static IQueryable<${PascalCaseName}> Get${PascalCaseName}(IResolverContext context, [Service] IFreeSql db)
    {
        return db.Select<${PascalCaseName}>().AsQueryable();
    }
}
