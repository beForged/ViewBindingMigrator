package activityConverter

import contract.Collator
import org.antlr.v4.runtime.misc.Interval

class KtActivityCollator : Collator {
    override fun extractSyntheticFromImport(s: String, i: Interval) {
        TODO("Not yet implemented")
    }

    override fun extractFunctionDeclarations(declarationContext: KotlinParser.FunctionDeclarationContext) {
        TODO("Not yet implemented")
    }
}
