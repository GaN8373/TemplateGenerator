package generator.data

import generator.MapperAction

class TypeMapper(val action: MapperAction, val rule: String, val type: String) : Comparable<TypeMapper> {

    override fun compareTo(other: TypeMapper): Int {
        val compareTo = action.ordinal - other.action.ordinal

        if (compareTo != 0) {
            return compareTo
        }

        return rule.compareTo(other.rule)
    }
}
