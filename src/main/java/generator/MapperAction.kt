package generator

import generator.interfaces.ITypeMapperConvertor


enum class MapperAction(val convertor: ITypeMapperConvertor) {
    Regex(ITypeMapperConvertor { rule, input -> input.matches(rule.toRegex()) }),
    Contains(ITypeMapperConvertor { rule, input -> input.contains(rule) }),
    StartsWith(ITypeMapperConvertor { rule, input -> input.startsWith(rule) }),
    Eq(ITypeMapperConvertor { rule, input -> input == rule })
}
