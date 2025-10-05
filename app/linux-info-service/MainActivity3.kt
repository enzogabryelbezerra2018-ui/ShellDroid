uiScope.launch {
    val info = getLinuxCommandInfo(command)
    adapter.addLine(TerminalLine("info", info))
    rvTerminal.scrollToPosition(adapter.itemCount - 1)
}
