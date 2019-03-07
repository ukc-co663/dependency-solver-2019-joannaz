package depsolver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import depsolver.Package;

public class Main {

	static ArrayList<Package> 	repo;
	static ArrayList<String> 	initial;
	static ArrayList<String> 	toInstall;
	static Repository 		realRepo;
	

	public static void main(String[] args) throws IOException {
		
		String repoPath 		= 	null;
		String initialPath 		=  	null;
		String constraintsPath 	= 	null;

		if(args.length == 0) {
			repoPath = "tests/seen-4/repository.json";
			initialPath = "tests/seen-4/initial.json";
			constraintsPath = "tests/seen-4/constraints.json";
		} else {
			repoPath 		= args[0];
			initialPath 	= args[1];
			constraintsPath = args[2];
		}


		TypeReference<ArrayList<String>> strListType = new TypeReference<ArrayList<String>>() {};
		TypeReference<ArrayList<Package>> repoType 	= new TypeReference<ArrayList<Package>>() {};
		// Repo
		repo = JSON.parseObject(readFile(repoPath), repoType);
		realRepo = new Repository(repo);
		// Initial state
		initial = JSON.parseObject(readFile(initialPath), strListType);
		// What I need to install
		toInstall = JSON.parseObject(readFile(constraintsPath), strListType);    

		realRepo.calculateDeps(repo);
		realRepo.calculateConflicts(repo);

		Util.solve(initial,toInstall, realRepo);
		

	}

	static String readFile(String filename) throws IOException {
		BufferedReader br 	= new BufferedReader(new FileReader(filename));
		StringBuilder sb 	= new StringBuilder();
		br.lines().forEach(line -> sb.append(line));
		br.close();
		return sb.toString();
	}

	private static void testCode() {
		System.out.println("------------------------ TEST STUFF ---------------------");
//		System.out.println("Get all conflicts of B=3.2");
//		
//		System.out.println(Repository.repo.get("B").get("3.2").getConflicts());		
//		
//		System.out.println("Get all dependencies of D=10.3.1");
//		for(List<String> x : Repository.repo.get("D").get("10.3.1").getDepends()) {
//			System.out.println(x);
//		}
		//System.out.println(Repository.repo.get("D").get("10.3.1").toCNFString());
		
		System.out.println("------------------------ CNF STUFF ---------------------");
		
		ArrayList<ArrayList<Constraint>> dependencies = Util.calcDep("A=2.01", realRepo);
		ArrayList<ArrayList<Constraint>> depsAndCons = Util.calcConflicts(dependencies, realRepo);
		ArrayList<String> solutions = Util.calculateFormula(depsAndCons);
		ArrayList<String> validSolutions = Util.SATSolve(solutions);
		ArrayList<ArrayList<String>> nf = Util.convertBack(validSolutions, initial);
		ArrayList<String> smallestSol = Util.getSmallestWeight(nf, realRepo);
		Util.reorderDependencies(smallestSol, realRepo);
			
		System.out.println("---------------------------------------------------------");
		
	}
}
