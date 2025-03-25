package generator.interfaces.impl

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import generator.config.GlobalHistoryState
import generator.data.ScoredMember


@State(
    name = "HistoryStateService",
    storages = [Storage(value = "TemplateGeneratorGlobalHistoryState.xml")]
)
class HistoryStateService : PersistentStateComponent<GlobalHistoryState?> {
    private var state = GlobalHistoryState()

    override fun getState(): GlobalHistoryState {
        return state
    }

    override fun loadState(state: GlobalHistoryState) {
        if (state.historyUsePath.size > 10) {
            val v = HashSet<ScoredMember<String>>()
            state.historyUsePath.stream().limit(10).forEach { v.add(it) }
            state.historyUsePath = v
        }
        this.state = state
    }

    companion object {
        @JvmStatic
        fun getInstance(): HistoryStateService {
            return ApplicationManager.getApplication().getService(HistoryStateService::class.java)
        }
    }
}
