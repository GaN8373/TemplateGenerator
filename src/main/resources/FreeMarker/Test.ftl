/**
* ${table.getComment()}
*
* @Date ${.now?string('yyyy-MM-dd HH:mm:ss')}
*/
public class ${NameUtil.toPascalCase(table.getName())} {

<#list columns as column >
    /**
    * ${column.getColumnComment()}
    */
    <#if column.hasPrimaryKey() == true>
    [PrimaryKey]
    </#if>
    private ${column.getMapperType()} ${column.getColumnName()};
</#list>

}
