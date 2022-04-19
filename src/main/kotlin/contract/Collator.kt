package contract

import org.antlr.v4.runtime.misc.Interval

interface Collator {
    val syntheticViews: MutableList<ConverterModel.SyntheticImport>
    val viewReferences: MutableList<ConverterModel.ViewReference>

    fun extractSyntheticFromImport(s: String, i: Interval) {
        val view = s.split(".").reversed()[0]
        val layout = s.split(".").reversed()[1]
        syntheticViews.add(ConverterModel.SyntheticImport(layout, view, i))
    }

    fun functionBodyText(declarationContext: KotlinParser.FunctionBodyContext): String {
        return declarationContext.start.inputStream.getText(
            Interval(declarationContext.start.startIndex, declarationContext.stop.stopIndex)
        )
    }

    fun extractFunctionDeclarations(declarationContext: KotlinParser.FunctionDeclarationContext)

    fun classMemberDecl(ctx: KotlinParser.ClassMemberDeclarationContext)

    fun converterModel(): ConverterModel?
}
