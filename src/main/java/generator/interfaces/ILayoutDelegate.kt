package generator.interfaces

import com.intellij.ui.components.JBCheckBox
import java.awt.Component
import java.awt.LayoutManager
import javax.swing.AbstractButton

interface ILayoutDelegate {

    fun initContainer(
        items: Collection<String>,
        addComponent: (Component, Any?) -> Void?
    ): Collection<AbstractButton>

    fun getLayoutManager(): LayoutManager
}
