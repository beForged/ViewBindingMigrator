import contract.Collator

class Listener(private val collator: Collator) : KotlinParserBaseListener() {

    override fun enterImportList(ctx: KotlinParser.ImportListContext?) {
        super.enterImportList(ctx)
        val importHeaders = ctx!!.importHeader()
        importHeaders.map {
            if (it.identifier().text.contains("synthetic")) {
                collator.extractSyntheticFromImport(it.identifier().text, it.sourceInterval)
            }
        }
    }

    override fun enterConstructorInvocation(ctx: KotlinParser.ConstructorInvocationContext?) {
        super.enterConstructorInvocation(ctx)
    }

    override fun enterFunctionBody(ctx: KotlinParser.FunctionBodyContext?) {
        super.enterFunctionBody(ctx)
    }

    override fun enterFunctionDeclaration(ctx: KotlinParser.FunctionDeclarationContext?) {
        super.enterFunctionDeclaration(ctx)
        if (ctx != null) {
            collator.extractFunctionDeclarations(ctx)
        }
    }

    override fun enterClassMemberDeclaration(ctx: KotlinParser.ClassMemberDeclarationContext?) {
        super.enterClassMemberDeclaration(ctx)
        if (ctx != null) {
            collator.classMemberDecl(ctx)
        }
    }
}
