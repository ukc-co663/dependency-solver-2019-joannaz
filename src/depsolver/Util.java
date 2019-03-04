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
					List<List<String>> dependenciesForPackage = p.getDepends();
					if(dependenciesForPackage.size() > 0) {
						for(List<String> deps : dependenciesForPackage) {
							
							
						}
						currPackCNF += calcDep(dependenciesForPackage, repo);
					}
					
					CNF += currPackCNF;
				}
			}
		}
		return CNF;
	}
	
	static String calcDep(List<List<String>> identifier, Repository repo) {
		//TODO: IMPLEMENT
		return "";
	}
	

	static String depCalcHell(String identifier, Repository repo) {
		ArrayList<ArrayList<String>> deps = new ArrayList<ArrayList<String>>();
		Package pack = repo.getSpecific(identifier);
		
		for(List<String> dependences : pack.getDepends()) {
			System.out.println(dependences);
		}
		
		return "";

	}

}
