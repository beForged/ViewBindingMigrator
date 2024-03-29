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

    override fun exitImportList(ctx: KotlinParser.ImportListContext?) {
        super.exitImportList(ctx)
        if (ctx != null) {
            collator.postSyntheticImport(ctx)
        }
    }

    override fun enterConstructorInvocation(ctx: KotlinParser.ConstructorInvocationContext?) {
        super.enterConstructorInvocation(ctx)
    }

    override fun enterClassParameter(ctx: KotlinParser.ClassParameterContext?) {
        super.enterClassParameter(ctx)
        if (ctx != null) {
            collator.extractParam(ctx)
        }
    }

    override fun enterAnonymousInitializer(ctx: KotlinParser.AnonymousInitializerContext?) {
        super.enterAnonymousInitializer(ctx)
        if (ctx != null) {
            collator.extractInitializer(ctx)
        }
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
