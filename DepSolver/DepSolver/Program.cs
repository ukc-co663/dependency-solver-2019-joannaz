using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace DepSolver
{
    class Program
    {
        static void Main(string[] args)
        {

            string repoPath;
            string initialPath;
            string constraintsPath;

            if (args.Any())
            {
                repoPath = args[0];
                initialPath = args[1];
                constraintsPath = args[2];
            }
            else
            {
                    var basePath = Directory.GetParent(Environment.CurrentDirectory).Parent.Parent.FullName;
                    repoPath = Path.Combine(basePath, "tests\\example-0\\repository.json");
                    initialPath = Path.Combine(basePath, "tests\\example-0\\initial.json");
                    constraintsPath = Path.Combine(basePath, "tests\\example-0\\constraints.json");
            }

            
            List<Package> repo = JsonConvert.DeserializeObject<List<Package>>(File.ReadAllText(repoPath));
            List<string> initial = JsonConvert.DeserializeObject<List<string>>(File.ReadAllText(initialPath));
            List<string> constraints = JsonConvert.DeserializeObject<List<string>>(File.ReadAllText(constraintsPath));

            repo.ForEach(x =>
            {
                Console.Write("Name: ");
                Console.Write(x.Name);
                Console.Write(" Version: ");
                Console.Write(x.Version);
                Console.Write(" Size: ");
                Console.WriteLine(x.Size);

                x.Depends.ForEach(dep =>
                {
                    Console.Write("  Dependencies: ");
                    dep.ForEach(Console.Write);
                    Console.WriteLine();
                });
            });

        }
    }
}
