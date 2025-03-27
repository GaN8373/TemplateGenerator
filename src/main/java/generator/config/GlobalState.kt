package generator.config

import generator.data.TypeMappingUnit

class GlobalState {
    var typeMappingGroupMap: MutableMap<String, Collection<TypeMappingUnit>> = HashMap()
    var databaseMappingGroupMap: MutableMap<String, Collection<TypeMappingUnit>> = HashMap()

    fun getTemplates(templateGroup: String?): Set<TypeMappingUnit> {
        return typeMappingGroupMap[templateGroup]?.toSet() ?: emptySet()
    }
}