package generator.config

import generator.data.ScoredMember

class GlobalHistoryState {
    var historyUseTypeMapper: String? = null
    var historyUsePath: MutableSet<ScoredMember> = HashSet()
}