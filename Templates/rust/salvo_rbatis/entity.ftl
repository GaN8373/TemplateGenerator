#region config
fileName=${table.getRawName()}_model.cs
dir=models
#endregion

<#assign PascalCaseName=NameUtil.toPascalCase(table.getRawName())>
<#assign SnakeCaseName=NameUtil.toSnakeCase(table.getRawName())>


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
    pub ${column.getRawName()}:Option<${column.getMapperType()}>,
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
    #[serde(rename(deserialize ="params[${column.getRawName()}]"))]
    #[salvo(parameter(rename="params[${column.getRawName()}]"))]
    #[salvo(parameter(value_type = Option<String>))]
    pub ${column.getRawName()}:Option<${column.getMapperType()}>,

</#list>
}

rbatis::crud!(${PascalCaseName} {}, "${table.getRawName()}"); 