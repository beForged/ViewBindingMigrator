package activityConverter

import SyntheticMigratorUtils.Companion.snakeToLowerCamelCase
import SyntheticMigratorUtils.Companion.snakeToUpperCamelCase
import contract.ConverterModel
import org.antlr.v4.runtime.misc.Interval

class ActivityConverterModel(
    val bindingName: String?,
    val onCreateExists: Boolean,
    val findItem: Boolean,
    val onCreateBodyLocation: Interval?,
    val onCreateLocation: Interval?,
    val syntheticImports: List<ConverterModel.SyntheticImport>,
    val viewReferences: List<ConverterModel.ViewReference>,
    val layoutId: String?,
    val layoutIdLocation: Interval?,
    val variableDecl: Interval
) : ConverterModel {

    fun bindingClassName(): String {
        return "${bindingName?.snakeToUpperCamelCase()}Binding"
    }

    private fun varBindingName(s: ConverterModel.SyntheticImport): String {
        return s.filename.snakeToLowerCamelCase() + "Binding"
    }
    private fun classBindingName(s: ConverterModel.SyntheticImport): String {
        return s.filename.snakeToUpperCamelCase() + "Binding"
    }

    fun bindingClassVariables(): String {
        return syntheticImports.distinctBy { it.filename }.joinToString("\n") {
            "\t private lateinit var ${varBindingName(it)}: ${classBindingName(it)}\n"
        }
    }

    fun onCreateInternalInflate(): String {
        return syntheticImports.joinToString("\n") {
            "\t\t${varBindingName(it)} = ${classBindingName(it)}.inflate(layoutInflater)\n"
        }
    }

    fun onCreateInternalRootBind(): String {
        return "\t\tval view = ${bindingName?.snakeToLowerCamelCase()}Binding.root\n" +
            "\t\tsetContentView(view)"
    }

    fun onCreateExternal(): String {
        return "\toverride fun onCreate(savedInstanceState: Bundle?) {\n" +
            "\t\tsuper.onCreate(savedInstanceState)\n" +
            onCreateInternalInflate() +
            onCreateInternalRootBind() +
            "\t}\n"
    }

    fun replaceViewReference(lst: List<String>): String {
        val ret = lst.map {
            val synthetic = findViewName(it)
            if (synthetic != null) {
                it.replace(synthetic.view, "${synthetic.bindingVarName()}.${synthetic.view}")
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
