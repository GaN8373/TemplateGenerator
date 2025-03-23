package generator.interfaces

import com.intellij.database.model.DasObject

interface IRawDas<T : DasObject> {

    fun getRawDas(): T

    fun getRawName(): String {
        return this.getRawDas().name
    }

    fun getRawComment(): String {
        return this.getRawDas().comment ?: ""
    }
}