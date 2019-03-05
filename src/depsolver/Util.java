package depsolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
		int i = 0;
		for(String s : arr) {
			returnVal.add(Integer.parseInt(s));
			i++;
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
					ArrayList<ArrayList<Constraint>> dependencies = calcDep(p.toString(), repo);
					ArrayList<ArrayList<Constraint>> depsAndCons = calcConflicts(dependencies, p.toString(), repo);
					CNF += currPackCNF;
				}
			}
		}
		return CNF;
	}
	
	static ArrayList<ArrayList<Constraint>> calcConflicts(ArrayList<ArrayList<Constraint>> deps, String id, Repository repo){
		Package p = repo.getSpecific(id);
		
		List<String> cons = p.getConflicts();
		
		for(ArrayList<Constraint> d : deps) {
			for(String c : cons) {
				// Get each conflict
				// TODO: Find conflict for each package needing to install
//				ArrayList<ArrayList<Constraint>> p = calcConflicts(deps, c, repo);
				Constraint con = new Constraint(Op.NEG, c.toString());
				d.add(con);
			}
		}
		
		
		return deps;
	}

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
}
