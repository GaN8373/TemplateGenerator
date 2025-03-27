package generator.util

import com.intellij.database.model.DasObject
import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import com.intellij.database.psi.DbDataSource
import com.intellij.database.psi.DbPsiFacade
import com.intellij.database.psi.DbTable
import com.intellij.openapi.project.Project
import java.util.stream.Stream

object DbUtil {

    @JvmStatic
    fun getAllDatasource(project: Project): Stream<DbDataSource> {
        val instance = DbPsiFacade.getInstance(project)
        return instance.dataSources.stream()
    }

    @JvmStatic
    fun getDatasource(db: Stream<DbDataSource>, das: DasObject): DbDataSource? {
        val dbname = findDatasourceName(das)
        if (dbname.isNullOrEmpty()) {
            return null
        }

        return db.filter { it.name.startsWith(dbname) }
            .findFirst().orElse(null)
    }

    @JvmStatic
    fun getDbTable(db: DbDataSource, das: DasTable): DbTable {
        return db.findElement(das) as DbTable
    }


    @JvmStatic
    private fun findDatasourceName(das: DasObject): String? {
        return when (das.kind) {
            ObjectKind.DATABASE -> return das.name
            ObjectKind.ROOT -> return null
            else -> {
                if (das.dasParent == null) {
                    return null
                }
                findDatasourceName(das.dasParent!!)
            }
        }
    }

}
