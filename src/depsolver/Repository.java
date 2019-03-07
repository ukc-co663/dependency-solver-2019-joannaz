package depsolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import depsolver.Package;

public class Repository {

	public static HashMap<String, HashMap<String, Package>> repo = new HashMap<String, HashMap<String, Package>>();

	public Repository(List<Package> packages)
	{
		for(Package p : packages) {
			if(repo.get(p.getName())== null) {
				HashMap<String, Package> newHashMap = new HashMap<String, Package>();
				newHashMap.put(p.getVersion(), p);
				repo.put(p.getName(), newHashMap);
			} else {
				HashMap<String, Package> name = repo.get(p.getName());
				name.put(p.getVersion(), p);
			}
		}
	}

	/**
	 * Calculate the dependencies
	 * @param JSON repo
	 */
	public void calculateDeps(List<Package> packages) {
		for(Package p : packages) {
			// The dependencies of the package
			List<List<String>> dependencies = p.getDepends();

			// The x AND y
			List<List<String>> ands = new ArrayList<>();
			for(List<String> and : dependencies) {

				// the a or b
				ArrayList<String> orsList = new ArrayList<String>();
				for(String or : and) {

					String[] ors = or.split("[<>=]+");
					//ors[0] = B
					//ors[1] = 3.1
					/****************************************
					 *  Smaller or Equal to
					 ****************************************/
					if(or.contains("<=")) {
						// A <= 3 smaller or equal to
						HashMap<String, Package> versions = repo.get(ors[0]);

						for(String version : versions.keySet()) {
							if(Util.compareVersion(version, ors[1]) <= 0) {
								orsList.add(ors[0] + "=" + version);
							}
						}
					} 

					/****************************************
					 *  Bigger or Equal to
					 ****************************************/
					else if (or.contains(">=")) {
						HashMap<String, Package> versions = repo.get(ors[0]);

						for(String version : versions.keySet()) {
							if(Util.compareVersion(version, ors[1]) >= 0) {
								orsList.add(ors[0] + "=" + version);
							}
						}
					} 

					/****************************************
					 *  Smaller Than
					 ****************************************/
					else if(or.contains("<")) {
						HashMap<String, Package> versions = repo.get(ors[0]);

						for(String version : versions.keySet()) {
							if(Util.compareVersion(version, ors[1]) < 0) {
								orsList.add(ors[0] + "=" + version);
							}
						}
					} 
					/****************************************
					 *  Bigger Than
					 ****************************************/
					else if(or.contains(">")) {
						// Bigger than ors[0]
						HashMap<String, Package> versions = repo.get(ors[0]);

						for(String version : versions.keySet()) {
							if(Util.compareVersion(version, ors[1]) > 0) {
								orsList.add(ors[0] + "=" + version);
							}
						}
					}
					/****************************************
					 *  Bigger Than
					 ****************************************/
					else if(or.contains("=")) {
						HashMap<String, Package> versions = repo.get(ors[0]);

						for(String version : versions.keySet()) {
							if(Util.compareVersion(version, ors[1]) == 0) {
								orsList.add(ors[0] + "=" + version);
							}
						}
					}

					else {						
						HashMap<String, Package> versions = repo.get(ors[0]);

						for(String version : versions.keySet()) {
							orsList.add(ors[0] + "=" + version);
						}

						// get any
					}
				}
				ands.add(orsList);

				repo.get(p.getName()).get(p.getVersion()).setDepends(ands);
			}
		}
	}


	public void calculateConflicts(List<Package> packages) {
		for(Package p : packages) {
			// The dependencies of the package
			List<String> conflicts = p.getConflicts();
			ArrayList<String> conflictList = new ArrayList<String>();
			for(String conflict : conflicts) {

				String[] con = conflict.split("[<>=]+");
				//con[0] = B
				//con[1] = 3.1
				/****************************************
				 *  Smaller or Equal to
				 ****************************************/
				if(conflict.contains("<=")) {
					// A <= 3 smaller or equal to
					HashMap<String, Package> versions = repo.get(con[0]);

					for(String version : versions.keySet()) {
						if(Util.compareVersion(version, con[1]) <= 0) {
							conflictList.add(con[0] + "=" + version);
						}
					}

				} 

				/****************************************
				 *  Bigger or Equal to
				 ****************************************/
				else if (conflict.contains(">=")) {
					HashMap<String, Package> versions = repo.get(con[0]);

					for(String version : versions.keySet()) {
						if(Util.compareVersion(version, con[1]) >= 0) {
							conflictList.add(con[0] + "=" + version);
						}
					}
				} 

				/****************************************
				 *  Smaller Than
				 ****************************************/
				else if(conflict.contains("<")) {
					HashMap<String, Package> versions = repo.get(con[0]);
					for(String version : versions.keySet()) {
						if(Util.compareVersion(version, con[1]) < 0) {
							conflictList.add(con[0] + "=" + version);
						}
					}
				} 
				/****************************************
				 *  Bigger Than
				 ****************************************/
				else if(conflict.contains(">")) {
					// Bigger than ors[0]
					HashMap<String, Package> versions = repo.get(con[0]);

					for(String version : versions.keySet()) {
						if(Util.compareVersion(version, con[1]) > 0) {
							conflictList.add(con[0] + "=" + version);
						}
					}
				}
				/****************************************
				 *  Bigger Than
				 ****************************************/
				else if(conflict.contains("=")) {
					HashMap<String, Package> versions = repo.get(con[0]);

					for(String version : versions.keySet()) {
						if(Util.compareVersion(version, con[1]) == 0) {
							conflictList.add(con[0] + "=" + version);
						}
					}
				}

				else {						
					HashMap<String, Package> versions = repo.get(con[0]);

					for(String version : versions.keySet()) {
						conflictList.add(con[0] + "=" + version);
					}

				}


			}
			repo.get(p.getName()).get(p.getVersion()).setConflicts(conflictList);
		}

	}

	public Package getSpecific(String id) {
		if(id.contains("+")) {
			id = id.replace("+", "");
		}
		if(id.contains("*")) {
			id = id.replace("*", "");
		}
		String[] x = id.split("=");
		return repo.get(x[0]).get(x[1]);
	}

	public ArrayList<Package> getPackages(String id){
		ArrayList<Package> returnPackages = new ArrayList<>();
		String[] v = id.split("[<>=]+");
		HashMap<String,Package> packages = repo.get(v[0]);
		if(packages != null) {
			if(id.contains("<=")) {
				for(String ver : packages.keySet()) {				
					if(Util.compareVersion(ver, v[1]) <= 0) {
						returnPackages.add(packages.get(ver));
					}

				}
			}
			else if(id.contains("<")) {
				for(String ver : packages.keySet()) {
					if(Util.compareVersion(ver, v[1]) < 0) {
						returnPackages.add(packages.get(ver));
					}
				}

			} 
			else if(id.contains(">")) {
				for(String ver : packages.keySet()) {
					if(Util.compareVersion(ver, v[1]) > 0) {
						returnPackages.add(packages.get(ver));
					}
				}
			}
			else if(id.contains(">=")) {
				for(String ver : packages.keySet()) {
					if(Util.compareVersion(ver, v[1]) >= 0) {
						returnPackages.add(packages.get(ver));
					}
				}
			} else {
				for(String ver : packages.keySet()) {
					returnPackages.add(packages.get(ver));
				}
			}

		}
		return returnPackages;
	}

}
