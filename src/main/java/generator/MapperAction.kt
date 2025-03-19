package generator

import generator.interfaces.ITypeMapperMatch

/**
 * The order of the enum entries matters.
 */
enum class MapperAction(val match: ITypeMapperMatch) {
    Regex(ITypeMapperMatch { rule, input -> input.matches(rule.toRegex()) }),
    Eq(ITypeMapperMatch { rule, input -> input == rule }),
    StartsWith(ITypeMapperMatch { rule, input -> input.startsWith(rule) }),
    EndsWith(ITypeMapperMatch { rule, input -> input.endsWith(rule) }),
    Contains(ITypeMapperMatch { rule, input -> input.contains(rule) }),
}
