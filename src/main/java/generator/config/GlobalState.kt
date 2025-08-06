package generator.config


import generator.data.ComboBoxSelect
import generator.data.TypeMappingUnit

class GlobalState {
    var typeMappingGroupMap: MutableMap<String, Collection<TypeMappingUnit>> = HashMap()

    var namespaceSplitChar: MutableList<ComboBoxSelect> = ArrayList<ComboBoxSelect>().apply {
        add(ComboBoxSelect(".", true))
        add(ComboBoxSelect(":", false))
        add(ComboBoxSelect("::", false))
        add(ComboBoxSelect("\\", false))
        add(ComboBoxSelect("_", false))
        add(ComboBoxSelect("-", false))
        add(ComboBoxSelect("/", false))
    }
}