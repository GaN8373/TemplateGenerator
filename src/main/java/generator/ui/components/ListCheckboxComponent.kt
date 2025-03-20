package generator.ui.components

import com.intellij.ui.components.JBCheckBox
import generator.interfaces.ILayoutDelegate
import java.awt.Component
import javax.swing.AbstractButton
import javax.swing.JPanel
import javax.swing.JScrollPane

/**
 * 列表复选框组件
 *
 * @author makejava
 * @version 1.0.0
 * @since 2018/09/03 17:12
 */
class ListCheckboxComponent(
    layoutDelegate: ILayoutDelegate,
    /**
     * 原属列表
     */
    private val items: Collection<String>
) :
    JPanel(layoutDelegate.getLayoutManager()) {
    /**
     * 复选框列表
     */
    var checkBoxList: Collection<AbstractButton>? = null
        private set


    init {
        // 使用垂直流式布局
        this.initCheckBox(layoutDelegate)
    }

    /**
     * 初始化操作
     */
    private fun initCheckBox(layoutDelegate: ILayoutDelegate) {
        if (items.isEmpty()) {
            return
        }
        checkBoxList = layoutDelegate.initContainer(items) { component: Component, item: Any? ->
            add(component, item)
            null
        }
    }

    val selectedItems: List<String>
        /**
         * 获取已选中的元素
         *
         * @return 已选中的元素
         */
        get() {
            if (checkBoxList == null || checkBoxList!!.isEmpty()) {
                return emptyList()
            }
            val result: MutableList<String> = ArrayList()
            checkBoxList!!.forEach { checkBox ->
                if (checkBox.isSelected) {
                    result.add(checkBox.text)
                }
            }
            return result
        }
}
