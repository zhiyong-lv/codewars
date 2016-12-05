package com.zlv.codewars.compiler3pass;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zlv.codewars.compiler3pass.UnOp.OPS;

public class Compiler {
	private static final boolean debug = true;
	
	public List<String> compile(String prog) {
		return pass3(pass2(pass1(prog)));
	}

	/**
	 * Returns an un-optimized AST
	 */
	public Ast pass1(String prog) {
		Deque<String> tokens = tokenize(prog);
		Map<String, Integer> map = new HashMap<String, Integer>();
		getArgsList(map, tokens);
		return getAst(map, tokens);
	}

	/**
	 * Returns an AST with constant expressions reduced
	 */
	public Ast pass2(Ast ast) {
		if(ast instanceof BinOp) {
			Ast a = ((BinOp) ast).a();
			Ast b = ((BinOp) ast).b();
			
			if(a instanceof BinOp) {
				a = pass2(a);
			}
			if(b instanceof BinOp) {
				b = pass2(b);
			}
			
			if(a instanceof UnOp && ((UnOp)a).op().equals("imm") && b instanceof UnOp && ((UnOp)b).op().equals("imm")) {
				return new UnOp("imm", calc(ast.op(), ((UnOp)a), ((UnOp)b)));
			} else {
				return new BinOp(ast.op(), a, b);
			}
		} 
		
		return ast;
	}

	private int calc(String op, UnOp a, UnOp b) {
		// TODO Auto-generated method stub
		int x = a.n();
		int y = b.n();
		
		switch(op) {
		case "+": return x+y;
		case "-": return x-y;
		case "*": return x*y;
		case "/": return x/y;
		default: throw new RuntimeException("Unknow options");
		}
	}

	/**
	 * Returns assembly instructions
	 */
	public List<String> pass3(Ast ast) {
		List<String> rst = new LinkedList<>();
		List<String> aRst = null;
		List<String> bRst = null;
		
		switch(ast.op()) {
		case "imm": 
			rst.add("IM " + ((UnOp) ast).n()); break;
		case "arg": 
			rst.add("AR " + ((UnOp) ast).n()); break;
		case "+": 
		case "-":
		case "*":
		case "/":
			rst.addAll(pass3Cal(ast)); break;
		default: 
			throw new RuntimeException();
		}
		return rst;
	}
	
	private List<String> pass3Cal(Ast ast) {
		List<String> rst = new LinkedList<>();
		List<String> aRst = pass3(((BinOp) ast).a());
		List<String> bRst = pass3(((BinOp) ast).b());
		
		if(aRst.contains("SW")) {
			rst.addAll(aRst);
			rst.add("PU");
		}
		
		rst.addAll(bRst);
		rst.add("SW");
		
		if(aRst.contains("SW")) {
			rst.add("PO");
		} else {
			rst.addAll(aRst);
		}
		
		switch(ast.op()) {
		case "+":
			rst.add("AD"); break;
		case "-":
			rst.add("SU"); break;
		case "*":
			rst.add("MU"); break;
		case "/":
			rst.add("DI"); break;
		}
		
		
		
		return rst;
	}

	private static Deque<String> tokenize(String prog) {
		Deque<String> tokens = new LinkedList<>();
		Pattern pattern = Pattern.compile("[-+*/()\\[\\]]|[a-zA-Z]+|\\d+");
		Matcher m = pattern.matcher(prog);
		while (m.find()) {
			tokens.add(m.group());
		}
		tokens.add("$"); // end-of-stream
		return tokens;
	}
	
	private static void getArgsList(Map<String, Integer> argsMap, Deque<String> tokens) {
		String s = null;
		int position = 0;
		if((s = tokens.peek()) != null && s.equals("[")) {
			while(!(s = tokens.pop()).equals("]")){
				if(!s.equals("[")) {
					printf("Arg[%d] = %s\n", position, s);
					argsMap.put(s, position);
					position++;
				}
			}
		} else {
			print("args is error!");
		}
	}
	
