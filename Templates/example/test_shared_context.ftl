<#if context.containsKey("BaseEntity")>
    <#stop>
</#if>

<#assign DbStructData=table.getParent()>
<#assign DbPascalName = NameUtil.toPascalCase(DbStructData.getRawName())>
<#assign DbContext = DbPascalName + "Context">

#region config
fileName=test_shared_context.cs
#endregion

public abstract class BaseEntity
{
    public virtual void FillInsertDefaultValue(){}
    public virtual void FillUpdateDefaultValue(){}

}
${context.put("BaseEntity","true")}