import activityConverter.ActivityConverterModel
import activityConverter.ActivityRewriter
import activityConverter.KtActivityCollator
import fragmentConverter.FragmentConverterModel
import fragmentConverter.FragmentRewriter
import fragmentConverter.KtFragmentCollator
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.TokenStreamRewriter
import org.antlr.v4.runtime.tree.ParseTreeWalker
import java.io.File
import java.lang.IllegalArgumentException

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
            fileName.contains("Component") || fileName.contains("Module") -> {
                println("dagger file")
                return false
            }
            fileName.contains(ACTIVITY) -> KtActivityCollator()
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
            fileName.contains(FRAGMENT) -> collator.converterModel()
            fileName.contains(ACTIVITY) -> collator.converterModel()
            else -> return false
        }
        if (model != null) {
            when {
                fileName.contains(FRAGMENT) -> {
                    val re = FragmentRewriter()
                    re.rewriteFragment(model as FragmentConverterModel, rewriter)
                }
                fileName.contains(ACTIVITY) -> {
                    val re = ActivityRewriter()
                    re.rewriteActivity(model as ActivityConverterModel, rewriter, fileName)
                }
            }
            try {
                file.writeText(rewriter.text)
            } catch (e: IllegalArgumentException) {
                println("failed to rewrite $fileName due to Illegal argument exception: skipping")
                println(e.message)
                return false
            }
        }
        // rewrite file
        println("converted $fileName")
        return true
    }
}
