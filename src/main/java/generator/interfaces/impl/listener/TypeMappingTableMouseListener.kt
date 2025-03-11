package generator.interfaces.impl.listener

import generator.config.GlobalState
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.JTable

class TypeMappingTableMouseListener(
    globalState: GlobalState,
    private val typeMappingTable: JTable
) : MouseListener {
    override fun mouseClicked(e: MouseEvent) {
        if (e.clickCount == 2) {
            val selectedRow = typeMappingTable.selectedRow
            if (selectedRow != -1) {
            }
        }
    }

    override fun mousePressed(e: MouseEvent) {
    }

    override fun mouseReleased(e: MouseEvent) {
    }

    override fun mouseEntered(e: MouseEvent) {
    }

    override fun mouseExited(e: MouseEvent) {
    }
}
