using System.Collections.Generic;
using System.Text.Json;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Hosting;

namespace LinuxInfoService
{
    public class LinuxCommand
    {
        public string Name { get; set; } = "";
        public string Description { get; set; } = "";
    }

    public static class CommandDatabase
    {
        public static List<LinuxCommand> Commands = new()
        {
            new LinuxCommand { Name = "ls", Description = "Lista arquivos e diretórios." },
            new LinuxCommand { Name = "cd", Description = "Muda o diretório atual." },
            new LinuxCommand { Name = "pwd", Description = "Mostra o caminho do diretório atual." },
            new LinuxCommand { Name = "cat", Description = "Exibe o conteúdo de arquivos." },
            new LinuxCommand { Name = "echo", Description = "Imprime uma linha de texto." },
            new LinuxCommand { Name = "rm", Description = "Remove arquivos ou diretórios." },
            new LinuxCommand { Name = "mkdir", Description = "Cria um novo diretório." },
            new LinuxCommand { Name = "cp", Description = "Copia arquivos e diretórios." },
            new LinuxCommand { Name = "mv", Description = "Move ou renomeia arquivos e diretórios." },
            new LinuxCommand { Name = "top", Description = "Mostra processos em tempo real." }
        };
    }

    public class LinuxCommandService
    {
        public static void MapEndpoints(WebApplication app)
        {
            app.MapGet("/api/commands", () => CommandDatabase.Commands);

            app.MapGet("/api/command/{name}", (string name) =>
            {
                var cmd = CommandDatabase.Commands
                    .Find(c => c.Name.Equals(name, System.StringComparison.OrdinalIgnoreCase));
                return cmd is null
                    ? Results.NotFound(new { message = "Comando não encontrado." })
                    : Results.Json(cmd);
            });
        }
    }
}
