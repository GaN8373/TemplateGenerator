package generator.ui.layout

import com.intellij.ui.components.JBCheckBox
import generator.interfaces.ILayoutDelegate
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.LayoutManager
import javax.swing.AbstractButton

class DoubleColumnLayout : ILayoutDelegate {
    override fun getLayoutManager(): LayoutManager {
        return GridBagLayout()
    }

    override fun initContainer(
        items: Collection<String>,
        addComponent: (Component, Any?) -> Void?
    ): Collection<AbstractButton> {
        val checkBoxList = ArrayList<JBCheckBox>(items.size)

        val gbc = GridBagConstraints()
        gbc.fill = GridBagConstraints.HORIZONTAL // 水平填充可用空间
        gbc.weightx = 0.5 // 水平空间平均分配

        var row = 0
        var col = 0
        for (item in items) {
            val checkBox = JBCheckBox(item)
            checkBoxList.add(checkBox)
            gbc.gridx = col // 组件的列索引
            gbc.gridy = row // 组件的行索引
            addComponent.invoke(checkBox, gbc)

            col++
            if (col > 1) { // 超过两列后换行
                col = 0
                row++
            }
        }

        return checkBoxList
    }
}
