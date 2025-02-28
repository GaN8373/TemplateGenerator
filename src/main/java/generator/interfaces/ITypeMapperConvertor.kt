package generator.interfaces

fun interface ITypeMapperConvertor {
    fun convert(rule: String, input: String): Boolean
}
