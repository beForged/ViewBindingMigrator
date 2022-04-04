class SyntheticMigratorUtils {

    companion object {
        private val snakeRegex = "_[a-zA-Z]".toRegex()

        // first letter is not capitalized
        fun String.snakeToLowerCamelCase(): String {
            return replace(snakeRegex) {
                it.value.replace("_", "").uppercase()
            }
        }

        // first letter is capitalized
        fun String.snakeToUpperCamelCase(): String {
            return this.snakeToLowerCamelCase().capitalize()
        }
    }
}
