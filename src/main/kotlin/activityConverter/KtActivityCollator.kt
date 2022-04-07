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

    override fun extractFunctionDeclarations(declarationContext: KotlinParser.FunctionDeclarationContext) {
        val functionName = declarationContext.simpleIdentifier().Identifier()
    }

    fun activityConverterModel(): ActivityConverterModel? {
        return if (syntheticViews.isEmpty()) {
            ActivityConverterModel()
        } else {
            println()
            null
        }
    }
}
