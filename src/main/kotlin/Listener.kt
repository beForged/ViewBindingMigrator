class Listener: KotlinParserBaseListener() {

    override fun enterImportList(ctx: KotlinParser.ImportListContext?) {
        super.enterImportList(ctx)
        val name = ctx?.text
        name?.split("\n")?.filter { it.contains("synthetic") }?.map { println(it) }
    }

    override fun enterConstructorInvocation(ctx: KotlinParser.ConstructorInvocationContext?) {
        super.enterConstructorInvocation(ctx)
    }

    override fun enterFunctionBody(ctx: KotlinParser.FunctionBodyContext?) {
        super.enterFunctionBody(ctx)
    }

    override fun enterFunctionDeclaration(ctx: KotlinParser.FunctionDeclarationContext?) {
        super.enterFunctionDeclaration(ctx)
        if(ctx?.text?.contains("view") == true) { println(ctx.text) }
    }

}