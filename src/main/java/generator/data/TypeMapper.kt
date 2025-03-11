package generator.data

import generator.MapperAction

class TypeMapper : Comparable<TypeMapper> {
    var action: MapperAction = MapperAction.Eq
    var rule: String = ""
    var type: String = ""


    constructor(action: MapperAction, rule: String, type: String) {
        this.action = action
        this.rule = rule
        this.type = type
    }

    constructor()

    override fun compareTo(other: TypeMapper): Int {
        val compareTo = action.ordinal - other.action.ordinal

        if (compareTo != 0) {
            return compareTo
        }

        return rule.compareTo(other.rule)
    }
}
