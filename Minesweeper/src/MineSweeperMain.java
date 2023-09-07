import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class MineSweeperMain {

	private static int xDim;
	private static int yDim;
	private static int numMines;

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_WHITE = "\u001B[37m";

	private static ArrayList<Pair> flags = new ArrayList<>();

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);

		System.out.println("Enter the x and y dimensions and the number of mines seperated by a space");
		System.out.print(ANSI_BLUE);
		String line = in.nextLine();
		System.out.print(ANSI_RESET);
		xDim = Integer.parseInt(line.split(" ")[0]);
		yDim = Integer.parseInt(line.split(" ")[1]);
		numMines = Integer.parseInt(line.split(" ")[2]);

		while (xDim * yDim < numMines) {
			System.out.println(ANSI_RED + "Invalid mine input please enter a valid mine amount" + ANSI_RESET);
			System.out.print(ANSI_BLUE);
			numMines = Integer.parseInt(in.nextLine());
			System.out.print(ANSI_RESET);
		}

		Board board = new Board(xDim, yDim, numMines);
		printBoard(board.getValues(), board.getDisplay(), false);
		System.out.print("===");
		for (int i = 0; i < xDim; i++)
			System.out.print("===");
		System.out.println();

		System.out.println("Enter starting coords seperated by a space ex: x y");
		System.out.print(ANSI_BLUE);
		line = in.nextLine();
		System.out.print(ANSI_RESET);
		int sX = Integer.parseInt(line.split(" ")[0]);
		int sY = Integer.parseInt(line.split(" ")[1]);

		while (sX < 0 || sX > xDim - 1) {
			System.out.println(ANSI_RED + "Invalid x coord please enter a valid x coord" + ANSI_RESET);
			sX = Integer.parseInt(in.nextLine());
		}
		while (sY < 0 || sY > xDim - 1) {
			System.out.println(ANSI_RED + "Invalid y coord please enter a valid y coord" + ANSI_RESET);
			sY = Integer.parseInt(in.nextLine());
		}

		board.generate(sX, sY);

		board.display(sX, sY);

		boolean wins = true;
		printBoard(board.getValues(), board.getDisplay(), false);
		System.out.print("===");
		for (int i = 0; i < xDim; i++)
			System.out.print("===");
		System.out.println();

		while (!board.hasWon()) {
			System.out.println("Enter coords to guess seperated by a space ex: x y");
			System.out.println("\t or start with an f followed by a space to mark that tile ex: f x y");
			System.out.print(ANSI_BLUE);
			line = in.nextLine().toLowerCase();
			System.out.print(ANSI_RESET);
			int cX = -1;
			int cY = -1;
			if (line.charAt(0) != 'f') {
				cX = Integer.parseInt(line.split(" ")[0]);
				cY = Integer.parseInt(line.split(" ")[1]);
				while (flagsContains(cX, cY)) {
					System.out.println(ANSI_RED + "Cannot display a flagged tile. Enter new x and y" + ANSI_RESET);
					System.out.print(ANSI_BLUE);
					line = in.nextLine().toLowerCase();
					System.out.print(ANSI_RESET);
					cX = Integer.parseInt(line.split(" ")[0]);
					cY = Integer.parseInt(line.split(" ")[1]);
				}

				boolean isNotValid = true;
				while (isNotValid) {
					try {
						isNotValid = false;
						cX = Integer.parseInt(line.split(" ")[0]);
						cY = Integer.parseInt(line.split(" ")[1]);
						if (cX < 0 || cX >= xDim || cY < 0 || cY >= yDim)
							throw new IllegalArgumentException();
					} catch (Exception E) {
						System.out.println("Invalid input");
						System.out.println("Enter coords to display seperated by a space ex: x y");
						System.out.print(ANSI_BLUE);
						line = in.nextLine().toLowerCase();
						System.out.print(ANSI_RESET);
						isNotValid = true;
					}
				}

				if (board.getValues()[cY][cX] == -1) {
					wins = false;
					break;
				}

				board.display(cX, cY);
			} else {
				boolean isNotValid = true;
				while (isNotValid) {
					try {
						isNotValid = false;
						cX = Integer.parseInt(line.split(" ")[1]);
						cY = Integer.parseInt(line.split(" ")[2]);
						if (cX < 0 || cX >= xDim || cY < 0 || cY >= yDim)
							throw new IllegalArgumentException();
					} catch (Exception E) {
						System.out.println("Invalid input");
						System.out.println("Enter coords to flag seperated by a space ex: f x y");
						System.out.print(ANSI_BLUE);
						line = in.nextLine().toLowerCase();
						System.out.print(ANSI_RESET);
						isNotValid = true;
					}
				}
				if (flagsIndexOf(cX, cY) != -1)
					flags.remove(flagsIndexOf(cX, cY));
				else
					flags.add(new Pair(cX, cY));
			}

			printBoard(board.getValues(), board.getDisplay(), false);
			System.out.print("===");
			for (int i = 0; i < xDim; i++)
				System.out.print("===");
			System.out.println();
		}

		for (int i = 0; i < 50; i++)
			System.out.println();

		printBoard(board.getValues(), board.getDisplay(), true);

		if (wins)
			System.out.println("You completed the puzzle!!!");
		else
			System.out.println("You hit a bomb! YOU LOSE!!!");

		in.close();
	}

	public static void printBoard(int[][] tBoard, boolean[][] dBoard, boolean use) {
		System.out.print("   ");
		for (int i = 0; i < xDim; i++)
			System.out.printf("%2s ", i);
		System.out.println();
		System.out.print("===");
		for (int i = 0; i < xDim; i++)
			System.out.print("===");
		System.out.println();
		for (int o = 0; o < tBoard.length; o++) {
			System.out.printf("%-2s|", o);
			for (int i = 0; i < tBoard[o].length; i++) {
				if (dBoard[o][i] || use) {
					if (tBoard[o][i] == -5)
						System.out.printf(ANSI_WHITE + " 0 " + ANSI_RESET);
					else if (tBoard[o][i] == -1)
						System.out.printf(ANSI_RED + " Bo" + ANSI_RESET);
					else {
						if (tBoard[o][i] != 0)
							System.out.printf(ANSI_GREEN + "%2d " + ANSI_RESET, tBoard[o][i]);
						else
							System.out.printf(ANSI_WHITE + "%2d " + ANSI_RESET, tBoard[o][i]);
					}

				} else {
					if (flagsContains(i, o))
						System.out.printf(ANSI_RED + " F " + ANSI_RESET);
					else
						System.out.print(ANSI_BLACK + " X " + ANSI_RESET);
				}
			}
			System.out.println();
		}
	}

	public static boolean flagsContains(int x, int y) {
		for (Pair k : flags)
			if (k.a == x && k.b == y)
				return true;
		return false;
	}

	public static int flagsIndexOf(int x, int y) {
		int size = flags.size();
		for (int i = 0; i < size; i++) {
			Pair k = flags.get(i);
			if (k.a == x && k.b == y)
				return i;
		}
		return -1;
	}
}

