<#if context.containsKey("BaseEntity")>
    <#stop>
</#if>

<#assign DbStructData=table.getParent()>
<#assign DbPascalName = NameUtil.toPascalCase(DbStructData.getRawName())>
<#assign DbContext = "IFreeSql">

#region config
fileName=BaseEntity.cs
dir=Entities
#endregion

public abstract class BaseEntity
{
  
}

public enum BaseStatus
{
    /// <summary>
    ///     启用
    /// </summary>
    Enable = 0,

    /// <summary>
    ///     禁用
    /// </summary>
    Disable = 1
}

${context.put("BaseEntity","true")}