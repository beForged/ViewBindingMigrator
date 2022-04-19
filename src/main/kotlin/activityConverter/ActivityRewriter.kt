package activityConverter

import contract.ConverterModel
import contract.Rewriter
import org.antlr.v4.runtime.TokenStreamRewriter

class ActivityRewriter : Rewriter {

    fun rewriteActivity(model: ActivityConverterModel, rewriter: TokenStreamRewriter, name: String): TokenStreamRewriter {
        if (model.syntheticImports.isEmpty()) {
            println("no synthetic imports for $name, skipping")
            return rewriter
        }
        // remove synthetic imports
        model.syntheticImports.map {
            rewriter.replace(it.interval.a, it.interval.b, "")
        }
        // if findItemById or findItem is in the file, want to just dump and express need for manual fixing
        if (model.findItem) {
            println("findItem for $name failed, exiting with deleted synthetic imports")
            return rewriter
        }

        // add in private binding variables
        // and add oncreate bindings/function
        if (model.onCreateExists) {
            rewriter.insertBefore(model.onCreateLocation!!.a, model.bindingClassVariables())
            rewriter.insertBefore(model.onCreateBodyLocation!!.a, model.onCreateInternalInflate())
        } else {
            rewriter.insertAfter(model.variableDecl.a, model.bindingClassVariables() + "\n")
            rewriter.insertAfter(model.variableDecl.a, model.onCreateExternal() + "\n")
        }

        model.viewReferences.map {
            rewriter.replace(
                it.interval.a,
                it.interval.b,
                model.replaceViewReference(it.viewList)
            )
        }

        return rewriter
    }

    override fun rewrite(model: ConverterModel, rewriter: TokenStreamRewriter): TokenStreamRewriter {
        return rewriter
    }
}
