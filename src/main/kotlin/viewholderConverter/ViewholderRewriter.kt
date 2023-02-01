package viewholderConverter

import contract.ConverterModel
import contract.Rewriter
import org.antlr.v4.runtime.TokenStreamRewriter

class ViewholderRewriter : Rewriter {

    fun rewriteViewHolder(model: ViewholderConverterModel, rewriter: TokenStreamRewriter): TokenStreamRewriter {
        // sanity check - ideally enforce idempotency
        if (model.syntheticImports.isEmpty()) {
            println("fragment contains no synthetic imports, skipping")
            return rewriter
        }
        // remove all synthetic imports
        model.syntheticImports.map {
            rewriter.replace(it.interval.a, it.interval.b, "")
        }

        // replace itemview with itembinding
        model.viewParamLocation?.let {
            rewriter.replace(it.a, it.b, model.replaceItemView())
        }
        // replace every view instance of synthetic with ${CamelCaseLayoutId}Binding!!.view
        // if they have requireActivity(), may need to replace it with binding and snake->camelCase
        model.viewReferences.map {
            rewriter.replace(
                it.interval.a,
                it.interval.b,
                model.replaceViewReference(it.viewList)
            )
        }

        return rewriter
    }
    override fun rewrite(model: ConverterModel, rewriter: TokenStreamRewriter, filename: String): TokenStreamRewriter {
        return rewriteViewHolder(model as ViewholderConverterModel, rewriter)
    }
}
