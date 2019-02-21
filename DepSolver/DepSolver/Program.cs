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
        private static Repository repo;
        private static List<string> initial;


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
                //var basePath = Directory.GetCurrentDirectory(); // Ubuntu base path
                var basePath = Path.GetFullPath(Path.Combine(AppContext.BaseDirectory, "..\\..\\..\\..\\..\\")); // For VS Studio
                repoPath = Path.Combine(basePath, "tests/example-0/repository.json");
                initialPath = Path.Combine(basePath, "tests/example-0/initial.json");
                constraintsPath = Path.Combine(basePath, "tests/example-0/constraints.json");
            }

            repo = new Repository(JsonConvert.DeserializeObject<List<Package>>(File.ReadAllText(repoPath)));
            initial = JsonConvert.DeserializeObject<List<string>>(File.ReadAllText(initialPath));
            var toInstall = JsonConvert.DeserializeObject<List<string>>(File.ReadAllText(constraintsPath));

            var x = Solve(toInstall, new List<string>());
            var installedPack = new List<string>();
        }

        public static List<string> Solve(List<string> toInstall, List<string> install)
        {
            toInstall.ForEach(con =>
            {
                var name = con.Substring(0, 1);
                var op = con.Substring(1, con.Length - 1);
                // Get the thing to install / uninstall
                if (op.Equals("+"))
                {
                    // Find the package in the repo
                    if (repo.Packages.TryGetValue(name, out var packToInstall))
                    {
                        // Init conflict list??
                        List<string> conflicts = new List<string>();
                        // If there is only one repo:
                        if (packToInstall.Count == 1)
                        {
                            // 
                            packToInstall[0].Depends.ForEach(x =>
                            {
                                // Traverse down and recurse?
                            });
                        }
                    }
                }

            });

            return new List<string>();
        }

    }
}
