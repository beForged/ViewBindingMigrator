import fragmentConverter.FragmentRewriter
import fragmentConverter.KtFragmentCollator
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.TokenStreamRewriter
import org.antlr.v4.runtime.tree.ParseTreeWalker
import java.io.File

class FileConverter {
    companion object {
        private const val FRAGMENT = "Fragment"
    }
    fun convert(file: File): Boolean {
        val fileName = file.absolutePath
        if (fileName.contains(FRAGMENT).not()) {
            println("unsupported nonfragment file: $fileName")
            return false
        }
        // fragment, activity, viewholder, adapter?,
        val collator = when {
            fileName.contains(FRAGMENT) -> KtFragmentCollator()
            else -> KtFragmentCollator()
        }

        val stream = org.antlr.v4.runtime.ANTLRFileStream(fileName)
        val lexer = KotlinLexer(stream)
        KotlinLexer(null).tokenTypeMap
        val tokens = CommonTokenStream(lexer)
        val parser = KotlinParser(tokens)
        val rewriter = TokenStreamRewriter(tokens)

        parser.buildParseTree = true

        val tree = parser.kotlinFile()

        ParseTreeWalker.DEFAULT.walk(Listener(collator), tree.ruleContext)

        val model = when {
            fileName.contains(FRAGMENT) -> collator.fragmentConverterModel()
            else -> collator.fragmentConverterModel()
        }
        if (model != null) {
            when {
                fileName.contains(FRAGMENT) -> {
                    val re = FragmentRewriter()
                    re.rewriteFragment(model, rewriter)
                }
            }
        }
        // rewrite file
        file.writeText(rewriter.text)
        println("converted $fileName")
        return true
    }
}
