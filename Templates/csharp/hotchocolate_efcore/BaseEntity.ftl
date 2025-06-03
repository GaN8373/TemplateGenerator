<#assign DbStructData=table.getParent()>
<#assign DbPascalName = NameUtil.toPascalCase(DbStructData.getRawName())>
<#assign DbContext = DbPascalName + "Context">

#region config
fileName=${DbContext}.cs
dir=Entities
#endregion

public abstract class BaseEntity
{
    abstract public void fillInsertDefaultValue();
    abstract public void fillUpdateDefaultValue();

}