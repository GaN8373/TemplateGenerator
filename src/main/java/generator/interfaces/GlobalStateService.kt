package generator.interfaces

import com.intellij.openapi.components.*
import generator.config.GlobalState

@Service(Service.Level.APP)
@State(
    name = "GlobalStateService",
    storages = [Storage(value = "Template-Generator-GlobalState.xml", roamingType = RoamingType.PER_OS)]
)
class GlobalStateService : PersistentStateComponent<GlobalState?> {
    private var globalState = GlobalState()

    override fun getState(): GlobalState {
        return globalState
    }

    override fun loadState(state: GlobalState) {
        state.historyUsePath = state.historyUsePath.takeLast(10)

        this.globalState = state
    }
}
