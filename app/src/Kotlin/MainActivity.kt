package com.shelldroid

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var rvTerminal: RecyclerView
    private lateinit var etCommand: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var fabClear: FloatingActionButton
    private lateinit var fabHistory: FloatingActionButton
    private lateinit var toolbar: MaterialToolbar

    private val adapter = TerminalAdapter(mutableListOf())
    private val uiScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvTerminal = findViewById(R.id.rvTerminal)
        etCommand = findViewById(R.id.etCommand)
        btnSend = findViewById(R.id.btnSend)
        fabClear = findViewById(R.id.fabClear)
        fabHistory = findViewById(R.id.fabHistory)
        toolbar = findViewById(R.id.toolbar)

        rvTerminal.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        rvTerminal.adapter = adapter

        btnSend.setOnClickListener { submitCommand() }

        etCommand.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                submitCommand()
                true
            } else false
        }

        fabClear.setOnClickListener {
            adapter.clear()
        }

        fabHistory.setOnClickListener {
            // simples: copia todo o histórico para a área de transferência
            val all = adapter.getAll().joinToString("\n\n") { "${it.command}\n${it.output}" }
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("ShellDroid history", all))
            toolbar.subtitle = "Histórico copiado"
            // remove subtitle depois de 2s
            uiScope.launch {
                kotlinx.coroutines.delay(2000)
                toolbar.subtitle = null
            }
        }

        // opcional: comando inicial
        runCommand("echo Bem-vindo ao ShellDroid")
    }

    private fun submitCommand() {
        val cmd = etCommand.text.toString().trim()
        if (cmd.isEmpty()) return
        etCommand.setText("")
        runCommand(cmd)
    }

    private fun runCommand(command: String) {
        // mostra comando imediatamente com "executando..."
        adapter.addLine(TerminalLine(command, "executando..."))
        rvTerminal.scrollToPosition(adapter.itemCount - 1)

        uiScope.launch {
            val output = withContext(Dispatchers.IO) {
                executeShellCommand(command)
            }
            // substituir último “executando...” pela saída real
            // como simplificação, atualiza a última linha
            val pos = adapter.itemCount - 1
            if (pos >= 0) {
                // remover e re-adicionar (simples)
                adapter.apply {
                    // hack simples: remove e readd
                    (this.items as MutableList).removeAt(pos)
                    notifyItemRemoved(pos)
                    addLine(TerminalLine(command, output))
                }
            } else {
                adapter.addLine(TerminalLine(command, output))
            }
            rvTerminal.scrollToPosition(adapter.itemCount - 1)
        }
    }

    private fun executeShellCommand(command: String): String {
        try {
            // Usa sh -c "comando" para interpretar pipes/redirections
            val process = ProcessBuilder("sh", "-c", command)
                .redirectErrorStream(true)
                .start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val out = StringBuilder()
            var line: String?

            while (true) {
                line = reader.readLine() ?: break
                out.append(line).append("\n")
            }
            reader.close()
            process.waitFor()
            val result = out.toString().trim()
            return if (result.isEmpty()) "(sem saída)" else result
        } catch (e: Exception) {
            return "Erro: ${e.message}"
        }
    }
}
