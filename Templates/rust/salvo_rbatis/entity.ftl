#region config
fileName=${table.getTableName()}_model.cs
dir=models
#endregion

<#assign PascalCaseName=NameUtil.toPascalCase(table.getTableName())>
<#assign SnakeCaseName=NameUtil.toSnakeCase(table.getTableName())>


use serde::{Serialize,Deserialize};
use rbatis::rbdc::datetime::DateTime;


#[derive(Debug,Serialize,Deserialize,Clone,ToParameters)]
#[serde(rename_all(deserialize ="camelCase"))]
#[salvo(parameters(rename_all="camelCase"))]
#[salvo(parameters(default_parameter_in = Query))]

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct ${PascalCaseName}{

<#list columns as column > 
    /**
     * ${column.getColumnComment()}
     */
    pub ${column.getColumnName()}:Option<${column.getMapperType()}>,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all(deserialize ="camelCase"))]
#[salvo(parameters(rename_all="camelCase"))]
#[salvo(parameters(default_parameter_in = Query))]
pub struct ${PascalCaseName}PagePayload{
    pub page_num:Option<u64>,
    pub page_size:Option<u64>,

<#list columns as column > 
    /**
     * ${column.getColumnComment()}
     */
    #[serde(rename(deserialize ="params[${column.getColumnName()}]"))]
    #[salvo(parameter(rename="params[${column.getColumnName()}]"))]
    #[salvo(parameter(value_type = Option<String>))]
    pub ${column.getColumnName()}:Option<${column.getMapperType()}>,

</#list>
}

rbatis::crud!(${PascalCaseName} {}, "${table.getTableName()}"); 