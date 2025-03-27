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
        val classLoader = GlobalStateService::class.java.classLoader
        val objectMapper = StaticUtil.JSON

        if (globalState.typeMappingGroupMap.isEmpty()) {
            classLoader.getResourceAsStream("TypeMapper/$DEFAULT_TYPE_GROUP.json").use { inputStream ->
                val typeReference =
                    object : TypeReference<MutableSet<TypeMappingUnit>>() {}
                val readValue = objectMapper.readValue(inputStream, typeReference)
                globalState.typeMappingGroupMap[DEFAULT_TYPE_GROUP] = readValue
            }
        }
        return globalState
    }

    override fun loadState(state: GlobalState) {
        this.globalState = state
    }

    companion object {
        const val DEFAULT_TYPE_GROUP = "DefaultCSharp"

        @JvmStatic
        fun getInstance(): GlobalStateService {
            return ApplicationManager.getApplication().getService(GlobalStateService::class.java)
        }
    }
}
