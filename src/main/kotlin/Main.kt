import java.io.File

fun main(args: Array<String>) {
    println("Program arguments: ${args.joinToString("\n")}")

    val root = args[0]

    val fc = FileConverter()
    File(root).walk().forEach {
        if (it.isFile) {
            println("converting $it")
            fc.convert(it)
        }
    }
}
