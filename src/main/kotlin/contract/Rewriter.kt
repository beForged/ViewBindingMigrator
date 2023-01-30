package contract

import org.antlr.v4.runtime.TokenStreamRewriter

interface Rewriter {
    fun rewrite(model: ConverterModel, rewriter: TokenStreamRewriter, filename: String): TokenStreamRewriter
}
