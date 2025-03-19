package generator.data.table

import com.intellij.database.model.ObjectKind
import com.intellij.database.psi.DbElement

class DbStructData (val parent: DbElement?) {
    fun getRawName(): String{
        return parent?.name ?: ""
    }

    fun hasSchema(): Boolean{
        return parent?.kind == ObjectKind.SCHEMA
    }

    fun hasDatabase(): Boolean{
        return parent?.kind == ObjectKind.DATABASE
    }
}
