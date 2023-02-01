
import activityConverter.ActivityRewriter
import activityConverter.KtActivityCollator
import adapterConverter.AdapterCollator
import adapterConverter.AdapterRewriter
import fragmentConverter.FragmentRewriter
import fragmentConverter.KtFragmentCollator
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.TokenStreamRewriter
import org.antlr.v4.runtime.tree.ParseTreeWalker
import viewholderConverter.ViewholderCollator
import viewholderConverter.ViewholderRewriter
import java.io.File
import java.lang.IllegalArgumentException

class FileConverter {
    companion object {
        private const val FRAGMENT = "Fragment"
        private const val ACTIVITY = "Activity"
        private const val VIEWHOLDER = "ViewHolder"
        private const val ADAPTER = "Adapter"
    }
    fun convert(file: File): Boolean {
        val fileName = file.absolutePath

        val collator = when {
            fileName.contains("baseFragment") || fileName.contains("baseActivity") -> {
                println("base file, skipping")
                return false
            }
            fileName.contains("Component") || fileName.contains("Module") -> {
                println("dagger file")
                return false
            }
            fileName.contains(ACTIVITY) -> KtActivityCollator()
            fileName.contains(FRAGMENT) -> KtFragmentCollator()
            fileName.contains(VIEWHOLDER) -> ViewholderCollator()
            fileName.contains(ADAPTER) -> AdapterCollator()
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

        val model = collator.converterModel()

        if (model != null) {
            when {
                fileName.contains(FRAGMENT) -> {
                    val re = FragmentRewriter()
                    re.rewrite(model, rewriter, fileName)
                }
                fileName.contains(ACTIVITY) -> {
                    val re = ActivityRewriter()
                    re.rewrite(model, rewriter, fileName)
                }
                fileName.contains(VIEWHOLDER) -> {
                    val re = ViewholderRewriter()
                    re.rewrite(model, rewriter, fileName)
                }
                fileName.contains(ADAPTER) -> {
                    AdapterRewriter().rewrite(model, rewriter, fileName)
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
        if(model == null ) {
            println("skipped $fileName")
        } else {
            println("converted $fileName")
        }
        return true
    }
}
