package generator.config

import generator.data.TypeMappingUnit

class GlobalState {
    var typeMappingGroupMap: MutableMap<String, Collection<TypeMappingUnit>> = HashMap()
}