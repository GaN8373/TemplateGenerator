<#assign PascalCaseName=NameUtil.toPascalCase(table.getRawName())>
<#assign SnakeCaseName=NameUtil.toSnakeCase(table.getRawName())>
<#assign DbStructData=table.getParent()>

#region config
fileName=${PascalCaseName}Mapper.java
dir=mapper
#endregion

package ${namespace}.mapper;

import ${namespace}.*;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* ${table.getRawComment()}
* @author TemplateGeneratorPlugins
* @date ${.now?string('yyyy-MM-dd HH:mm:ss')}
*/
@Mapper
public interface ${PascalCaseName}Mapper extends MPJBaseMapper<${PascalCaseName}> {


}

