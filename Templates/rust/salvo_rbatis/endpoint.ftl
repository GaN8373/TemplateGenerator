#region config
fileName=${table.getTableName()}_controller.cs
dir=controller
#endregion

<#assign PascalCaseName=NameUtil.toPascalCase(table.getTableName())>
<#assign SnakeCaseName=NameUtil.toSnakeCase(table.getTableName())>

/// 分页列表
#[endpoint(
    tags("${table.getComment()}"),
    parameters(
        ${PascalCaseName}PagePayload
    ),
    responses(
        (status_code = 200,body=ResObj<Page<${PascalCaseName}List>>,description ="table.getComment()列表")
    ),
)]
pub async fn get_${SnakeCaseName}_by_page(req:&mut Request)->Res<Page<${PascalCaseName}List>>{
    let payload:${PascalCaseName}PagePayload = req.parse_queries().unwrap();
    match_ok::<Page<${PascalCaseName}List>>(role_service::get_role_by_page(
        payload.page_num.map_or(1,|v|v),
        payload.page_size.map_or(10,|v|v),
        payload.role_name,
        payload.role_key,
        payload.status,
        payload.begin_time,
        payload.end_time,
    ).await)
}
