<#if context.containsKey("BaseEntity")>
    <#stop>
</#if>

<#assign DbStructData=table.getParent()>
<#assign DbPascalName = NameUtil.toPascalCase(DbStructData.getRawName())>
<#assign DbContext = DbPascalName + "Context">

#region config
fileName=BaseEntity.cs
dir=Entities
#endregion

public abstract class BaseEntity
{
    /// <summary>
    /// 插入时填充默认值
    /// </summary>
    public virtual void FillInsertDefaultValue(){}
    /// <summary>
    /// 更新时填充默认值
    /// </summary>
    public virtual void FillUpdateDefaultValue(){}

}
${context.put("BaseEntity","true")}