class Board {
	private static int xDim;
	private static int yDim;
	private static int numMines;
	private static int[][] values;
	private static boolean[][] display;

	public Board(int xDim, int yDim, int numMines) {
		this.xDim = xDim;
		this.yDim = yDim;
		this.numMines = numMines;
		values = new int[yDim][xDim];
		display = new boolean[yDim][xDim];
	}

	public static int getxDim() {
		return xDim;
	}

	public static int getyDim() {
		return yDim;
	}

	public static int getNumMines() {
		return numMines;
	}

	public int[][] getValues() {
		return values;
	}

	public boolean[][] getDisplay() {
		return display;
	}

	public boolean isSolved() {
		for (int[] arr : values)
			for (int x : arr)
				if (x >= 0)
					return true;
		return false;
	}

	public void generate(int x, int y) {
		Random r = new Random();
		for (int i = 0; i < numMines; i++) {
			int xM = r.nextInt(xDim);
			int yM = r.nextInt(yDim);

			while ((values[yM][xM] == -1) || invalidMine(x, y, xM, yM)) {
				xM = r.nextInt(xDim);
				yM = r.nextInt(yDim);
			}

			values[yM][xM] = -1;
		}

		for (int yC = 0; yC < yDim; yC++) {
			for (int xC = 0; xC < xDim; xC++) {
				if (values[yC][xC] == -1)
					continue;
				int minesA = countAround(xC, yC);
				values[yC][xC] = minesA;
			}
		}
	}

	public boolean invalidMine(int x, int y, int mX, int mY) {
		return (mX >= x - 1 && mX <= x + 1) && (mY >= y - 1 && mY <= y + 1);
	}

	public int countAround(int x, int y) {
		int count = 0;
		for (int i = -1; i <= 1; i++) {
			if (x + i < 0 || x + i > xDim - 1)
				continue;
			for (int q = -1; q <= 1; q++) {
				if (y + q < 0 || y + q > yDim - 1)
					continue;
				if (values[y + q][x + i] == -1)
					count++;
			}
		}
		return count;
	}

	public void display(int x, int y) {
		displayPoint(x, y);
		displayAround(x, y);
	}

	public void displayPoint(int x, int y) {
		display[y][x] = true;
		if (values[y][x] == 0)
			values[y][x] = -5;
	}

	public void displayAround(int x, int y) {
		boolean isZeroAndDisplayed = values[y][x] == -5 && display[y][x];

		for (int i = -1; i <= 1; i++) {
			int xi = x + i;
			if (xi < 0 || xi > xDim - 1)
				continue;
			for (int q = -1; q <= 1; q++) {
				int yq = y + q;
				if (i == 0 && q == 0)
					continue;
				if (yq < 0 || yq > yDim - 1)
					continue;
				if (isZeroAndDisplayed)
					display[yq][xi] = true;
				// checking future spots
				if (values[yq][xi] == 0) {
					values[yq][xi] = -5;
					displayAround(xi, yq);
				}
			}
		}
	}

	public boolean hasWon() {
		for (int i = 0; i < xDim; i++)
			for (int q = 0; q < yDim; q++)
				if (!display[q][i] && values[q][i] != -1)
					return false;
		return true;
	}
}

class Pair {
	Integer a;
	Integer b;

	Pair(Integer a, Integer b) {
		this.a = a;
		this.b = b;
	}
}