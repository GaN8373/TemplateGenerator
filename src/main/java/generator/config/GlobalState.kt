package generator.config

import generator.data.TypeMapper
import java.util.LinkedHashSet

class GlobalState {
    val groupMapTemplate: MutableMap<String, Collection<TypeMapper>> = HashMap()
    var historyUsePath: LinkedHashSet<String> = LinkedHashSet()

    fun getTemplates(templateGroup: String?): Set<TypeMapper> {
        return groupMapTemplate[templateGroup]?.toSet() ?: emptySet()
    }
}