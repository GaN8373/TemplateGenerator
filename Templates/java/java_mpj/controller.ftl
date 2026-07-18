<#assign PascalCaseName=NameUtil.toPascalCase(table.getRawName())>
<#assign CamelCaseName=NameUtil.toCamelCase(table.getRawName())>
<#assign SnakeCaseName=NameUtil.toSnakeCase(table.getRawName())>
<#assign DbStructData=table.getParent()>

#region config
fileName=${PascalCaseName}Controller.java
dir=controller
#endregion

package ${namespace}.controller;
import ${namespace}.*;


import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

/**
* ${table.getRawComment()}
* @author TemplateGeneratorPlugins
* @date ${.now?string('yyyy-MM-dd HH:mm:ss')}
*/
@RestController
@RequestMapping("/api/${PascalCaseName}")
@RequiredArgsConstructor
public class ${PascalCaseName}Controller {
    /**
     * 服务对象
     */
    private final I${PascalCaseName}Service ${CamelCaseName}Service;

    /**
     * 分页查询所有数据
     *
     * @return 所有数据
     */
    @PostMapping("/page")
    public HttpResponseEntity<PageResult<${PascalCaseName}>> page(@RequestBody PageQuery<${PascalCaseName}Param> param) {
        var page = ${CamelCaseName}Service.pageParam(ParamToQueryUtil.convertToPage(param), param.getQuery());
        return HttpResponseEntity.ok(PageUtils.buildPageResultVO(page));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/{id}")
    public HttpResponseEntity<${PascalCaseName}> selectOne(@PathVariable Serializable id) {
        return HttpResponseEntity.ok(${CamelCaseName}Service.getById(id));
    }

    /**
     * 删除数据
     *
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public HttpResponseEntity<Boolean> delete(@RequestBody ${PascalCaseName}UpdateParam param) {
        if (param.getDeleteIdList() == null || param.getDeleteIdList().isEmpty()) {
            return HttpResponseEntity.error("参数不支持");
        }

        return HttpResponseEntity.ok(${CamelCaseName}Service.removeByIds(param.getDeleteIdList()));
    }
}

