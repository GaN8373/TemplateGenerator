package generator.data

class ComboBoxSelect() {
    constructor(string: String, bool: Boolean) : this(){
        value = string
        isSelect = bool
    }

    var value: String? = null
    var isSelect: Boolean = false
}