package depsolver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import depsolver.Package;

public class Main {

	static List<Package> repo;
	static List<String> initial;
	static List<String> toInstall;
	static Repository realRepo;

	public static void main(String[] args) throws IOException {

		String repoPath = null;
		String initialPath =  null;
		String constraintsPath = null;

		if(args.length == 0) {
			repoPath = "tests/example-0/repository.json";
			initialPath = "tests/example-0/initial.json";
			constraintsPath = "tests/example-0/constraints.json";
		} else {
			repoPath = args[0];
			initialPath = args[1];
			constraintsPath = args[2];
		}

		TypeReference<List<String>> strListType = new TypeReference<List<String>>() {};
		TypeReference<List<Package>> repoType = new TypeReference<List<Package>>() {};
		// Repo
		repo = JSON.parseObject(readFile(repoPath), repoType);
		realRepo = new Repository(repo);
		// Initial state
		initial = JSON.parseObject(readFile(initialPath), strListType);
		// What I need to install
		toInstall = JSON.parseObject(readFile(constraintsPath), strListType);    

		realRepo.calculateDeps(repo);
		realRepo.calculateConflicts(repo);
		
		
		
	}

	static String readFile(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		StringBuilder sb = new StringBuilder();
		br.lines().forEach(line -> sb.append(line));
		return sb.toString();
	}
}
