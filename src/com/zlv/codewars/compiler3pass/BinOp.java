package com.zlv.codewars.compiler3pass;

public class BinOp implements Ast {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public Ast a, b;
	private String op;
	private static final String[] ops = new String[] {"+", "-", "*", "/"};
	
	public BinOp(String op, Ast a, Ast b) {
		/*if(Arrays.stream(ops).filter(s -> s.equals(op)).count() != 1) 
			throw new AstOpNotSupportException();*/
		this.op = op;
		this.a = a;
		this.b = b;
	}

	@Override
	public String op() {
		// TODO Auto-generated method stub
		return op;
	}
	
	public Ast a() {
		return a;
	}
	
	public Ast b() {
		return b;
	}
	
	public void setA(Ast a) {
		this.a = a;
	}
	
	public void setB(Ast b) {
		this.b = b;
	}
	
	public String toString() {
		return String.format("new BinOp(\"%s\", %s, %s)", op, a, b);
	}

}
