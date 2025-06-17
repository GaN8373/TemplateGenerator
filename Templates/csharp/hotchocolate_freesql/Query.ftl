<#assign PascalCaseName=NameUtil.toPascalCase(table.getRawName())>
<#assign SnakeCaseName=NameUtil.toSnakeCase(table.getRawName())>
<#assign DbStructData=table.getParent()>
<#assign primarys=table.getPrimaryColumns()>

#region config
fileName=Query${PascalCaseName}.cs
dir=Endpoint
#endregion
using GaN8373.HotChocolate.Extensions.FreeSQL.Extensions;
using GaN8373.HotChocolate.Extensions.FreeSQL.Factories;
using HotChocolate.Resolvers;
using HotChocolate.Types.Pagination;


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
    public static Connection<${PascalCaseName}> Get${PascalCaseName}(IResolverContext context, [Service] IFreeSql db)
    {
        var select = context.FillGraphqlParams<${PascalCaseName}>(db, out var paging);
        var projectionSelector = context.TryExtractProjectionSelector<${PascalCaseName}>(x => new ${PascalCaseName}{
            <#list columns as column >
                <#if column.hasPrimaryKey() || column.getRawName()?lower_case?ends_with("id")>
                    <#assign ColumnPascalName = NameUtil.toPascalCase(column.getRawName())>
                    ${ColumnPascalName} = x.${ColumnPascalName}${column?is_last?then("",",")}
                </#if>
            </#list>
        });

        var list = projectionSelector == null ? [] : select.ToList(projectionSelector);

        return ConnectionFactory.CreateConnection(list, paging, x => $"<#list primarys as column >{x.${NameUtil.toPascalCase(column.getRawName())}}</#list>");
    }
}
