import fragmentConverter.FragmentRewriter
import fragmentConverter.KtFragmentCollator
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.TokenStreamRewriter
import org.antlr.v4.runtime.tree.ParseTreeWalker
import java.io.File

class FileConverter {
    companion object {
        private const val FRAGMENT = "Fragment"
        private const val ACTIVITY = "Activity"
        private const val VIEWHOLDER = "ViewHolder"
    }
    fun convert(file: File): Boolean {
        val fileName = file.absolutePath

        // fragment, activity, viewholder, adapter?,
        val collator = when {
            fileName.contains(FRAGMENT) -> KtFragmentCollator()
            else -> {
                println("unsupported filetype $fileName")
                return false
            }
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
            fileName.contains(ACTIVITY) -> return false
            else -> return false
        }
        if (model != null) {
            when {
                fileName.contains(FRAGMENT) -> {
                    val re = FragmentRewriter()
                    re.rewriteFragment(model, rewriter)
                }
                fileName.contains(ACTIVITY) -> {
                }
            }
        }
        // rewrite file
        file.writeText(rewriter.text)
        println("converted $fileName")
        return true
    }
}
