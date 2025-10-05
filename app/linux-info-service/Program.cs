using Microsoft.AspNetCore.Builder;
using Microsoft.Extensions.Hosting;
using LinuxInfoService;

var builder = WebApplication.CreateBuilder(args);
var app = builder.Build();

LinuxCommandService.MapEndpoints(app);

app.Run("http://0.0.0.0:5000"); // roda na porta 5000
