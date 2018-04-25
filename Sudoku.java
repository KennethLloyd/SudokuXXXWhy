import java.util.*;
import java.io.*;
import java.lang.Math.*;

public class Sudoku {
	private static ArrayList<Integer> subgridSize = new ArrayList<Integer>();
	private static ArrayList<ArrayList<ArrayList<Integer>>> puzzles = new ArrayList<ArrayList<ArrayList<Integer>>>();
	private static BufferedWriter bufferedWriter = null;

	public static void main(String[] args) {
		String line = null;
		int n;

		//file reading
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader("input.txt"));
			line = bufferedReader.readLine();
			n = Integer.parseInt(line);

			for (int i=0;i<n;i++) {
				ArrayList<ArrayList<Integer>> puzzle = new ArrayList<ArrayList<Integer>>();
				line = bufferedReader.readLine();
				subgridSize.add(Integer.parseInt(line));
				int squaredLen = subgridSize.get(i)*subgridSize.get(i);
				for (int j=0;j<squaredLen;j++) {
					ArrayList<Integer> puzzleRow = new ArrayList<Integer>();
					line = bufferedReader.readLine();
					String[] tokens = line.split(" ");
					for (String tok: tokens) {
						puzzleRow.add(Integer.parseInt(tok));
					}
					puzzle.add(puzzleRow);
				}
				puzzles.add(puzzle);
			}

			bufferedWriter = new BufferedWriter(new FileWriter("output.txt"));

			for (int i=0;i<n;i++) {
				bufferedWriter.write("PUZZLE " + (i+1) + "\n");
				ArrayList<ArrayList<Integer>> puz3 = puzzles.get(i);
				for (int j=0;j<subgridSize.get(i)*subgridSize.get(i);j++) {
					ArrayList<Integer> puz2 = puz3.get(j);
					for (Integer puz1: puz2) {
						bufferedWriter.write(String.valueOf(puz1) + " ");
					}
					bufferedWriter.write("\n");
				}
				bufferedWriter.write("\n");
				//findSolution(subgridSize.get(i),i);
			}

			bufferedReader.close();
			bufferedWriter.close();
		}
		catch(Exception ex) {
			System.out.println("Error reading file");
		}
	}

	@SuppressWarnings("unchecked")
	public static void findSolution(int size, int puzzleIndex) {
		int gridTotal = (int)Math.pow(size,4);
		int rowcolTotal = (int)Math.pow(size, 2);
		int solCounter = 0;
		int[][] options = new int[gridTotal+2][rowcolTotal+2];
		int[] noptions = new int[rowcolTotal+2];
		String[] types = {"regular","x","y","xy"};
		ArrayList<ArrayList<Integer>> grid = (ArrayList<ArrayList<Integer>>) puzzles.get(puzzleIndex).clone();

		for (String solType: types) {
			if (solType.equals("y") && rowcolTotal%2==0) continue;

			int move = 0, start = 0;
			noptions[start] = 1;
			//find solution using backtracking
			while (noptions[start] > 0) {
				if (noptions[move] > 0) {
					move++;
					noptions[move] = 0; //initialize new move-1

					if (move == gridTotal+1) { //solution found
						System.out.println("SOLUTION " + (solCounter+1));
						for(int i=1;i<move;i++) {
							System.out.print(String.valueOf(options[i][noptions[i]]) + " ");
				        	if(i % rowcolTotal == 0) {
				        		System.out.println();
				        	}
				    	}
				    	solCounter++;
				    	move--; //go back to the last cell
				    	noptions[move]--; //pop
					}
				}
			}
		}
	}
}