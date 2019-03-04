package depsolver;

public class Constraint {
	
	public Op constraint;
	public String name;

	Constraint(Op constraint, String name){
		this.constraint = constraint;
		this.name = name;
	}
	
	@Override
	public String toString(){
		if(constraint == Op.POS) {
			return "+" + name;
		} else {
			return "-" + name;
		}
	}
}

enum Op {
	POS,
	NEG
}
