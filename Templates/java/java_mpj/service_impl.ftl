<#assign PascalCaseName=NameUtil.toPascalCase(table.getRawName())>
<#assign SnakeCaseName=NameUtil.toSnakeCase(table.getRawName())>
<#assign DbStructData=table.getParent()>

#region config
fileName=${PascalCaseName}ServiceImpl.java
dir=service/impl
#endregion

package ${namespace}.service.impl;

import ${namespace}.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
* ${table.getRawComment()}
* @author TemplateGeneratorPlugins
* @date ${.now?string('yyyy-MM-dd HH:mm:ss')}
*/
@Service
public class ${PascalCaseName}ServiceImpl extends ServiceImpl<${PascalCaseName}Mapper, ${PascalCaseName}> implements I${PascalCaseName}Service {

}

