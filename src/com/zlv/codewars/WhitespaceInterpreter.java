package com.zlv.codewars;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class WhitespaceInterpreter {

	/*
	 * IMPs: 
	 * [space]: 			Stack Manipulation 
	 * [tab][space]: 		Arithmetic 
	 * [tab][tab]: 			Heap Access 
	 * [tab][line-feed]: 	Input/Output 
	 * [line-feed]: 		Flow Control
	 */
	private static final String IMP_STACK = "s";
	private static final String IMP_Arithmetic = "ts";
	private static final String IMP_HEAP = "tt";
	private static final String IMP_IO = "tl";
	private static final String IMP_FLOW_CONTROL = "lf";

	private static final String REG_NUM = "^[ts]+l$";
	private static final String REG_LABLE = "^[ts]*l$";
	
	private static final String REG_STACK_PUSH_NUM = "^"+IMP_STACK+"s"+"([ts]+l)$";
	private static final String REG_STACK_DUPLICATE_NTH_NUM = "^"+IMP_STACK+"ts"+"([ts]+l)$";
	private static final String REG_STACK_DISCARD_TOP_N_NUM = "^"+IMP_STACK+"tl"+"([ts]+l)$";
	private static final String REG_STACK_DUPLICATE_TOP_NUM = "^"+IMP_STACK+"ls$";
	private static final String REG_STACK_SWAP_TOP_TWO_NUM = "^"+IMP_STACK+"lt$";
	private static final String REG_STACK_DISCARD_TOP_NUM = "^"+IMP_STACK+"ll$";
	
	private static final String REG_ARITHMETIC_POP_2NUM_PUSH_SUM = "^"+IMP_Arithmetic+"ss$";
	private static final String REG_ARITHMETIC_POP_2NUM_PUSH_ABS = "^"+IMP_Arithmetic+"st$";
	private static final String REG_ARITHMETIC_POP_2NUM_PUSH_MUL = "^"+IMP_Arithmetic+"sl$";
	private static final String REG_ARITHMETIC_POP_2NUM_PUSH_FLOOR_DIV = "^"+IMP_Arithmetic+"ts$";
	private static final String REG_ARITHMETIC_POP_2NUM_PUSH_MOD = "^"+IMP_Arithmetic+"tt$";

	private static final String REG_HEAP_POP_2NUM_STORE_FIRST_AT_ADDR_SECOND = "^"+IMP_HEAP+"s$";
	private static final String REG_HEAP_POP_2NUM_STORE_FIRST_AT_ADDR_FIRST = "^"+IMP_HEAP+"t$";
	
	private static final String REG_IO_POP_CHAR_2_OUTPUT = "^"+IMP_IO+"ss$";
	private static final String REG_IO_POP_NUM_2_OUTPUT = "^"+IMP_IO+"st$";
	private static final String REG_IO_GET_CHAR_FROM_INPUT_STORE_ASCII = "^"+IMP_IO+"ts$";
	private static final String REG_IO_GET_NUM_FROM_INPUT_STORE_ASCII = "^"+IMP_IO+"tt$";
	
	private static final String REG_FLOW_CONTROL_MARK_FUNC = "^"+IMP_FLOW_CONTROL+"ss([ts]*l)$";
	private static final String REG_FLOW_CONTROL_CALL_FUNC = "^"+IMP_FLOW_CONTROL+"st([ts]*l)$";
	private static final String REG_FLOW_CONTROL_GO = "^"+IMP_FLOW_CONTROL+"sl([ts]*l)$";
	private static final String REG_FLOW_CONTROL_GO_ZERO = "^"+IMP_FLOW_CONTROL+"ts([ts]*l)$";
	private static final String REG_FLOW_CONTROL_GO_LESS_ZERO = "^"+IMP_FLOW_CONTROL+"tt([ts]*l)$";
	private static final String REG_FLOW_CONTROL_EXIT_FUNC = "^"+IMP_FLOW_CONTROL+"tl$";
	private static final String REG_FLOW_CONTROL_EXIT_ALL = "^"+IMP_FLOW_CONTROL+"ll$";
	
	
	/**
	 * <p>Just verify if the input code is a valid code. and return it.</p>
	 * 
	 * <p>
	 * The following are the requirements for numbers in Whitespace system:
	 * <li>Numbers begin with a [sign] symbol. The sign symbol is either [tab] -> negative, or [space] -> positive.
	 * <li>Numbers end with a [terminal] symbol: [line-feed].
	 * <li>Between the sign symbol and the terminal symbol are binary digits [space] -> binary-0, or [tab] -> binary-1.
	 * <li>A number expression [sign][terminal] will be treated as zero.
	 * <li>The expression of just [terminal] should throw an error. (The Haskell implementation is inconsistent about this.)
	 * </p>
	 * @param code <b>Only the valid number format is allowed</b>
	 * @return will return an number in radix of 10.
	 * @throws RuntimeException when the input code is not a valid number format.
	 */
	private static int parsingNum(String code) {
		if (!code.matches(REG_NUM))
			throw new RuntimeException("Parsing Number Error: the input code is " + code);

		int sign = ((code.charAt(0) == 't') ? -1 : 1);
		String num = code.substring(1, code.length() - 1)
				.replaceAll("t", "1")
				.replaceAll("s", "0");

		if (num.length() == 0)
			return 0;
		else
			return sign * Integer.parseInt(num, 2);

	}
	
	/**
	 * <p>Just verify if the input code is a valid label. and return it.</p>
	 * 
	 * <p>
	 * The following are the requirements for labels in Whitespace system:
	 * <li>Labels begin with any number of [tab] and [space] characters.
	 * <li>Labels end with a terminal symbol: [line-feed].
	 * <li>Unlike with numbers, the expression of just [terminal] is valid.
	 * <li>Labels must be unique.
	 * <li>A label may be declared either before or after a command that refers to it.
	 * </p>
	 * @param code <b>Only the valid Label format is allowed</b>
	 * @return will return an empty string if code's length is less than 2. for example "sl" or "l".
	 * @throws RuntimeException when the input code is not a valid label format.
	 */
	private static String parsingLabel(String code) {
		if (!code.matches(REG_LABLE))
			throw new RuntimeException("Parsing Lable Error: the input code is " + code);
		if (code.length() <= 2)
			return "";
		else
			return code.substring(1, code.length() - 1);
	}
	
	/**
	 * <p>Read a character from input stream and return it.</p>
	 * 
	 * <p>
	 * The following are the requirements for Input in Whitespace system:
	 * <li>Reading a character involves simply taking a character from the input stream.
	 * <li>The Java implementations will use an InputStream instance for input. For InputStream use readLine if the program requests a number and read if the program expects a character.
	 * <li>An error should be thrown if the input ends before parsing is complete. 
	 * </p>
	 * @param is <b>the input stream</b>
	 * @return return a char.
	 * @throws IOException if an I/O error occurs when using is.read().
	 */
	private static char paringInputChars(InputStream is) throws IOException {
		return (char) is.read();
	}
	
	/**
	 * <p>Read a number from input stream and return it.</p>
	 * 
	 * <p>
	 * The following are the requirements for Input in Whitespace system:
	 * <li>Reading an integer involves parsing a decimal or hexadecimal number from the current position of the input stream, up to and terminated by a line-feed character.
	 * <li>The Java implementations will use an InputStream instance for input. For InputStream use readLine if the program requests a number and read if the program expects a character.
	 * <li>An error should be thrown if the input ends before parsing is complete. 
	 * </p>
	 * @param is <b>the input stream</b>
	 * @return return a integer.
	 * @throws IOException if an I/O error occurs when using is.read().
	 * NumberFormatException if the input string doesn't contain a parsable integer.
	 */
	private static int paringInputNum(InputStream is) throws IOException {
		return Integer.parseInt(new BufferedReader(new InputStreamReader(is)).readLine());
	}
	
	/**
	 * <p>Write a number to output stream.</p>
	 * 
	 * <p>
	 * The following are the requirements for Output in Whitespace system:
	 * <li>For a number, append the output string with the number's string value.
	 * <li>The Java implementations will support an optional OutputStream for output. If an OutputStream is provided, it should be flushed before and after code execution and filled as code is executed. The output string should be returned in any case.
	 * </p>
	 * @param is <b>the output stream</b>
	 * @return 
	 * @throws IOException if an I/O error occurs when using os.flush().
	 * NumberFormatException if the input string doesn't contain a parsable integer.
	 */
	private static void output(OutputStream os, int num) {
		PrintWriter pw = new PrintWriter(os);
		pw.flush();
		pw.append(""+num).flush();
	}
	
	/**
	 * <p>Write a character to output stream.</p>
	 * 
	 * <p>
	 * The following are the requirements for Output in Whitespace system:
	 * <li>For a character, simply append the output string with the character.
	 * <li>The Java implementations will support an optional OutputStream for output. If an OutputStream is provided, it should be flushed before and after code execution and filled as code is executed. The output string should be returned in any case.
	 * </p>
	 * @param is <b>the output stream</b>
	 * @return 
	 * @throws IOException if an I/O error occurs when using os.flush().
	 * NumberFormatException if the input string doesn't contain a parsable integer.
	 */
	private static void output(OutputStream os, char c) {
		PrintWriter pw = new PrintWriter(os);
		pw.flush();
		pw.append(""+c).flush();
	}
	
	private static void stack(String code, Stack<Integer> stack) {
		Matcher m = null;
		
		do {
			m = Pattern.compile(REG_STACK_PUSH_NUM).matcher(code);
			if(m.find()){
				stack.push(parsingNum(m.group(1)));
				break;
			}
			
			m = Pattern.compile(REG_STACK_DUPLICATE_NTH_NUM).matcher(code);
			if (m.find()) {
				stack.push(stack.get(stack.size() - parsingNum(m.group(1))));
				break;
			}
			
			m = Pattern.compile(REG_STACK_DISCARD_TOP_N_NUM).matcher(code);
			if (m.find()) {
				int index = parsingNum(m.group(1));
				if (index < 0 || index >= stack.size()){
					stack.clear();
				} else {
					while(index-->0) stack.pop();
				}
				break;
			}
			
			m = Pattern.compile(REG_STACK_DUPLICATE_TOP_NUM).matcher(code);
			if (m.find()) {
				stack.push(stack.get(stack.size()-1));
				break;
			}
			
			m = Pattern.compile(REG_STACK_SWAP_TOP_TWO_NUM).matcher(code);
			if (m.find()) {
				int t = stack.get(stack.size()-1);
				stack.set(stack.size()-1, stack.get(stack.size()-2));
				stack.set(stack.size()-2, t);
				break;
			}
			
			m = Pattern.compile(REG_STACK_DISCARD_TOP_NUM).matcher(code);
			if (m.find()) {
				stack.pop();
				break;
			}
			
			throw new RuntimeException("STACK: unclean termination");
		} while(false);
	}
	
	private static void arithmetic(String code, Stack<Integer> stack) {
		Matcher m = null;
		
		do {
			m = Pattern.compile(REG_ARITHMETIC_POP_2NUM_PUSH_SUM).matcher(code);
			if(m.find()){
				int a = stack.pop();
				int b = stack.pop();
				stack.push(a+b);
				break;
			}
			
			m = Pattern.compile(REG_ARITHMETIC_POP_2NUM_PUSH_ABS).matcher(code);
			if (m.find()) {
				int a = stack.pop();
				int b = stack.pop();
				stack.push(b-a);
				break;
			}
			
			m = Pattern.compile(REG_ARITHMETIC_POP_2NUM_PUSH_MUL).matcher(code);
			if (m.find()) {
				int a = stack.pop();
				int b = stack.pop();
				stack.push(b*a);
				break;
			}
			
			m = Pattern.compile(REG_ARITHMETIC_POP_2NUM_PUSH_FLOOR_DIV).matcher(code);
			if (m.find()) {
				int a = stack.pop();
				int b = stack.pop();
				stack.push(Math.floorDiv(b, a));
				break;
			}
			
			m = Pattern.compile(REG_ARITHMETIC_POP_2NUM_PUSH_MOD).matcher(code);
			if (m.find()) {
				int a = stack.pop();
				int b = stack.pop();
				stack.push(Math.floorMod(b, a));
				break;
			}
			
			throw new RuntimeException("ARITHMETIC: unclean termination");
		} while(false);
	}
	
	private static void heap(String code, Stack<Integer> stack, Map<Integer, Integer> heap) {
		Matcher m = null;
		
		do {
			m = Pattern.compile(REG_HEAP_POP_2NUM_STORE_FIRST_AT_ADDR_SECOND).matcher(code);
			if(m.find()){
				int a = stack.pop();
				int b = stack.pop();
				heap.put(b, a);
				break;
			}
			
			m = Pattern.compile(REG_HEAP_POP_2NUM_STORE_FIRST_AT_ADDR_FIRST).matcher(code);
			if (m.find()) {
				int a = stack.pop();
				heap.put(a, a);
				break;
			}
			
			throw new RuntimeException("ARITHMETIC: unclean termination");
		} while(false);
	}
	
	private static void io(String code, Stack<Integer> stack, Map<Integer, Integer> heap, InputStream in, OutputStream out) throws IOException {
		Matcher m = null;
		
		do {
			m = Pattern.compile(REG_IO_POP_CHAR_2_OUTPUT).matcher(code);
			if(m.find()){
				int a = stack.pop();
				output(out, (char)a);
				break;
			}
			
			m = Pattern.compile(REG_IO_POP_NUM_2_OUTPUT).matcher(code);
			if (m.find()) {
				output(out,stack.pop());
				break;
			}
			
			m = Pattern.compile(REG_IO_GET_CHAR_FROM_INPUT_STORE_ASCII).matcher(code);
			if(m.find()){
				char a = paringInputChars(in);
				int b = stack.pop();
				heap.put(b, (int)a);
				break;
			}
			
			m = Pattern.compile(REG_IO_GET_NUM_FROM_INPUT_STORE_ASCII).matcher(code);
			if (m.find()) {
				int a = paringInputNum(in);
				int b = stack.pop();
				heap.put(b, a);
				break;
			}
			
			throw new RuntimeException("ARITHMETIC: unclean termination");
		} while(false);
	}

	// transforms space characters to ['s','t','n'] chars;
	public static String unbleach(String code) {
		return code != null ? code.replace(' ', 's').replace('\t', 't')
				.replace('\n', 'n') : null;
	}

	// solution
	public static String execute(String code, InputStream input) {
		String output = "";
		Stack<Integer> stack = new Stack<>();
		Map<Integer, Integer> heap = new HashMap<>();
		// ... you code ...
		
		return output;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//testParsingNum();
		//testparsingLabel();
		//testParingInputNum();
		/*testParingInputChars();
		testParingInputChars();
		testParingInputChars();*/
		//testOutput();
		//testStack();
		//testArithmetic();
		//testHeap();
		testIO();
	}

	private static void testParsingNum() {
		try {
			System.out.println(parsingNum("l"));
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		try {
			System.out.println(parsingNum("lssl"));
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		try {
			System.out.println(parsingNum("lss"));
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		try {
			System.out.println(parsingNum("stlss"));
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		System.out.println(parsingNum("sl"));
		System.out.println(parsingNum("tl"));
		System.out.println(parsingNum("tssssl"));
		System.out.println(parsingNum("sssssl"));
		System.out.println(parsingNum("tttttl"));
		System.out.println(parsingNum("ttstsl"));
		System.out.println(parsingNum("ststsl"));
	}
	
	private static void testparsingLabel() {
		try {
			System.out.println(parsingLabel("l"));
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		try {
			System.out.println(parsingLabel("lssl"));
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		try {
			System.out.println(parsingLabel("lss"));
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		try {
			System.out.println(parsingLabel("stlss"));
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		System.out.println(parsingLabel("sl"));
		System.out.println(parsingLabel("tl"));
		System.out.println(parsingLabel("tssssl"));
		System.out.println(parsingLabel("sssssl"));
		System.out.println(parsingLabel("tttttl"));
		System.out.println(parsingLabel("ttstsl"));
		System.out.println(parsingLabel("ststsl"));
		System.out.println(parsingLabel("tsssstttttttttttttttttl").equals(parsingLabel("ssssstttttttttttttttttl")));
	}
	
	private static void testParingInputNum() {
		try {
			System.out.println(paringInputNum(System.in));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void testParingInputChars() {
		try {
			System.out.println(paringInputChars(System.in));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void testOutput() {
		output(System.out,1234567);
		output(System.out,'a');
	}
	
	private static void testStack() {
		Stack<Integer> stack = new Stack<Integer>();
		System.out.println("push n to stack. stack.capacity()="+stack.size());
		stack("ssssssl",stack);
		stack("ssttttl",stack);
		stack("sststsl",stack);
		stack("ssststl",stack);
		stack.stream().forEach(i -> System.out.print(i+","));

		System.out.println("\nduplicate the nth value from the top of the stack");
		stack("stssttl",stack);
		stack.stream().forEach(i -> System.out.print(i+","));

		System.out.println("\ndiscard the top n values");
		stack("stlsttl",stack);
		stack.stream().forEach(i -> System.out.print(i+","));

		System.out.println("\nduplicate the top value on the stack");
		stack("sls",stack);
		stack.stream().forEach(i -> System.out.print(i+","));

		System.out.println("\nSwap the top two value on the stack.");
		stack("ssststl",stack);
		stack("slt",stack);
		stack.stream().forEach(i -> System.out.print(i+","));

		System.out.println("\nDiscard the top value on the stack.");
		stack("sll",stack);
		stack.stream().forEach(i -> System.out.print(i+","));
	}
	
	private static void testArithmetic() {
		Stack<Integer> stack = new Stack<Integer>();
		System.out.println("push n to stack. stack.capacity()="+stack.size());
		stack("ssssssl",stack);
		stack("ssttttl",stack);
		stack("sststsl",stack);
		stack("ssststl",stack);
		stack("ssttttl",stack);
		stack("sststsl",stack);
		stack("ssststl",stack);
		stack("ssttttl",stack);
		stack("sststsl",stack);
		stack("ssststl",stack);
		stack.stream().forEach(i -> System.out.print(i+","));

		System.out.println("\nPop a and b, then push b+a.");
		arithmetic("tsss",stack);
		stack.stream().forEach(i -> System.out.print(i+","));

		System.out.println("\nPop a and b, then push b-a.");
		arithmetic("tsst",stack);
		stack.stream().forEach(i -> System.out.print(i+","));

		System.out.println("\nPop a and b, then push b*a.");
		arithmetic("tssl",stack);
		stack.stream().forEach(i -> System.out.print(i+","));

		System.out.println("\nPop a and b, then push b/a*. ");
		stack("ssstttl",stack);
		stack("ssssttl",stack);
		arithmetic("tsts",stack);
		stack.stream().forEach(i -> System.out.print(i+","));

		System.out.println("\nPop a and b, then push b%a*. If a is zero, throw an error.");
		stack("ssstttl",stack);
		arithmetic("tstt",stack);
		stack.stream().forEach(i -> System.out.print(i+","));

		System.out.println("\nPop a and b, then push b/a*. throw an error.");
		stack("ssssssl",stack);
		arithmetic("tsts",stack);
		stack.stream().forEach(i -> System.out.print(i+","));
	}

	private static void testHeap() {
		Stack<Integer> stack = new Stack<Integer>();
		Map<Integer, Integer> heap = new HashMap<Integer, Integer>();
		System.out.println("push n to stack");
		stack("ssssssl",stack);
		stack("ssttttl",stack);
		stack("sststsl",stack);
		stack("ssststl",stack);
		stack("ssttttl",stack);
		stack("sststsl",stack);
		stack("ssststl",stack);
		stack("ssttttl",stack);
		stack("sststsl",stack);
		stack("ssststl",stack);
		stack.stream().forEach(i -> System.out.print(i+","));

		System.out.println("\nPop a and b, then store a at heap address b.");
		heap("tts",stack,heap);
		stack.stream().forEach(i -> System.out.print(i+","));
		System.out.println("");
		heap.entrySet().stream().forEach(set -> System.out.print(String.format("(%d)->(%d), ", set.getKey(),set.getValue())));
		
		System.out.println("\nPop a and then push the value at heap address a onto the stack");
		heap("ttt",stack,heap);
		stack.stream().forEach(i -> System.out.print(i+","));
		System.out.println("");
		heap.entrySet().stream().forEach(set -> System.out.print(String.format("(%d)->(%d), ", set.getKey(),set.getValue())));
	}

	private static void testIO() {
		Stack<Integer> stack = new Stack<Integer>();
		Map<Integer, Integer> heap = new HashMap<Integer, Integer>();
		System.out.println("push n to stack");
		stack("sssttsssttl",stack);
		stack.stream().forEach(i -> System.out.print(i+","));

		System.out.println("\nPop a value off the stack and output it as a character.");
		try {
			io("tlss",stack,heap,System.in,System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stack.stream().forEach(i -> System.out.print(i+","));
		System.out.println("");
		heap.entrySet().stream().forEach(set -> System.out.print(String.format("(%d)->(%d), ", set.getKey(),set.getValue())));
		
		System.out.println("\nPop a value off the stack and output it as a number.");
		stack("sssttsssttl",stack);
		try {
			io("tlst",stack,heap,System.in,System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stack.stream().forEach(i -> System.out.print(i+","));
		System.out.println("");
		heap.entrySet().stream().forEach(set -> System.out.print(String.format("(%d)->(%d), ", set.getKey(),set.getValue())));

		System.out.println("\nRead a character from input, a, Pop a value off the stack, b, then store the ASCII value of a at heap address b.");
		stack("sssttsssttl",stack);
		try {
			io("tlts",stack,heap,System.in,System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stack.stream().forEach(i -> System.out.print(i+","));
		System.out.println("");
		heap.entrySet().stream().forEach(set -> System.out.print(String.format("(%d)->(%d), ", set.getKey(),set.getValue())));
		
		System.out.println("\nRead a number from input, a, Pop a value off the stack, b, then store a at heap address b.");
		stack("sssttsssttl",stack);
		try {
			io("tltt",stack,heap,System.in,System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stack.stream().forEach(i -> System.out.print(i+","));
		System.out.println("");
		heap.entrySet().stream().forEach(set -> System.out.print(String.format("(%d)->(%d), ", set.getKey(),set.getValue())));
		
	}

}
