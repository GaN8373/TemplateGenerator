<#assign PascalCaseName=NameUtil.toPascalCase(table.getRawName())>
<#assign SnakeCaseName=NameUtil.toSnakeCase(table.getRawName())>
<#assign DbStructData=table.getParent()>

#region config
fileName=${SnakeCaseName}.rs
dir=entity
#endregion

use salvo::prelude::Extractible;
use sea_orm::entity::prelude::*;
use serde::{Deserialize, Serialize};

#[derive(Clone, Debug, PartialEq, Eq, DeriveEntityModel, Extractible, Deserialize, Serialize)]
#[sea_orm(table_name = "${table.getRawName()}"<#if DbStructData.hasSchema()>, schema_name = "${DbStructData.getRawName()}"</#if>)]
pub struct Model {
    <#list columns as column >

    #[sea_orm(column_name = "${column.getRawName()}" <#if column.hasPrimaryKey()>, primary_key</#if>)]
    pub ${NameUtil.toSnakeCase(column.getRawName())}: ${column.getMapperType()},

    </#list>
}

#[derive(Copy, Clone, Debug, EnumIter, DeriveRelation)]
pub enum Relation {}

impl ActiveModelBehavior for ActiveModel {}
