package fragmentConverter

import contract.ConverterModel
import contract.Rewriter
import org.antlr.v4.runtime.TokenStreamRewriter

class FragmentRewriter : Rewriter {
    fun rewriteFragment(model: FragmentConverterModel, rewriter: TokenStreamRewriter): TokenStreamRewriter {
        // sanity check - ideally enforce idempotency
        if (model.syntheticImports.isEmpty()) {
            println("fragment contains no synthetic imports, skipping")
            return rewriter
        }
        // remove all synthetic imports
        model.syntheticImports.map {
            rewriter.replace(it.interval.a, it.interval.b, "")
        }

        // remove the layout id function (cant do this since its required by parent fragment)
        // need to instead remove
        // rewriter.replace(model.layoutIdFunction.a, model.layoutIdFunction.b, "")

        /* add onCreatedView function to replace layoutId setting, but needs layout removal
        //which includes butterknife work
        if(model.onCreatedViewExists) {
            rewriter.insertAfter(model.onCreatedViewLocation!!.a, model.onCreatedViewLayout())
        } else {
            rewriter.insertAfter(model.layoutIdFunction.b, model.onCreatedViewLayout())
        }
         */

        // add private binding variables
        // add in onCreate static bindings
        if (model.onCreateExists) {
            rewriter.insertAfter(model.variableDeclInterval.a, model.bindingVariables())
            rewriter.insertAfter(model.onCreateLocation!!.a, model.onCreateViewOutput())
        } else {
            // create on onCreate and add bindings
            rewriter.insertAfter(model.variableDeclInterval.a, model.onCreateViewOutputExternal())
            rewriter.insertAfter(model.variableDeclInterval.a, model.bindingVariables())
        }

        // add in ondestroyview
        if (model.onDestroyExists) {
            rewriter.insertAfter(model.onDestroyLocation!!.a, model.onDestroyViewInternal())
        } else {
            rewriter.insertAfter(model.variableDeclInterval.a, model.onDestroyViewExternal())
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
