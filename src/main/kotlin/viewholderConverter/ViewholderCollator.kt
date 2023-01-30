package viewholderConverter

import contract.Collator
import contract.ConverterModel
import org.antlr.v4.runtime.misc.Interval

class ViewholderCollator(
    override val syntheticViews: MutableList<ConverterModel.SyntheticImport> = mutableListOf(),
    override val viewReferences: MutableList<ConverterModel.ViewReference> = mutableListOf(),
    override var variableDeclInterval: Interval? = null
) : Collator {

    var layoutBindingName: String? = null
    override fun extractFunctionDeclarations(declarationContext: KotlinParser.FunctionDeclarationContext) {
        val funBody = declarationContext.functionBody()
        val contains = syntheticViews.map {
            if (funBody?.text != null) {
                funBody.text.contains(it.view)
            } else {
                false
            }
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

    override fun extractSyntheticFromImport(s: String, i: Interval) {
        val view = s.split(".").reversed()[0]
        val layout = s.split(".").reversed()[2]
        syntheticViews.add(ConverterModel.SyntheticImport(layout, view, i))
    }

    override fun postSyntheticImport(declaractionContext: KotlinParser.ImportListContext) {
        super.postSyntheticImport(declaractionContext)
        if (syntheticViews.map { it.filename }.distinct().size > 1) {
            println("multiple synthetic imports! please manually verify")
        }
        layoutBindingName = syntheticViews.map { it.filename }.sortedDescending().distinct()[0]
        println(layoutBindingName)
    }

    var viewParamLocation: Interval? = null

    override fun extractParam(declaractionContext: KotlinParser.ClassParameterContext) {
        println("params: ${declaractionContext.type().text}")
        val type = declaractionContext.type().text
        if (type == "View") {
            viewParamLocation = declaractionContext.sourceInterval
        }
    }

    override fun converterModel(): ConverterModel? {
        return ViewholderConverterModel(
            bindingName = layoutBindingName,
            syntheticImports = syntheticViews.toList().distinct(),
            viewReferences = viewReferences.toList().distinct(),
            viewParamLocation = viewParamLocation
        )
    }

    override fun extractInitializer(declaractionContext: KotlinParser.AnonymousInitializerContext) {
        val text = declaractionContext.text
        val contains = syntheticViews.map {
            text?.contains(it.view) ?: false
        }
        if (contains.contains(true)) {
            viewReferences.add(
                ConverterModel.ViewReference(
                    viewList = text.split("\n"),
                    interval = declaractionContext.sourceInterval
                )
            )
        }
        println(viewReferences.toString())
    }
}
