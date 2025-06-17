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
    public BaseStatus Status { get; set; } = BaseStatus.Enable;
    ${context.put("BaseEntity.Status","true")}

    /// <summary>
    /// 插入时填充默认值
    /// </summary>
    public virtual void FillInsertDefaultValue(){}
    /// <summary>
    /// 更新时填充默认值
    /// </summary>
    public virtual void FillUpdateDefaultValue(){}

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