package activityConverter

import contract.Collator
import contract.ConverterModel
import org.antlr.v4.runtime.misc.Interval

class KtActivityCollator(
    override val syntheticViews: MutableList<ConverterModel.SyntheticImport> = mutableListOf(),
    override val viewReferences: MutableList<ConverterModel.ViewReference> = mutableListOf()
) : Collator {

    override fun extractFunctionDeclarations(declarationContext: KotlinParser.FunctionDeclarationContext) {
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
