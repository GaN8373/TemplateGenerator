<#assign DbStructData=table.getParent()>
<#assign DbPascalName = NameUtil.toPascalCase(DbStructData.getRawName())>
<#assign DbContext = DbPascalName + "Context">

#region config
fileName=BaseEntity.cs
#endregion

public abstract class BaseEntity
{
    public virtual void FillInsertDefaultValue(){}
    public virtual void FillUpdateDefaultValue(){}

}