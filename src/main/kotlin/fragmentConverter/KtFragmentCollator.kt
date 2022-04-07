package fragmentConverter

import KotlinParser
import contract.Collator
import contract.ConverterModel
import org.antlr.v4.runtime.misc.Interval

class KtFragmentCollator(
    override val syntheticViews: MutableList<ConverterModel.SyntheticImport> = mutableListOf(),
    override val viewReferences: MutableList<ConverterModel.ViewReference> = mutableListOf()
) : Collator {

    var layoutBindingName: String? = null
    var bindingLocation: Interval? = null
    var onCreateExists = false
    var onCreateLocation: Interval? = null
    var onDestroyExists = false
    var onDestroyLocation: Interval? = null
    var onCreatedViewExists = false
    var onCreatedViewLocation: Interval? = null

    override fun extractFunctionDeclarations(
        declarationContext: KotlinParser.FunctionDeclarationContext
    ) {
        val functionName = declarationContext.simpleIdentifier().Identifier()
        // println(functionName)
        when {
            functionName.text.equals("onViewCreated") -> {
                onCreateExists = true
                onCreateLocation = declarationContext.functionBody().sourceInterval
            }
            functionName.text.equals("onCreatedView") -> {
                onCreatedViewExists = true
                onCreatedViewLocation = declarationContext.functionBody().sourceInterval
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
                val funBody = declarationContext.functionBody()
                val contains = syntheticViews.map {
                    funBody.text.contains(it.view)
                }
                if (contains.contains(true)) {
                    // extracts function body with whitespace
                    val text = declarationContext.functionBody().start.inputStream.getText(
                        Interval(funBody.start.startIndex, funBody.stop.stopIndex)
                    )
                    viewReferences.add(
                        ConverterModel.ViewReference(
                            viewList = text.split("\n"),
                            interval = declarationContext.functionBody().sourceInterval
                        )
                    )
                }
            }
        }
    }

    fun fragmentConverterModel(): FragmentConverterModel? {
        return if (layoutBindingName != null || syntheticViews.isEmpty()
        ) {
            FragmentConverterModel(
                bindingName = layoutBindingName!!,
                layoutIdFunction = bindingLocation!!,
                onCreateExists = onCreateExists,
                onCreateLocation = onCreateLocation,
                onCreatedViewExists = onCreatedViewExists,
                onCreatedViewLocation = onCreatedViewLocation,
                onDestroyExists = onDestroyExists,
                onDestroyLocation = onDestroyLocation,
                syntheticImports = syntheticViews.toList(),
                viewReference = viewReferences.toList()
            )
        } else {
            println("missing layoutId or synthetic imports")
            null
        }
    }
}
