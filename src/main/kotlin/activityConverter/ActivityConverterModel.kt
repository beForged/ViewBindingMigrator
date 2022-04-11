package activityConverter

import SyntheticMigratorUtils.Companion.snakeToLowerCamelCase
import SyntheticMigratorUtils.Companion.snakeToUpperCamelCase
import contract.ConverterModel
import org.antlr.v4.runtime.misc.Interval

class ActivityConverterModel (
    val bindingName: String,
    val onCreateExists: Boolean,
    val findItem: Boolean,
    val onCreateLocation: Interval?,
    val syntheticImports: List<ConverterModel.SyntheticImport>,
    val viewReferences: List<ConverterModel.ViewReference>
): ConverterModel {

    fun bindingClassName(): String {
        return "${bindingName.snakeToUpperCamelCase()}Binding"
    }

    fun bindingClassVariable(): String {
        return "private lateinit var binding: ${bindingClassName()}"
    }




}