	private static Ast getAst(Map<String, Integer> argsMap, Deque<String> tokens) {
		Deque<Ast> astsP1 = new LinkedList<>();
		Deque<Ast> astsP2 = new LinkedList<>();
		Deque<String> opsP1 = new LinkedList<>();
		Deque<String> opsP2 = new LinkedList<>();
		Ast tmp = null;
		String s = null;
		
		while(!(s = tokens.pop()).equals("$")) {
			if(Pattern.matches("[a-zA-Z]+", s)){
				print("arg = " + s);
				tmp = new UnOp("arg", argsMap.get(s));
			} else if (Pattern.matches("\\d+", s)){
				tmp = new UnOp("imm", Integer.parseInt(s));
			} else if (Pattern.matches("\\(", s)) {
				int emphasisEnd = 1;
				Deque<String> newTokens = new LinkedList<>();
				while(emphasisEnd > 0) {
					s = tokens.pop();
					if(s.equals("(")) emphasisEnd++;
					if(s.equals(")")) emphasisEnd--;
					newTokens.add(s);
				}
				newTokens.removeLast();
				newTokens.add("$");
				tmp = getAst(argsMap, newTokens);
			} else if (Pattern.matches("[*/]", s)) {
				astsP1.add(tmp);
				opsP1.add(s);
			} else if (Pattern.matches("[+-]", s)) {
				opsP2.add(s);
				if(!opsP1.isEmpty()) {
					// calculate all asts in p1 and add it to p2.
					astsP1.add(tmp);
					astsP2.add(getAstOnlySamePriority(astsP1, opsP1));
				} else {
					astsP2.add(tmp);
				}
			}
		}
		
		if(astsP1.size() > 0) {
			astsP1.add(tmp);
			tmp = null;
			// calculate all asts in p1 and add it to p2.
			astsP2.add(getAstOnlySamePriority(astsP1, opsP1));
		}
		
		if(astsP2.size() > 0) {
			if(null != tmp) astsP2.add(tmp);
			return getAstOnlySamePriority(astsP2, opsP2);
		}
		
		return null;
	}
	
	private static Ast getAstOnlySamePriority(Deque<Ast> asts, Deque<String> ops) {
		if(ops.size() + 1 != asts.size()) throw new RuntimeException();
		if(ops.size() == 0) {
			return asts.pop();
		} else if(ops.size() == 1) {
			return new BinOp(ops.pop(),asts.pop(),asts.pop());
		} else {
			String op = ops.removeLast();
			Ast ast = asts.removeLast();
			return new BinOp(op, getAstOnlySamePriority(asts,ops), ast);
		}
	}
	
	private static void print(String s) {
		if(debug) System.out.println(s);
	}
	
	private static void printf(String s, Object... args) {
		if(debug) System.out.printf(s, args);
	}
	
	public static void main(String[] args) {
		Compiler compiler = new Compiler();
		//print(new Compiler().pass1("[ x y z ] ( 2*3*x + 5*y - 3*z ) / (1 + 3 + 2*2)").toString());
		//Ast ast = new BinOp("/", new BinOp("-", new BinOp("+", new BinOp("*", new BinOp("*", new UnOp("imm", 2), new UnOp("imm", 3)), new UnOp("arg", 0)), new BinOp("*", new UnOp("imm", 5), new UnOp("arg", 1))), new BinOp("*", new UnOp("imm", 3), new UnOp("arg", 2))), new BinOp("+", new BinOp("+", new UnOp("imm", 1), new UnOp("imm", 3)), new BinOp("*", new UnOp("imm", 2), new UnOp("imm", 2))));
		print(compiler.pass3(compiler.pass2(compiler.pass1("[ x y z ] ( 2*3*x + 5*y - 3*z ) / (1 + 3 + 2*2)"))).toString());
	}
}
