#region config
fileName=Query${NameUtil.toPascalCase(table.getRawName())}.cs
dir=QueryEndpoint
#endregion

using HotChocolate.Resolvers;

/// <summary>
/// 查询 ${table.getRawComment()}
/// </summary>

[QueryType]
public static class Query${NameUtil.toPascalCase(table.getRawName())}
{
    [UsePaging]
    [UseProjection]
    [UseFiltering]
    [UseSorting]
    public static IQueryable<${NameUtil.toPascalCase(table.getRawName())}> Query${NameUtil.toPascalCase(table.getRawName())}(IResolverContext context, [Service] IFreeSql db)
    {
        return db.Select<${NameUtil.toPascalCase(table.getRawName())}>().AsQueryable();
    }
}
