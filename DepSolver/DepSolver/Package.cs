using System;
using System.Collections.Generic;

namespace DepSolver
{
    class Package
    {
        public string Name { get; }
        public Version Version { get; }
        public int Size { get; }
        public List<List<string>> Depends { get; }
        public List<string> Conflicts { get; }

        public Package(string name, string version, int size, List<List<string>> depends, List<string> conflicts)
        {
            Name = name;
            if (version.Length == 1)
            {
                version += ".0";
            }
            Version = new Version(version);
            Size = size;
            Depends = depends ?? new List<List<string>>();
            Conflicts = conflicts ?? new List<string>();
        }
    }
}
