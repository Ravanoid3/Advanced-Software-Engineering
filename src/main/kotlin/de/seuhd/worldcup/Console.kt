package de.seuhd.worldcup

/** Input helpers for the interactive menu. */
object Console {

    /** Read a line from stdin, returning an empty string on EOF. */
    fun readLineOrEmpty(): String = readlnOrNull().orEmpty()

    /**
     * Read an [Int] from stdin, reprompting until the input parses and (if
     * [validValues] is given) lies within the allowed set.
     */
    fun readInt(prompt: String, validValues: Set<Int>? = null): Int {
        while (true) {
            print(prompt)
            System.out.flush()
            val parsed = readLineOrEmpty().toIntOrNull()
            if (parsed != null && (validValues == null || parsed in validValues)) return parsed
            val allowed = validValues?.joinToString("/")?.let { " ($it)" }.orEmpty()
            println("Please enter a valid number$allowed.")
        }
    }

    /** Block until the user presses Enter. */
    fun waitForEnter() {
        println("Press Enter to continue ...")
        readLineOrEmpty()
    }
}
