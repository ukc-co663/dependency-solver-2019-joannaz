package depsolver;

import java.util.ArrayList;

public class Util {

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

	public static int solve() {
		
		
		return 1;
	}
	
}
