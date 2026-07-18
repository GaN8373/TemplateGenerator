<#assign PascalCaseName=NameUtil.toPascalCase(table.getRawName())>
<#assign SnakeCaseName=NameUtil.toSnakeCase(table.getRawName())>
<#assign DbStructData=table.getParent()>

#region config
fileName=I${PascalCaseName}Service.java
dir=service
#endregion

package ${namespace}.service;
import ${namespace}.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* ${table.getRawComment()}
* @author TemplateGeneratorPlugins
* @date ${.now?string('yyyy-MM-dd HH:mm:ss')}
*/
public interface I${PascalCaseName}Service extends MPJBaseService<${PascalCaseName}> {


      @Data
      class ${PascalCaseName}UpdateParam {
           <#list columns as column>
              private ${column.getMapperType()} ${NameUtil.toCamelCase(column.getRawName())};
            </#list>
            @JsonDeserialize(contentAs = String.class)
            private Set<String> deleteIdList;
      }
}

