package generator.interfaces.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import generator.config.GlobalState
import generator.data.TypeMappingUnit
import generator.util.StaticUtil


@State(
    name = "GlobalStateService",
    storages = [Storage(value = "TemplateGeneratorGlobalState.xml")]
)
class GlobalStateService : PersistentStateComponent<GlobalState?> {
    private var globalState = GlobalState()

    override fun getState(): GlobalState {
        if (globalState.typeMappingGroupMap.isEmpty()) {
            val objectMapper = StaticUtil.JSON

            val groupMapTemplate = globalState.typeMappingGroupMap

            val classLoader = GlobalStateService::class.java.classLoader
            classLoader.getResourceAsStream("TypeMapper/CSharpMapper.json").use { inputStream ->
                val typeReference =
                    object : TypeReference<MutableSet<TypeMappingUnit>>() {}
                val readValue = objectMapper.readValue(inputStream, typeReference)
                groupMapTemplate["DefaultCSharp"] = readValue
            }

            classLoader.getResourceAsStream("TypeMapper/db_pg_mapper.json").use { inputStream ->
                val typeReference =
                    object : TypeReference<MutableSet<TypeMappingUnit>>() {}
                val readValue = objectMapper.readValue(inputStream, typeReference)
                globalState.databaseMappingGroupMap["db_pg_mapper"] = readValue
            }
        }

        return globalState
    }

    override fun loadState(state: GlobalState) {
        this.globalState = state
    }

    companion object {
        @JvmStatic
        fun getInstance(): GlobalStateService {
            return ApplicationManager.getApplication().getService(GlobalStateService::class.java)
        }
    }
}
