<#assign PascalCaseName=NameUtil.toPascalCase(table.getRawName())>
<#assign SnakeCaseName=NameUtil.toSnakeCase(table.getRawName())>

#region config
fileName=${SnakeCaseName}_service.rs
dir=service
#endregion

pub struct ${PascalCaseName}Query;

impl ${PascalCaseName}Query {
    pub async fn find_by_id(db: &DbConn, id: i32) -> Result<Option<${SnakeCaseName}::Model>, DbErr> {
        ${SnakeCaseName}::Entity::find_by_id(id).one(db).await
    }

    /// If ok, returns (post models, num pages).
    pub async fn find_in_page(
        db: &DbConn,
        page: u64,
        per_page: u64,
    ) -> Result<(Vec<${SnakeCaseName}::Model>, u64), DbErr> {
        // Setup paginator
        let paginator = ${SnakeCaseName}::Entity::find()
        <#list table.getPrimaryColumns() as column >
            .order_by_desc(${SnakeCaseName}::Column::${NameUtil.toPascalCase(column.getRawName())})
        </#list>
            .paginate(db, per_page);
        let num_pages = paginator.num_pages().await?;

        // Fetch paginated posts
        paginator.fetch_page(page - 1).await.map(|p| (p, num_pages))
    }
}