<#assign DbStructData=table.getParent()>
<#assign DbPascalName = NameUtil.toPascalCase(DbStructData.getRawName())>
<#assign DbContext = "BizContext">
<#---->
<#if context.containsKey(DbContext)>
    <#stop>
</#if>
<#---->
#region config
fileName=${DbContext}.cs
dir=Context
#endregion
<#---->
using Microsoft.EntityFrameworkCore;

public partial class ${DbContext} : DbContext {
    public ${DbContext}()
    {
    }

    public ${DbContext}(DbContextOptions<${DbContext}> options)
        : base(options)
    {
    }
    <#assign ProcessTables = []>
    <#list DbStructData.getFullTable() as table>
<#--        自定义名称-->
        <#if !table.getParent().getRawName()?starts_with("service_")>
            <#continue >
        </#if>
        <#assign ProcessTables  = ProcessTables + [table]>
        <#assign TablePascalName=NameUtil.toPascalCase(table.getRawName())>
        /// <summary>
        /// ${table.getRawComment()}
        /// </summary>
        public virtual DbSet<${TablePascalName}> ${TablePascalName}Set { get; set; }

    </#list>

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
<#--    <#list ProcessTables as table>-->
<#--      -->
<#--    </#list>-->
    }
}
${context.put(DbContext,"true")}