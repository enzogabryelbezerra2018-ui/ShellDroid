using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text.Json;

namespace ShellDroid.CommandAI
{
    public static class LinuxCommandLearner
    {
        private static readonly string FilePath =
            Path.Combine(AppContext.BaseDirectory, "LinuxCommands.json");

        public static string LearnNewCommand(string name, string description)
        {
            var cmds = LoadCommands();

            if (cmds.Any(c => c.Name.Equals(name, StringComparison.OrdinalIgnoreCase)))
                return $"⚠️ O comando '{name}' já existe.";

            cmds.Add(new LinuxCommand { Name = name, Description = description });
            SaveCommands(cmds);

            return $"✅ Novo comando '{name}' aprendido com sucesso!";
        }

        public static LinuxCommand? FindClosest(string input)
        {
            var cmds = LoadCommands();
            LinuxCommand? closest = null;
            int bestDistance = int.MaxValue;

            foreach (var cmd in cmds)
            {
                int dist = LevenshteinDistance(input, cmd.Name);
                if (dist < bestDistance)
                {
                    bestDistance = dist;
                    closest = cmd;
                }
            }

            return bestDistance <= 2 ? closest : null; // tolera 2 letras erradas
        }

        private static List<LinuxCommand> LoadCommands()
        {
            if (!File.Exists(FilePath))
                return new List<LinuxCommand>();
            var json = File.ReadAllText(FilePath);
            return JsonSerializer.Deserialize<List<LinuxCommand>>(json) ?? new List<LinuxCommand>();
        }

        private static void SaveCommands(List<LinuxCommand> cmds)
        {
            var json = JsonSerializer.Serialize(cmds, new JsonSerializerOptions { WriteIndented = true });
            File.WriteAllText(FilePath, json);
        }

        private static int LevenshteinDistance(string a, string b)
        {
            if (string.IsNullOrEmpty(a)) return b.Length;
            if (string.IsNullOrEmpty(b)) return a.Length;

            var dp = new int[a.Length + 1, b.Length + 1];
            for (int i = 0; i <= a.Length; i++) dp[i, 0] = i;
            for (int j = 0; j <= b.Length; j++) dp[0, j] = j;

            for (int i = 1; i <= a.Length; i++)
            {
                for (int j = 1; j <= b.Length; j++)
                {
                    int cost = (a[i - 1] == b[j - 1]) ? 0 : 1;
                    dp[i, j] = Math.Min(
                        Math.Min(dp[i - 1, j] + 1, dp[i, j - 1] + 1),
                        dp[i - 1, j - 1] + cost
                    );
                }
            }

            return dp[a.Length, b.Length];
        }
    }
}
