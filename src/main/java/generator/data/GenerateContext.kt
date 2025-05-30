package generator.data

import com.intellij.database.psi.DbDataSource

data class GenerateContext(
   val typeMappingUnits: Collection<TypeMappingUnit>
    , val datasource: DbDataSource
)
