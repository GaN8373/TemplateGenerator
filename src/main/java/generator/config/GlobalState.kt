package generator.config

import generator.data.TypeMapper

class GlobalState {
    val groupMapTemplate: Map<String, Collection<TypeMapper>> = HashMap()


    fun getTemplates(templateGroup: String?): Set<TypeMapper> {
        return groupMapTemplate[templateGroup]?.toSet() ?: emptySet()
    }
}