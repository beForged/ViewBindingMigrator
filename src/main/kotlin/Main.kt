import fragmentConverter.FragmentRewriter
import fragmentConverter.KtFragmentCollator
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.TokenStreamRewriter
import org.antlr.v4.runtime.tree.ParseTreeWalker

fun main(args: Array<String>) {
    println("Program arguments: ${args.joinToString()}")

    val file = args[0]

    //fragment, activity, viewholder, adapter?,
    val collator = when {
        file.contains("Fragment", true) -> KtFragmentCollator(file)
        else -> KtFragmentCollator(file)
    }

    val stream = org.antlr.v4.runtime.ANTLRFileStream(file)
    val lexer = KotlinLexer(stream)
    //println(lexer.allTokens)
    KotlinLexer(null).tokenTypeMap
    val tokens = CommonTokenStream(lexer)
    val parser = KotlinParser(tokens)
    val rewriter = TokenStreamRewriter(tokens)

    parser.buildParseTree = true

    val tree = parser.kotlinFile()
    //tree.children.map { println(it.text) }


    ParseTreeWalker.DEFAULT.walk(Listener(collator), tree.ruleContext)

//    println(collator.syntheticViews)
//    println(collator.onCreateExists)
//    println(collator.onDestroyExists)

    val model = collator.fragmentConverterModel()
    if(model != null) {
        val re = FragmentRewriter()
        re.rewriteFragment(model, rewriter)
    }
    println(rewriter.text)
}


