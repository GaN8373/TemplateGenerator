package generator.data.table

import com.intellij.database.model.DasObject
import com.intellij.database.model.ObjectKind

@Suppress("unused")
class DbStructData(private val parent: DasObject?) {

    fun getRawName(): String {
        return parent?.name ?: ""
    }

    fun hasSchema(): Boolean {
        return parent?.kind == ObjectKind.SCHEMA
    }

    fun hasDatabase(): Boolean {
        return parent?.kind == ObjectKind.DATABASE
    }
}
