package generator.data

import generator.MapperAction
import javax.swing.table.DefaultTableModel

class TableRowData(@JvmField var data: List<TypeMappingUnit>) : DefaultTableModel(COLUMN_NAMES, 0) {
    init {
        for (it in data) {
            addRow(arrayOf<Any>(it.action.name, it.rule, it.type))
        }
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        val typeMapper = data[rowIndex]

        return when (columnIndex) {
            0 -> typeMapper.action.name
            1 -> typeMapper.rule
            2 -> typeMapper.type
            else -> ""
        }
    }

    override fun setValueAt(aValue: Any, rowIndex: Int, columnIndex: Int) {
        super.setValueAt(aValue, rowIndex, columnIndex)
        val typeMapper = data[rowIndex]
        when (columnIndex) {
            0 -> typeMapper.action = MapperAction.valueOf(aValue.toString())
            1 -> typeMapper.rule = aValue.toString()
            2 -> typeMapper.type = aValue.toString()
        }
    }

    companion object {
        private val COLUMN_NAMES = arrayOf("Action", "Rule", "Write")
    }
}