import SyntheticMigratorUtils.Companion.snakeToLowerCamelCase
import SyntheticMigratorUtils.Companion.snakeToUpperCamelCase
import contract.ConverterModel
import org.antlr.v4.runtime.misc.Interval

data class FragmentConverterModel(
    val bindingName: String,
    val layoutIdFunction: Interval,
    val onCreateExists: Boolean,
    val onCreateLocation: Interval?,
    val onDestroyExists: Boolean,
    val onDestroyLocation: Interval?,
    val syntheticImports: List<SyntheticImport>
    ): ConverterModel {

    fun bindingClassName(): String {
        return "${bindingName.snakeToUpperCamelCase()}Binding"
    }

    fun lateinitBinding(): String {
        return "private var _binding: ${bindingClassName()}? = null\n" +
               "private val binding get() = _binding"
    }

    fun bindingVariables(): String {
        val variableList = syntheticImports.map { it.filename }.distinct().map {
            "\tprivate var ${it.snakeToLowerCamelCase()}Binding: ${it.snakeToUpperCamelCase()}Binding? = null\n"
        }
        return variableList.joinToString("")
    }

    fun onCreateViewOutput(): String {
        val onCreateLocalBindings = syntheticImports.map { it.filename }.distinct().map {
            onCreateViewInternal(it, it)
        }
       return onCreateLocalBindings.joinToString("\n")
    }

    private fun onCreateViewInternal(distinct: String, layoutBinding: String): String {
        val localVariableName = distinct.snakeToUpperCamelCase()
        val bindingClassName = "${layoutBinding.snakeToUpperCamelCase()}Binding"
        val bindingVarName = "${layoutBinding.snakeToLowerCamelCase()}Binding"
        return "\n\t\tval binding$localVariableName= $bindingClassName.bind(view)\n" +
                "\t\t${bindingVarName} = binding$localVariableName"
    }

    fun onCreateViewOutputExternal(): String {
        return "\n\n\toverride fun onViewCreated(view: View, savedInstanceState: Bundle?) {\n" +
                "\t\tsuper.onViewCreated(view, savedInstanceState)\n" +
                onCreateViewOutput() +
                "\t\t\\TODO add presenter oncreated callback\n" +
                "\t}\n\n"
    }


    fun onDestroyViewInternal(): String {
        val destroyList = syntheticImports.map { it.filename }.distinct().map {
            val bindingVarName = "${it.snakeToLowerCamelCase()}Binding"
            "\t\t$bindingVarName = null\n"
        }
        return destroyList.joinToString("")
    }

    fun onDestroyViewExternal(): String {
        return "\n\n\toverride fun onDestroyView() {\n" +
                onDestroyViewInternal() +
                "\t\tsuper.onDestroyView()\n" +
                "\t}\n"
    }
}

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