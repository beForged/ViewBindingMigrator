package fragmentConverter

import contract.ConverterModel
import contract.Rewriter
import org.antlr.v4.runtime.TokenStreamRewriter

class FragmentRewriter : Rewriter {
    fun rewriteFragment(model: FragmentConverterModel, rewriter: TokenStreamRewriter): TokenStreamRewriter {
        // remove all synthetic imports
        model.syntheticImports.map {
            rewriter.replace(it.interval.a, it.interval.b, "")
        }

        // remove the layout id function (cant do this since its required by parent fragment)
        // rewriter.replace(model.layoutIdFunction.a, model.layoutIdFunction.b, "")

        // add private binding variables
        // add in onCreate static bindings
        if (model.onCreateExists) {
            rewriter.insertBefore(model.layoutIdFunction.a, model.bindingVariables())
            rewriter.insertAfter(model.onCreateLocation!!.a, model.onCreateViewOutput())
        } else {
            // create on onCreate and add bindings
            rewriter.insertBefore(model.layoutIdFunction.a, model.bindingVariables())
            rewriter.insertAfter(model.layoutIdFunction.b, model.onCreateViewOutputExternal())
        }

        // add in ondestroyview
        if (model.onDestroyExists) {
            rewriter.insertAfter(model.onDestroyLocation!!.a, model.onDestroyViewInternal())
        } else {
            rewriter.insertAfter(model.layoutIdFunction.b, model.onDestroyViewExternal())
        }

        // replace every view instance of synthetic with ${CamelCaseLayoutId}Binding!!.view
        // if they have requireActivity(), may need to replace it with binding and snake->camelCase
        model.viewReference.map {
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
