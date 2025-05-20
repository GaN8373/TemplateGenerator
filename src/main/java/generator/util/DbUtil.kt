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
        return db.filter{it.findElement(das) != null}.findFirst()
            .orElseThrow { Exception("das can not convert to db abstract. dbname is empty, das  is ${das.name}") }
    }

    @JvmStatic
    fun getDbTable(db: DbDataSource, das: DasTable): DbTable {
        return db.findElement(das) as DbTable
    }


    @JvmStatic
    private fun findDatasourceName(das: DasObject): DasObject {
        return when (das.kind) {
            ObjectKind.DATABASE -> return das
            ObjectKind.ROOT -> throw Exception("find root das abstract, not find database kind, das  is ${das.name}")
            else -> {
                if (das.dasParent == null) {
                    throw Exception("can not find database kind, das can not convert to db abstract. das  is ${das.name}")
                }
                findDatasourceName(das.dasParent!!)
            }
        }
    }

}
