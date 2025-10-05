class MainActivity : AppCompatActivity() {
    companion object {
        init { System.loadLibrary("native-lib") }
    }

    private external fun stringFromNative(): String
    private external fun runNativeEcho(input: String): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ...
        // exemplo de uso:
        val fromNative = stringFromNative() // "Hello from C++"
        runCommand("echo Bem-vindo ao ShellDroid")
        // chamar native echo e mostrar no terminal
        adapter.addLine(TerminalLine("native_echo", runNativeEcho("ShellDroid")))
    }
}
