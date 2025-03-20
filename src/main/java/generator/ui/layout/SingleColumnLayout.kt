package generator.ui.layout

import com.intellij.openapi.ui.VerticalFlowLayout
import com.intellij.ui.components.JBCheckBox
import generator.interfaces.ILayoutDelegate
import java.awt.Component
import java.awt.LayoutManager
import javax.swing.AbstractButton


class SingleColumnLayout : ILayoutDelegate {
    override fun getLayoutManager(): LayoutManager {
        return VerticalFlowLayout()
    }

    override fun initContainer(
        items: Collection<String>,
        addComponent: (Component, Any?) -> Void?
    ): Collection<AbstractButton> {
        val checkBoxList = ArrayList<JBCheckBox>(items.size)

        for (item in items) {
            val checkBox = JBCheckBox(item)
            checkBoxList.add(checkBox)
            addComponent.invoke(checkBox, null)
        }

        return checkBoxList
    }
}
