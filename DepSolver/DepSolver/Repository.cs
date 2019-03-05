using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace DepSolver
{
    class Repository
    {
        public Dictionary<string, List<Package>> Packages = new Dictionary<string, List<Package>>();

        public Repository(List<Package> packages)
        {
            packages.ToList().ForEach(p =>
            {
                if (this.Packages.TryGetValue(p.Name, out var val))
                {
                    val.Add(p);
                    this.Packages[p.Name] = val;
                }
                else
                {
                    var list = new List<Package> { p };
                    this.Packages.Add(p.Name, list);
                }
            });

        }
    }
}
