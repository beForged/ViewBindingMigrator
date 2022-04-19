package activityConverter

import contract.Collator
import contract.ConverterModel
import org.antlr.v4.runtime.misc.Interval

class KtActivityCollator(
    override val syntheticViews: MutableList<ConverterModel.SyntheticImport> = mutableListOf(),
    override val viewReferences: MutableList<ConverterModel.ViewReference> = mutableListOf()
) : Collator {

    var onCreateExists = false
    var onCreateBodyLocation: Interval? = null
    var onCreateLocation: Interval? = null
    var bindingName: String? = null
    var findItem: Boolean = false
    var layoutBindingName: String? = null
    var bindingLocation: Interval? = null

    override fun extractFunctionDeclarations(declarationContext: KotlinParser.FunctionDeclarationContext) {
        if (declarationContext.functionBody() == null) {
            return
        }
        val functionName = declarationContext.simpleIdentifier().Identifier()
        when {
            functionName.text.equals("onCreate") -> {
                onCreateExists = true
                onCreateBodyLocation = declarationContext.functionBody().sourceInterval
                onCreateLocation = declarationContext.sourceInterval
                bindingName = getContentView(declarationContext.functionBody())
            }
            functionName.text.equals("getLayoutId") -> {
                layoutBindingName = declarationContext.functionBody().text.split(".").last()
                bindingLocation = declarationContext.sourceInterval
            }
        }
        extractViewReferences(declarationContext.functionBody())
        if (declarationContext.functionBody().text.contains("findItem")) {
            findItem = true
        }
    }

    var variableDeclInterval: Interval? = null
    override fun classMemberDecl(ctx: KotlinParser.ClassMemberDeclarationContext) {
        variableDeclInterval = ctx.sourceInterval
    }

    private fun extractViewReferences(declarationContext: KotlinParser.FunctionBodyContext) {
        val contains = syntheticViews.map { declarationContext.text.contains(it.view) }

        if (contains.contains(true)) {
            val body = functionBodyText(declarationContext)
            viewReferences.add(
                ConverterModel.ViewReference(
                    viewList = body.split("\n"),
                    interval = declarationContext.sourceInterval
                )
            )
        }
    }

    private fun getContentView(declarationContext: KotlinParser.FunctionBodyContext): String? {
        val body = functionBodyText(declarationContext)
        val t = body.split("\n").firstOrNull { it.contains("setContentView") }
        return if (t != null) {
            Regex("\\((.*?)\\)").find(t)?.groupValues?.toList()?.firstOrNull { it.isNotBlank() }
        } else {
            null
        }
    }

    override fun converterModel(): ConverterModel? {
        return if (
            (syntheticViews.isNotEmpty() && layoutBindingName != null) && variableDeclInterval != null
        ) {
            ActivityConverterModel(
                bindingName = bindingName,
                onCreateExists = onCreateExists,
                findItem = findItem,
                onCreateBodyLocation = onCreateBodyLocation,
                onCreateLocation = onCreateLocation,
                syntheticImports = syntheticViews.toList().distinct(),
                viewReferences = viewReferences.toList().distinct(),
                layoutId = layoutBindingName,
                layoutIdLocation = bindingLocation,
                variableDecl = variableDeclInterval!!
            )
        } else {
            println("synthetic views may be empty or binding name may be null - skipping")
            null
        }
    }
}
