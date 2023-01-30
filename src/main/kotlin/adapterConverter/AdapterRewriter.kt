package adapterConverter

import contract.ConverterModel
import contract.Rewriter
import org.antlr.v4.runtime.TokenStreamRewriter

class AdapterRewriter : Rewriter {

    fun adapterRewriter(model: AdapterConverterModel, rewriter: TokenStreamRewriter): TokenStreamRewriter {
        model.onCreateLocation?.let {
            rewriter.replace(
                it.a, it.b,
                model.convertAdapter()
            )
        }
        return rewriter
    }

    override fun rewrite(model: ConverterModel, rewriter: TokenStreamRewriter, filename: String): TokenStreamRewriter {
        return adapterRewriter(model as AdapterConverterModel, rewriter)
    }
}
