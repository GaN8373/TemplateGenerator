package generator.interfaces

interface IHistorySelectedDelegate<T> {
    fun getSelectList(): Collection<T>

    fun getSelectItem(): T?

    fun selectItem(item: T)
}
