package viewholderConverter

import SyntheticMigratorUtils.Companion.snakeToUpperCamelCase
import contract.ConverterModel
import org.antlr.v4.runtime.misc.Interval

class ViewholderConverterModel(
    val bindingName: String?,
    val syntheticImports: List<ConverterModel.SyntheticImport>,
    val viewReferences: List<ConverterModel.ViewReference>,
    val viewParamLocation: Interval?
) : ConverterModel {

    fun bindingClassName(): String {
        return "${bindingName?.snakeToUpperCamelCase()}Binding"
    }

    fun replaceItemView(): String {
        return "\tval itemBinding: ${bindingClassName()}"
    }

    fun replaceViewReference(lst: List<String>): String {
        val ret = lst.map {
            val synthetic = findViewName(it)
            if (synthetic != null) {
                it.replace("itemView.${synthetic.view}", "itemBinding.${synthetic.view}")
            } else {
                it
            }
        }
        return ret.joinToString("\n")
    }

    // assumes that each line is contained such that there is only one view reference
    private fun findViewName(s: String): ConverterModel.SyntheticImport? {
        return syntheticImports.firstOrNull { s.contains(it.view) }
    }
}
