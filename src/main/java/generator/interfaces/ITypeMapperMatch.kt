package generator.interfaces

fun interface ITypeMapperMatch {
    fun match(rule: String, input: String): Boolean
}
