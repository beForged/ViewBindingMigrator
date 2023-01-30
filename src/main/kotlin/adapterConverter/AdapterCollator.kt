package adapterConverter

import contract.Collator
import contract.ConverterModel
import org.antlr.v4.runtime.misc.Interval

class AdapterCollator(
    override val syntheticViews: MutableList<ConverterModel.SyntheticImport> = mutableListOf(),
    override val viewReferences: MutableList<ConverterModel.ViewReference> = mutableListOf(),
    override var variableDeclInterval: Interval? = null
) : Collator {

    var onCreateText: String? = null
    var onCreateLocation: Interval? = null

    override fun extractFunctionDeclarations(declarationContext: KotlinParser.FunctionDeclarationContext) {
        val functionName = declarationContext.simpleIdentifier().Identifier()

        if (functionName.text.equals("onCreateViewHolder")) {
            val funBody = declarationContext.functionBody()
            onCreateText = declarationContext.functionBody().start.inputStream.getText(
                Interval(funBody.start.startIndex, funBody.stop.stopIndex)
            )
            onCreateLocation = declarationContext.functionBody().sourceInterval
        }
    }

    override fun converterModel(): ConverterModel {
        return AdapterConverterModel(
            onCreateText = onCreateText,
            onCreateLocation = onCreateLocation
        )
    }
}
