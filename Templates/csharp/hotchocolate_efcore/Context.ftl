<#assign DbStructData=table.getParent()>
<#assign DbPascalName = NameUtil.toPascalCase(DbStructData.getRawName())>
<#assign DbContext = DbPascalName + "Context">

#region config
fileName=${DbContext}.cs
dir=Context
#endregion
<#---->
using Microsoft.EntityFrameworkCore;

public partial class ${DbPascalName}Context : DbContext {
    public ${DbPascalName}Context()
    {
    }

    public ${DbPascalName}Context(DbContextOptions<${DbContext}> options)
        : base(options)
    {
    }

    <#list DbStructData.getFullTable() as table>
        <#if table.getParent().getRawName() != DbStructData.getRawName()>
            <#continue >
        </#if>
        <#assign TablePascalName=NameUtil.toPascalCase(table.getRawName())>
        /// <summary>
        /// ${table.getRawComment()}
        /// </summary>
        public virtual DbSet<${TablePascalName}> ${TablePascalName}Set { get; set; }

    </#list>
}