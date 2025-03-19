<#assign PascalCaseName=NameUtil.toPascalCase(table.getRawName())>
<#assign SnakeCaseName=NameUtil.toSnakeCase(table.getRawName())>

#region config
fileName=${SnakeCaseName}_api.rs
dir=api
#endregion


#[handler]
async fn list(req: &mut Request, depot: &mut Depot) -> Result<Text<String>> {
    let state = depot
        .obtain::<AppState>()
        .ok_or_else(StatusError::internal_server_error)?;
    let conn = &state.conn;

    let page = req.query("page").unwrap_or(1);
    let per_page = req
        .query("per_page")
        .unwrap_or(DEFAULT_POSTS_PER_PAGE);

    let (posts, num_pages) = ${PascalCaseName}Query::find_in_page(conn, page, per_page)
        .await
        .map_err(|_| StatusError::internal_server_error())?;

    let mut ctx = tera::Context::new();
    ctx.insert("posts", &posts);
    ctx.insert("page", &page);
    ctx.insert("per_page", &per_page);
    ctx.insert("num_pages", &num_pages);

    let body = state
        .templates
        .render("index.html.tera", &ctx)
        .map_err(|_| StatusError::internal_server_error())?;
    Ok(Text::Html(body))
}
