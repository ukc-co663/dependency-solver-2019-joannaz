package depsolver;

import java.util.ArrayList;

public class UninstallStructure {
	public static ArrayList<ArrayList<Constraint>> removedUninstall;
	public static ArrayList<ArrayList<String>> uninstallForEachSolution;
	public static ArrayList<ArrayList<String>> removedUninstallString;
	
	
	public UninstallStructure(ArrayList<ArrayList<Constraint>> removedUninstall, ArrayList<ArrayList<String>> uninstallForEachSolution, ArrayList<ArrayList<String>> newSolString) {
		UninstallStructure.removedUninstall = removedUninstall;
		UninstallStructure.uninstallForEachSolution = uninstallForEachSolution;
		UninstallStructure.removedUninstallString = newSolString;
	}

	
	public static ArrayList<String> lookup(ArrayList<String> sol) {
		int i = 0;
//		System.out.println(uninstallForEachSolution);
		for(i = 0; i < removedUninstall.size(); i++) {
			ArrayList<String> x = removedUninstallString.get(i);
			if(x.containsAll(sol)) {
				return uninstallForEachSolution.get(i);
			}
		}
		
		return null;
	}
}
