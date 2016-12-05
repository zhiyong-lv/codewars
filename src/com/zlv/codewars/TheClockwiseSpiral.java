package com.zlv.codewars;

public class TheClockwiseSpiral {
	private static int[] turnRight(int[] direction) {
		if (direction[0] == 0 && direction[1] == 1)
			return new int[] { 1, 0 };
		else if (direction[0] == 1 && direction[1] == 0)
			return new int[] { 0, -1 };
		else if (direction[0] == 0 && direction[1] == -1)
			return new int[] { -1, 0 };
		else if (direction[0] == -1 && direction[1] == 0)
			return new int[] { 0, 1 };
		else
			throw new RuntimeException();
	}

	private static boolean next(int[][] result, int[] direction, int[] position) {
		if (position[0] + direction[0] < 0 
				|| position[0] + direction[0] >= result.length
				|| position[1] + direction[1] < 0 
				|| position[1] + direction[1] >= result[0].length
				|| result[position[0] + direction[0]][position[1] + direction[1]] != 0)
			return false;
		else {
			position[0] += direction[0];
			position[1] += direction[1];
			return true;
		}
	}

	public static int[][] createSpiral(int N) {
		// your code here
		if (N < 1) {
			return new int[][] {};
		}

		int[][] result = new int[N][N];
		int[] direction = new int[] { 0, 1 };
		int[] position = new int[] { 0, 0 };
		int x = 0, y = 0, val = 1;

		do {
			do {
				result[position[0]][position[1]] = val++;
				if (!next(result, direction, position))
					break;
			} while (true);
			direction = turnRight(direction);
			if (!next(result, direction, position))
				break;
		} while (true);

		return result;
	}

	public static void main(String[] args) {
		int[][] spiral = createSpiral(3);

		for (int row = 0; row < spiral.length; row++) {
			for (int col = 0; col < spiral[0].length; col++) {
				System.out.print(spiral[row][col] + "\t");
			}
			System.out.println("");
		}
	}
}
