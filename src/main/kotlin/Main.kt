import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.ParseTreeListener
import org.antlr.v4.runtime.tree.ParseTreeWalker

fun main(args: Array<String>) {
    println("Program arguments: ${args.joinToString()}")

    val file = args[0]


    val stream = org.antlr.v4.runtime.ANTLRFileStream(file)
    val lexer = KotlinLexer(stream)
    //println(lexer.allTokens)
    KotlinLexer(null).tokenTypeMap
    val tokens = CommonTokenStream(lexer)
    val parser = KotlinParser(tokens)

    parser.buildParseTree = true

    val tree = parser.kotlinFile()
    println( tree.children)


    ParseTreeWalker.DEFAULT.walk(Listener(), tree.ruleContext)



    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
}


