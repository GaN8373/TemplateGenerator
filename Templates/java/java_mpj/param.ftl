<#assign PascalCaseName=NameUtil.toPascalCase(table.getRawName())>
<#assign SnakeCaseName=NameUtil.toSnakeCase(table.getRawName())>
<#assign DbStructData=table.getParent()>

#region config
fileName=${PascalCaseName}Param.java
dir=param
#endregion

package ${namespace}.param;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
* ${table.getRawComment()}
* @author TemplateGeneratorPlugins
* @date ${.now?string('yyyy-MM-dd HH:mm:ss')}
*/
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class ${PascalCaseName}Param extends PageParam{
<#list columns as column>
    /**
    * ${column.getRawComment()}
    */
    ${column.getRawName()?starts_with("delet")?then('@JsonIgnore','')}
    @QueryField(QueryType.EQ)
    private ${column.getMapperType()} ${NameUtil.toCamelCase(column.getRawName())};
</#list>
}
