package contract

import SyntheticMigratorUtils.Companion.snakeToLowerCamelCase
import SyntheticMigratorUtils.Companion.snakeToUpperCamelCase
import org.antlr.v4.runtime.misc.Interval

interface ConverterModel {

    data class SyntheticImport(
        val filename: String,
        val view: String,
        val interval: Interval
    ) {
        fun bindingClassName(): String {
            return "${filename.snakeToUpperCamelCase()}Binding"
        }
        fun bindingVarName(): String {
            return "${filename.snakeToLowerCamelCase()}Binding"
        }

        fun localBindingName(): String {
            return "binding${filename.snakeToUpperCamelCase()}"
        }
    }

    data class ViewReference(
        val viewList: List<String>,
        val interval: Interval
    )
}
