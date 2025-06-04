package generator.data

import java.util.concurrent.ConcurrentHashMap

class TemplateContextWrapper {
    val context: ConcurrentHashMap<String, Any> = ConcurrentHashMap()
    fun get(key: String): Any? {
        return context[key]
    }

    fun set(key: String, value: Any) {
        context[key] = value
    }

    fun remove(key: String) {
        context.remove(key)
    }

    fun clear() {
        context.clear()
    }

    fun contains(key: String): Boolean {
        return context.contains(key)
    }

    fun containsKey(key: String): Boolean {
        return context.containsKey(key)
    }

    fun containsValue(value: Any): Boolean {
        return context.containsValue(value)
    }

    fun size(): Int {
        return context.size
    }

    fun keys(): Set<String> {
        return context.keys
    }

    fun values(): Collection<Any> {
        return context.values
    }

    fun entries(): Set<Map.Entry<String, Any>> {
        return context.entries
    }

    fun put(key: String, value: Any) {
        context[key] = value
    }

    fun isEmpty(): Boolean {
        return context.isEmpty()
    }
}