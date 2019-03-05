package depsolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;

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

	public static String getCNF(List<String> toInstall, Repository repo) {
		String CNF = "";
		for(String packageToDo : toInstall) {
			String packageToDoName = packageToDo.substring(1);
			String packageOperator = packageToDo.substring(0, packageToDo.length() -1).trim();
			// Install
			if(packageOperator.equals("+")) {
				// List of packs to install
				List<Package> packsToInstall = new ArrayList<Package>();
				// If it just wants a single one
				if(packageToDo.contains("=")) {
					// Just get the specific one
					Package packToInstall = repo.getSpecific(packageToDoName);
					packsToInstall.add(packToInstall);
				} else {
					// It either wants any pack, or ones that are greater, smaller etc. 
					List<Package> multiplePacks = repo.getPackages(packageToDoName);
					packsToInstall.addAll(multiplePacks);
				}

				for(Package p : packsToInstall) {
					String currPackCNF = "";
					currPackCNF+=p.toString();
					ArrayList<ArrayList<Constraint>> formulaList = calc(p.toString(), repo);
					CNF += currPackCNF;
				}
			}
		}
		return CNF;
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
		
		if(validSolutions.size() == 1) {
			return validSolutions.get(0);
		}
		
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
		ArrayList<String> orderedSolution = new ArrayList<>();
		
		for(String p : solution) {
			Package pac = repo.getSpecific(p);
			if(pac.getDepends().size() == 0) {
				
			}
		}
		
		
		return orderedSolution;
	}
}
