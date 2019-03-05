package depsolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class Util {

	static BiMap<String,String> dict = HashBiMap.create();

	/**
	 * Lexicographically compare versions
	 * @param v Version
	 * @param o Another version
	 * @return 0 if equals, 1 if v is greater than o, and -1 if v is smaller than o
	 */
	public static int compareVersion(String v, String o) {

		ArrayList<Integer> myVer = castToInt(v.split("\\."));
		ArrayList<Integer> compareVer = castToInt(o.split("\\."));


		int length = myVer.size();

		if(compareVer.size() > length) {
			length = compareVer.size();
			for(int i = myVer.size(); i < compareVer.size(); i++) {
				myVer.add(0);
			}
		} else {
			for(int i = compareVer.size(); i < myVer.size(); i++) {
				compareVer.add(0);
			}
		}

		for(int i = 0; i < length; i++) {
			if(myVer.get(i) == compareVer.get(i)) {
				continue;
			}
			else if(myVer.get(i) < compareVer.get(i)) {
				return -1;
			}
			else if(myVer.get(i) > compareVer.get(i)) {
				return 1;
			}
		}

		return 0;
	}

	/**
	 * Convert a primitive String array into an ArrayList
	 * @param arr String array
	 * @return ArrayList of integers
	 */
	private static ArrayList<Integer> castToInt(String[] arr) {
		ArrayList<Integer> returnVal = new ArrayList<Integer>();
		for(String s : arr) {
			returnVal.add(Integer.parseInt(s));
		}

		return returnVal;
	}

	public static void solve(List<String> toInstall, Repository repo) {
		for(String packageToDo : toInstall) {
			
			ArrayList<ArrayList<String>> solutions = new ArrayList<ArrayList<String>>();
			String packageToDoName = packageToDo.substring(1);

			String packageOperator = packageToDo.substring(0, 1).trim();			

			// Install
			if(packageOperator.equals("+")) {
				// List of packs to install			
				solveMain(packageToDoName, repo);
			}
		}
	}
	
	static void solveMain(String p, Repository repo) {
		ArrayList<ArrayList<String>> allSolutions = new ArrayList<ArrayList<String>>();
		ArrayList<Package> allPackages = repo.getPackages(p);
		for(Package pack : allPackages) {
			ArrayList<ArrayList<Constraint>> dependencies = calc(pack.toString(), repo);
			ArrayList<ArrayList<Constraint>> depsAndCons = calcConflicts(dependencies, repo);
			ArrayList<String> solutions = calculateFormula(depsAndCons);
			ArrayList<String> validSolutions = SATSolve(solutions);
			ArrayList<ArrayList<String>> nf = convertBack(validSolutions);
			ArrayList<String> smallestSol = getSmallestWeight(nf, repo);
			ArrayList<String> sortedGraph = reorderDependencies(smallestSol, repo);
			allSolutions.add(sortedGraph);
		}
		
		ArrayList<String> smallestSol = getSmallestWeight(allSolutions, repo);
		
		prettyPrintSolution(smallestSol);
		
	}

	/**
	 * Function to get the combinations of possible solutions for this repository
	 * @param id ID of the package to be installed
	 * @param repo Repository
	 * @return Combination of solutions
	 */
	static ArrayList<ArrayList<Constraint>> calc(String id, Repository repo){
		ArrayList<ArrayList<Constraint>> deps = calcDep(id, repo);
		ArrayList<ArrayList<Constraint>> depsAndCons = calcConflicts(deps, repo);
		return depsAndCons;
	}

	/**
	 * Calculate the conflicts by looking at each solution
	 * @param deps The dependency list
	 * @param repo The repository
	 * @return The combinations with conflicts in
	 */
	static ArrayList<ArrayList<Constraint>> calcConflicts(ArrayList<ArrayList<Constraint>> deps, Repository repo){
		// List of Dependencies
		ArrayList<ArrayList<Constraint>> dependencies = new ArrayList<ArrayList<Constraint>>(); 

		for(ArrayList<Constraint> solution : deps) {
			// [A=12, B=23]
			ArrayList<Constraint> newSolutionWithCons = new ArrayList<Constraint>(solution);
			for(Constraint c : solution) 
			{
				Package p = repo.getSpecific(c.name);
				List<String> cons = p.getConflicts();

				// Conflicts 
				for(String constraint : cons) {

					Constraint con = new Constraint(Op.NEG, constraint);
					newSolutionWithCons.add(con);
				}
			}
			dependencies.add(newSolutionWithCons);		
		}
		return dependencies;
	}

	/**
	 * Traverse the repository to get the dependency list
	 * @param id ID of the package to be installed
	 * @param repo The repository
	 * @return The List of dependencies to be installed without constraints
	 */
	static ArrayList<ArrayList<Constraint>> calcDep(String id, Repository repo) {
		ArrayList<ArrayList<Constraint>> comb = new ArrayList<ArrayList<Constraint>>();
		Package p = repo.getSpecific(id);
		
		List<List<String>> deps = p.getDepends();

		// Base case, no dependencies
		ArrayList<Constraint> singleComb = new ArrayList<>();			
		singleComb.add(new Constraint(Op.POS, id));
		comb.add(singleComb);

		// Some dependencies
		for(List<String> and : deps) {

			ArrayList<ArrayList<Constraint>> temp = new ArrayList<>();

			for(String or : and) {				
				ArrayList<ArrayList<Constraint>> dependencies = calcDep(or, repo);
				for(ArrayList<Constraint> combination : dependencies) {

					for(ArrayList<Constraint> r : comb) {
						ArrayList<Constraint> clone = (ArrayList<Constraint>) r.clone();
						clone.addAll(combination);
						temp.add(clone);
					}

				}
			}
			comb = temp;
		}
		return comb;
	}

	/**
	 * Calculate formula for SAT solver
	 * @param formulas ArrayList of solutions
	 * @return ArrayList of String
	 */
	static ArrayList<String> calculateFormula(ArrayList<ArrayList<Constraint>> formulas) {

		ArrayList<String> solutions = new ArrayList<String>();

		Integer i = 0;

		for(ArrayList<Constraint> solution : formulas) {
			String f = "";
			for(Constraint c : solution) {
				String constraintId = "";

				if(dict.inverse().get(c.name)== null) {
					dict.put(i.toString(), c.name);
					constraintId = i.toString();
					i++;
				} else {
					constraintId = dict.inverse().get(c.name);
				}

				if(f.isEmpty()) {
					if(c.constraint == Op.NEG) {
						f += " ~" + constraintId;
					} else {
						f += constraintId;
					}
				} else {
					if(c.constraint == Op.NEG) {
						f += " & ~" + constraintId;
					} else {
						f += " & " + constraintId;
					}

				}

			}
			solutions.add(f);
		}
		return solutions;
	}

	/**
	 * SAT Solve the formulas
	 * @param solutions the solutions to evaluate
	 * @return the solutions that are valid
	 */
	static ArrayList<String> SATSolve(ArrayList<String> solutions) {
		ArrayList<String> validSolutions = new ArrayList<>();
		final FormulaFactory f = new FormulaFactory();
		final PropositionalParser p = new PropositionalParser(f);

		for(String formula : solutions) {
			try {
				final Formula sol = p.parse(formula);
				final SATSolver miniSat = MiniSat.miniSat(f);
				miniSat.add(sol);
				final Tristate result = miniSat.sat();
				if(result == Tristate.TRUE) {
					validSolutions.add(formula);
				}
			} catch (ParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return validSolutions;
	}

	/**
	 * Convert the solutions back to normal form
	 * e.g. 1 will be converted back to A=2.01
	 * @param validSolutions
	 * @return
	 */
	static ArrayList<ArrayList<String>> convertBack(ArrayList<String> validSolutions) {
		ArrayList<ArrayList<String>> totalValidSolutions = new ArrayList<ArrayList<String>>();
		for(String valSol : validSolutions) {
			ArrayList<String> valSolConvert = new ArrayList<>();
			String[] packages = valSol.split("&");
			for(String p : packages) {
				String packageName = p.trim();
				if(!packageName.contains("~")) {
					valSolConvert.add(dict.get(packageName));
				}
			}
			totalValidSolutions.add(valSolConvert);
		}

		return totalValidSolutions;
	}

	/**
	 * Get the smallest weight of the solutions
	 * @param validSolutions the list of valid solutions
	 * @param repo the repository to get the size of each package from
	 * @return the smallest solution
	 */
	static ArrayList<String> getSmallestWeight(ArrayList<ArrayList<String>> validSolutions, Repository repo) {
		
		int bestWeight = Integer.MAX_VALUE;
		ArrayList<String> bestSolution = new ArrayList<String>();

		for(ArrayList<String> solution : validSolutions) {
			int solutionWeight = 0;
			for(String p : solution) {
				solutionWeight += repo.getSpecific(p).getSize();
			}

			if(solutionWeight < bestWeight) {
				bestWeight = solutionWeight;
				bestSolution = solution;
			}
		}
		return bestSolution;
	}

	static ArrayList<String> reorderDependencies(ArrayList<String> solution, Repository repo){
		// solution = [A=2.01, C=1, D=10.3.1]
		DefaultDirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
		
		for(String p : solution) {
			graph.addVertex(p);
		}
		
		for(String p : solution) {
			Package pack = repo.getSpecific(p);
			for(List<String> dependencies : pack.getDepends()) {
				for(String dep : dependencies) {
					if(graph.containsVertex(dep.toString())) {
						graph.addEdge(p, dep.toString());
					}
				}
			}
		}
		
		TopologicalOrderIterator<String, DefaultEdge> topIterator;
		
		topIterator = new TopologicalOrderIterator<>(graph);
		String res = "";
		ArrayList<String> topoSortedGraph = new ArrayList<>();
		
		while(topIterator.hasNext()) {
			res = topIterator.next();
			topoSortedGraph.add("+" + res);
		}
		
		// We want to go up the tree, therefore we need to reverse
		Collections.reverse(topoSortedGraph);		
		return topoSortedGraph;
	}
	
	static void prettyPrintSolution(ArrayList<String> solution) {
		System.out.println(JSON.toJSONString(solution));
	}
}
