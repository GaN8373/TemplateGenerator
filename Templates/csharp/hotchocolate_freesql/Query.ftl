#region config
fileName=Query${NameUtil.toPascalCase(table.getTableName())}.cs
dir=QueryEndpoint
#endregion

using HotChocolate.Resolvers;

[QueryType]
public static class Query${NameUtil.toPascalCase(table.getTableName())}
{
    [UsePaging]
    [UseProjection]
    [UseFiltering]
    [UseSorting]
    public static IQueryable<${NameUtil.toPascalCase(table.getTableName())}> QueryDemand(IResolverContext context, [Service] IFreeSql db)
    {
        return db.Select<${NameUtil.toPascalCase(table.getTableName())}>().AsQueryable();
    }
}
