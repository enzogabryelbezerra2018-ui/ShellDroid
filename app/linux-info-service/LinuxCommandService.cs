using System.Collections.Generic;
using System.IO;
using System.Text.Json;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Http;

namespace ShellDroid.CommandAI
{
    public class LinuxCommand
    {
        public string Name { get; set; } = "";
        public string Description { get; set; } = "";
    }

    public static class LinuxCommandService
    {
        private static readonly string JsonPath =
            Path.Combine(AppContext.BaseDirectory, "LinuxCommands.json");

        private static List<LinuxCommand> _commands = new();

        static LinuxCommandService()
        {
            if (File.Exists(JsonPath))
            {
                var json = File.ReadAllText(JsonPath);
                _commands = JsonSerializer.Deserialize<List<LinuxCommand>>(json)
                            ?? new List<LinuxCommand>();
            }
        }

        public static void MapEndpoints(WebApplication app)
        {
            app.MapGet("/", () => "ShellDroid.CommandAI ativo 🚀");

            app.MapGet("/api/commands", () => _commands);

            app.MapGet("/api/command/{name}", (string name) =>
            {
                var cmd = _commands.Find(c => c.Name.Equals(name, StringComparison.OrdinalIgnoreCase));
                if (cmd != null) return Results.Json(cmd);

                var suggestion = LinuxCommandLearner.FindClosest(name);
                if (suggestion != null)
                {
                    return Results.Json(new
                    {
                        message = $"❌ Comando '{name}' não encontrado. Você quis dizer '{suggestion.Name}'?",
                        suggestion
                    });
                }

                return Results.NotFound(new { message = $"❌ Comando '{name}' não encontrado e sem sugestão." });
            });

            // Novo endpoint: aprendizado de comandos
            app.MapPost("/api/learn", async (HttpRequest req) =>
            {
                var data = await JsonSerializer.DeserializeAsync<Dictionary<string, string>>(req.Body);
                if (data == null || !data.ContainsKey("name") || !data.ContainsKey("description"))
                    return Results.BadRequest(new { message = "Formato inválido. Use { name, description }" });

                string result = LinuxCommandLearner.LearnNewCommand(data["name"], data["description"]);
                return Results.Json(new { message = result });
            });
        }
    }
}
