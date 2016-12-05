package com.zlv.codewars.compiler3pass;

public class UnOp implements Ast {
	
	private String op;
	private int n;
	
	private static final String[] ops = new String[] {"arg", "imm"};
	public static enum OPS { arg, imm };
	private static final int[] argPositions = new int[] {0, 1};
	
	public UnOp(String op, int n) {
		this.op = op;
		this.n = n;
	}

	@Override
	public String op() {
		// TODO Auto-generated method stub
		return op;
	}
	
	public int n() {
		return n;
	}
	
	public String toString() {
		return String.format("new UnOp(\"%s\", %d)", op, n);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
