package generator.config

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import generator.data.TypeMapper
import generator.interfaces.GlobalStateService

class GlobalState {
    val groupMapTemplate: MutableMap<String, Collection<TypeMapper>> = HashMap()
    var historyUsePath: List<String> = ArrayList()
  init {
        val objectMapper = ObjectMapper()

        val groupMapTemplate = groupMapTemplate
        if (groupMapTemplate.isEmpty()) {
            val classLoader = GlobalStateService::class.java.classLoader
            classLoader.getResourceAsStream("TypeMapper/CSharpMapper.json").use { inputStream ->
                val typeReference =
                    object : TypeReference<MutableSet<TypeMapper>>() {}
                val readValue = objectMapper.readValue(inputStream, typeReference)

                groupMapTemplate["DefaultCSharp"] = readValue
            }
        }
    }

    fun getTemplates(templateGroup: String?): Set<TypeMapper> {
        return groupMapTemplate[templateGroup]?.toSet() ?: emptySet()
    }
}