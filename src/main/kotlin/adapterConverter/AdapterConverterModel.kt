package adapterConverter

import SyntheticMigratorUtils.Companion.snakeToUpperCamelCase
import contract.ConverterModel
import org.antlr.v4.runtime.misc.Interval

class AdapterConverterModel(
    val onCreateText: String?,
    val onCreateLocation: Interval?
) : ConverterModel {

    fun convertAdapter(): String {
        val lst = onCreateText?.split("itemView")
        val transformed = lst?.map {
            val regex = Regex("layout.([a-zA-Z0-9\\_]+),\\s*parent")
            if (it.contains(regex)) {
                val match = regex.find(it)
                println(match?.groupValues)
                val name = "${match?.groupValues?.get(1)?.snakeToUpperCamelCase()}Binding"

                it.replace(
                    Regex("= LayoutInflater.from\\(parent.context\\)\\s*.inflate\\(\\s*R.layout.[a-zA-Z0-9\\_]+,\\s*parent"),
                    "itemBinding = $name.inflate(LayoutInflater.from(parent.context),\n\t\t\t\t\t\tparent"
                )
            } else {
                it
            }
        }
        return transformed?.joinToString(" ") ?: "TODO//FIXME"
    }
}
