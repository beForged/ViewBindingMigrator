package fragmentConverter

import SyntheticMigratorUtils.Companion.snakeToLowerCamelCase
import SyntheticMigratorUtils.Companion.snakeToUpperCamelCase
import contract.ConverterModel
import org.antlr.v4.runtime.misc.Interval

data class FragmentConverterModel(
    val bindingName: String?,
    val layoutIdFunction: Interval?,
    val onCreateExists: Boolean,
    val onCreateLocation: Interval?,
    val onCreatedViewExists: Boolean,
    val onCreatedViewLocation: Interval?,
    val onDestroyExists: Boolean,
    val onDestroyLocation: Interval?,
    val syntheticImports: List<ConverterModel.SyntheticImport>,
    val viewReference: List<ConverterModel.ViewReference>,
    val variableDeclInterval: Interval
) : ConverterModel {

    fun bindingClassName(): String {
        return "${bindingName?.snakeToUpperCamelCase()}Binding"
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
            "\t\t$bindingVarName = binding$localVariableName"
    }

    fun onCreateViewOutputExternal(): String {
        return "\n\n\toverride fun onViewCreated(view: View, savedInstanceState: Bundle?) {\n" +
            "\t\tsuper.onViewCreated(view, savedInstanceState)\n" +
            onCreateViewOutput() +
            "\t\t\\TODO add presenter oncreated callback\n" +
            "\t}\n\n"
    }

    fun onCreatedViewLayout(): String {
        val innerBind = "\t\t_binding = ${bindingName?.snakeToUpperCamelCase()}Binding.inflate(inflater, container, false)\n" +
            "\t\tval view = binding.root\n" +
            "\t\treturn view\n"
        return if (onCreatedViewExists) {
            innerBind
        } else {
            "\toverride fun onCreateView(\n" +
                "\t\tinflater: LayoutInflater,\n" +
                "\t\tcontainer: ViewGroup?,\n" +
                "\t\tsavedInstanceState: Bundle?\n" +
                "\t): View? {\n" +
                innerBind +
                "\t}\n"
        }
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
