import java.io.File

fun main(args: Array<String>) {
    println("Program arguments: ${args.joinToString()}")

    val root = args[0]

    val fc = FileConverter()
    File(root).walk().forEach {
        if (it.isFile) {
            println("converting $it")
            fc.convert(it)
        }
    }
}
