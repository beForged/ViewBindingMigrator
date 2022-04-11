package activityConverter

import contract.Collator
import contract.ConverterModel
import org.antlr.v4.runtime.misc.Interval

class KtActivityCollator(
    override val syntheticViews: MutableList<ConverterModel.SyntheticImport> = mutableListOf(),
    override val viewReferences: MutableList<ConverterModel.ViewReference> = mutableListOf()
) : Collator {

    var onCreateExists = false
    var onCreateLocation: Interval? = null
    var bindingName: String? = null
    var findItem: Boolean = false

    override fun extractFunctionDeclarations(declarationContext: KotlinParser.FunctionDeclarationContext) {
        val functionName = declarationContext.simpleIdentifier().Identifier()
        when {
            functionName.text.equals("onCreate") -> {
                onCreateExists = true
                onCreateLocation = declarationContext.functionBody().sourceInterval
                bindingName = getContentView(declarationContext.functionBody())
            }
        }
        extractViewReferences(declarationContext.functionBody())
        if(declarationContext.functionBody().text.contains("findItem")) {
            findItem = true
        }
    }

    private fun extractViewReferences(declarationContext: KotlinParser.FunctionBodyContext) {
        val contains = syntheticViews.map { declarationContext.text.contains(it.view) }

        if(contains.contains(true)) {
            val body = functionBodyText(declarationContext)
            viewReferences.add(
                ConverterModel.ViewReference(
                    viewList = body.split("\n"),
                    interval = declarationContext.sourceInterval
                )
            )
        }
    }

    private fun getContentView( declarationContext: KotlinParser.FunctionBodyContext): String? {
        val body = functionBodyText(declarationContext)
        val t = body.split("\n").first { it.contains("setContentView") }
        return Regex("\\((.*?)\\)").find(t)?.groupValues?.toList()?.firstOrNull { it.isNotBlank() }
    }

    fun activityConverterModel(): ActivityConverterModel? {
        return if (syntheticViews.isEmpty() && bindingName != null) {
            ActivityConverterModel(
                bindingName = bindingName!!,
                onCreateExists = onCreateExists,
                onCreateLocation = onCreateLocation,
                findItem = findItem,
                syntheticImports = syntheticViews,
                viewReferences = viewReferences
            )
        } else {
            println("synthetic views may be empty or binding name may be null - skipping")
            null
        }
    }
}
