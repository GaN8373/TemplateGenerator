package generator.data

import com.fasterxml.jackson.annotation.JsonAlias
import generator.MapperAction

class TypeMappingUnit : Comparable<TypeMappingUnit> {
    @JsonAlias("action", "Action")
    var action: MapperAction = MapperAction.Eq
    @JsonAlias("rule", "Rule")
    var rule: String = ""
    @JsonAlias("type", "Type")
    var type: String = ""


    constructor(action: MapperAction, rule: String, type: String) {
        this.action = action
        this.rule = rule
        this.type = type
    }

    constructor()

    override fun compareTo(other: TypeMappingUnit): Int {
        val compareTo = action.ordinal - other.action.ordinal

        if (compareTo != 0) {
            return compareTo
        }

        return rule.compareTo(other.rule)
    }
}
