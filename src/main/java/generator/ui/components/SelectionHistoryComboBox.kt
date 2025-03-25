package generator.ui.components

import com.intellij.database.util.common.isNotNullOrEmpty
import generator.interfaces.IHistorySelectedDelegate
import javax.swing.JComboBox

class SelectionHistoryComboBox(private val delegate: IHistorySelectedDelegate<String>) : JComboBox<String>() {
    init {
        delegate.getSelectedList().forEach { addItem(it) }
        initLastSelectedItem()
        this.addItemListener {
            val toString = it.item.toString()
            if (toString.isEmpty()) {
                return@addItemListener
            }

            delegate.selectItem(toString)
        }
    }

    private fun initLastSelectedItem() {
        val lastSelectedItem = delegate.getSelectItem()
        if (lastSelectedItem.isNotNullOrEmpty) {
            for (i in 0 until itemCount) {
                if (getItemAt(i) == lastSelectedItem) {
                    this.selectedItem = lastSelectedItem
                    break
                }
            }
        }
    }

}