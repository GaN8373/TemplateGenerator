package generator.interfaces

interface IHistorySelectedDelegate<T> {
    fun getSelectedList(): Collection<T>

    fun getSelectItem(): T?

    fun selectItem(item: T)
}
