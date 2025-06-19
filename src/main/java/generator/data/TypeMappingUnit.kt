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

    @JsonAlias("sortIndex", "SortIndex")
    var sortIndex: Int? = 100
        get() = field ?: 100

    constructor(action: MapperAction, rule: String, type: String): this(action, rule, type,null)

    constructor(action: MapperAction, rule: String, type: String, sortIndex: Int?) {
        this.action = action
        this.rule = rule
        this.type = type
        this.sortIndex = sortIndex
    }

    companion object {
        @JvmStatic
        fun of(x: TypeMappingUnit): TypeMappingUnit {
            return TypeMappingUnit(x.action, x.rule, x.type,x.sortIndex)
        }


        @JvmStatic
        fun newDefault(): TypeMappingUnit {
            return TypeMappingUnit(MapperAction.Eq, "", "", null)
        }
    }


    constructor()

    override fun compareTo(other: TypeMappingUnit): Int {
        val sortSeq = this.sortIndex!!.compareTo(other.sortIndex!!)
        if (sortSeq != 0) {
            return sortSeq
        }

        val compareTo = action.ordinal - other.action.ordinal

        if (compareTo != 0) {
            return compareTo
        }

        return rule.compareTo(other.rule)
    }
}
