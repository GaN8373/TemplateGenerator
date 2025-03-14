package generator.interfaces

fun interface ITypeMapperConvertor {
    fun match(rule: String, input: String): Boolean
}
