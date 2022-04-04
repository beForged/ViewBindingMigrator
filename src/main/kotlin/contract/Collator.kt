package contract

import org.antlr.v4.runtime.misc.Interval

interface Collator {
    fun extractSyntheticFromImport(s: String, i: Interval)

    fun extractFunctionDeclarations(declarationContext: KotlinParser.FunctionDeclarationContext)
}
