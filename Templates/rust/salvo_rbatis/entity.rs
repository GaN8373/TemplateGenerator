<#assign PascalCaseName=NameUtil.toPascalCase(table.getRawName())>
<#assign SnakeCaseName=NameUtil.toSnakeCase(table.getRawName())>

#region config
fileName=${SnakeCaseName}_model.rs
dir=models
#endregion


use serde::{Serialize,Deserialize};
use rbatis::rbdc::datetime::DateTime;

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct ${PascalCaseName}{

<#list columns as column > 
    /**
     * ${column.getRawComment()}
     */
    pub ${NameUtil.toSnakeCase(column.getRawName())}:Option<${column.getMapperType()}>,
</#list>
}

rbatis::crud!(${PascalCaseName} {}, "${table.getRawName()}"); 