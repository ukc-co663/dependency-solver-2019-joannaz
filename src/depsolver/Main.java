package depsolver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.io.parsers.*;

import java.util.HashMap;

import depsolver.Package;

public class Main {

	static List<Package> 	repo;
	static List<String> 	initial;
	static List<String> 	toInstall;
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


		TypeReference<List<String>> strListType = new TypeReference<List<String>>() {};
		TypeReference<List<Package>> repoType 	= new TypeReference<List<Package>>() {};
		// Repo
		repo = JSON.parseObject(readFile(repoPath), repoType);
		realRepo = new Repository(repo);
		// Initial state
		initial = JSON.parseObject(readFile(initialPath), strListType);
		// What I need to install
		toInstall = JSON.parseObject(readFile(constraintsPath), strListType);    

		realRepo.calculateDeps(repo);
		realRepo.calculateConflicts(repo);

		testCode();

		String cnf = Util.getCNF(toInstall, realRepo);

	}

	static String readFile(String filename) throws IOException {
		BufferedReader br 	= new BufferedReader(new FileReader(filename));
		StringBuilder sb 	= new StringBuilder();
		br.lines().forEach(line -> sb.append(line));
		return sb.toString();
	}

	static void testCode() {
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
		
		ArrayList<ArrayList<Constraint>> dependencies = Util.calcDep("A=1", realRepo);
		ArrayList<ArrayList<Constraint>> depsAndCons = Util.calcConflicts(dependencies, "A=1", realRepo);
		
		System.out.println(depsAndCons);
		
			
		System.out.println("---------------------------------------------------------");
		
	}
}
