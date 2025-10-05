suspend fun getLinuxCommandInfo(cmd: String): String {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL("http://127.0.0.1:5000/api/command/$cmd")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 2000
            conn.readTimeout = 2000
            if (conn.responseCode == 200) {
                conn.inputStream.bufferedReader().use { it.readText() }
            } else {
                "Comando desconhecido ou servidor offline."
            }
        } catch (e: Exception) {
            "Erro de conexão com o serviço C#: ${e.message}"
        }
    }
}
