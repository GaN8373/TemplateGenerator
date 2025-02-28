package generator.interfaces

import com.intellij.ui.components.JBCheckBox
import java.awt.Component
import java.awt.LayoutManager

interface ILayoutDelegate {

    fun initContainer(
        items: Collection<String>,
        addComponent: (Component, Any?) -> Void?
    ): Collection<JBCheckBox>

    fun getLayoutManager(): LayoutManager
}
