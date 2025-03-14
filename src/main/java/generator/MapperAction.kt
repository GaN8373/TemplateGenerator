package generator

import generator.interfaces.ITypeMapperConvertor

/**
 * The order of the enum entries matters.
 */
enum class MapperAction(val convertor: ITypeMapperConvertor) {
    Regex(ITypeMapperConvertor { rule, input -> input.matches(rule.toRegex()) }),
    Eq(ITypeMapperConvertor { rule, input -> input == rule }),
    StartsWith(ITypeMapperConvertor { rule, input -> input.startsWith(rule) }),
    EndsWith(ITypeMapperConvertor { rule, input -> input.endsWith(rule) }),
    Contains(ITypeMapperConvertor { rule, input -> input.contains(rule) }),
}
