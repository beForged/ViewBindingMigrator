package fragmentConverter

import FragmentConverterModel
import KotlinParser
import SyntheticImport
import contract.Collator
import org.antlr.v4.runtime.misc.Interval

class KtFragmentCollator(filename: String): Collator {

    private val output: String = ""
    val syntheticViews: MutableList<SyntheticImport> = mutableListOf()

    fun appendOutput(s: String) {
        output.plus(s)
    }

    override fun extractSyntheticFromImport(s: String, i: Interval) {
        val view = s.split(".").reversed()[0]
        val layout = s.split(".").reversed()[1]
        syntheticViews.add(SyntheticImport(layout, view, i))
    }


    var layoutBindingName: String? = null
    var bindingLocation: Interval? = null
    var onCreateExists = false
    var onCreateLocation: Interval? = null
    var onDestroyExists = false
    var onDestroyLocation: Interval? = null

    override fun extractFunctionDeclarations(
        declarationContext: KotlinParser.FunctionDeclarationContext
    ) {
        val functionName = declarationContext.simpleIdentifier().Identifier()
        //println(functionName)
        when {
            functionName.text.equals("onViewCreated") -> {
                onCreateExists = true
                onCreateLocation = declarationContext.functionBody().sourceInterval
            }
            functionName.text.equals("onDestroyView") -> {
                onDestroyExists = true
                onDestroyLocation = declarationContext.functionBody().sourceInterval
            }
            functionName.text.equals("getLayoutId") -> {
                layoutBindingName = declarationContext.functionBody().text.split(".").last()
                bindingLocation = declarationContext.sourceInterval
            }
            else -> {
                val contains = syntheticViews.map { declarationContext.functionBody().text.contains(it.view) }

            }
        }
    }

    fun fragmentConverterModel(): FragmentConverterModel? {
        return if( layoutBindingName != null
        ) {
            FragmentConverterModel(
                bindingName = layoutBindingName!!,
                layoutIdFunction = bindingLocation!!,
                onCreateExists = onCreateExists,
                onCreateLocation = onCreateLocation,
                onDestroyExists = onDestroyExists,
                onDestroyLocation = onDestroyLocation,
                syntheticImports = syntheticViews.toList()
            )
        } else {
            println("missing layoutId")
            null
        }
    }

}