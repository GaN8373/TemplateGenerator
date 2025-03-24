package generator.interfaces

import com.intellij.database.model.DasObject

interface IRawDas<T : DasObject> {

    /**
     * 获取原始的 DasColumn 对象。
     *
     * @return the raw DasColumn object
     */
    fun getRawDas(): T

    /**
     * 获取列的原始名称。
     *
     * @return the raw name of the column
     */
    fun getRawName(): String {
        return this.getRawDas().name
    }

    /**
     * 获取列的原始注释文本。
     *
     * @return the raw comment string of the column
     */
    fun getRawComment(): String {
        return this.getRawDas().comment ?: ""
    }

}