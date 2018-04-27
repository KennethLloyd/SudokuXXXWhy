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
				//file writing
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
				findSolution(subgridSize.get(i),i);
			}

			bufferedReader.close();
			bufferedWriter.close();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void findSolution(int size, int puzzleIndex) {
		int gridTotal = (int)Math.pow(size,4);
		int rowcolTotal = (int)Math.pow(size,2);
		int npossible, row, col, i, j, count = 0;
		int[][] options = new int[gridTotal+2][rowcolTotal+2];
		int[] noptions = new int[gridTotal+2];
		int[] candidates = new int[rowcolTotal];
		int[][] numArr = new int[rowcolTotal][];
		int[] subgrids = new int[rowcolTotal];
		String[] solTypes = {"x","regular","y","xy"};
		ArrayList<ArrayList<Integer>> grid = (ArrayList<ArrayList<Integer>>) puzzles.get(puzzleIndex).clone();
		
		for(i=0; i<rowcolTotal; i++){
		    numArr[i] = new int[rowcolTotal];
		    for(j=0; j<rowcolTotal; j++){
		      numArr[i][j] = ++count;
		    }
		}
		for (int h=0; h<2; h++) { //limit to x and regular temporarily
			if (solTypes[h].equals("y") && rowcolTotal%2==0) continue;

			if (solTypes[h].equals("xy") || solTypes[h].equals("y")) break;
			System.out.println("SOLUTION " + solTypes[h]);
			int move = 0, start = 0, solCounter = 0;
			noptions[start] = 1;
			//find solution using backtracking
			while (noptions[start] > 0) {
				if (noptions[move] > 0) {
					move++;
					noptions[move] = 0; //initialize new move-1

					if (move == gridTotal+1) { //solution found
						try {
							bufferedWriter.write("SOLUTION " + solTypes[h] + " " + (solCounter+1) + "\n");
							System.out.println("SOLUTION " + solTypes[h] + " " + (solCounter+1));
							for(i=1;i<move;i++) {
								bufferedWriter.write(String.valueOf(options[i][noptions[i]]) + " ");
								System.out.print(String.valueOf(options[i][noptions[i]]) + " ");
					        	if(i % rowcolTotal == 0) {
					        		bufferedWriter.write("\n");
					        		System.out.println();
					        	}
					    	}
					    	solCounter++;
					    	move--; //go back to the last cell
					    	noptions[move]--; //pop
						}
						catch(Exception e) {
							e.printStackTrace();
						}
					}
					else if (move == 1) {
						npossible = checkPossible(candidates, solTypes[h], rowcolTotal, move/rowcolTotal, (move%rowcolTotal)-1, grid);
						if (npossible == 1) {
							options[move][++noptions[move]] = candidates[0];
						}
						else {
							for (i=1;i<npossible;i++) {
								options[move][++noptions[move]] = candidates[i-1];
							}
						}
					}
					else {
						if (move % rowcolTotal == 0) {
							row = (move/rowcolTotal)-1;
							col = rowcolTotal-1;
							npossible = checkPossible(candidates, solTypes[h], rowcolTotal, row, col, grid);
						}
						else {
							row = move/rowcolTotal;
							col = (move%rowcolTotal)-1;
							npossible = checkPossible(candidates, solTypes[h], rowcolTotal, row, col, grid);
						}
						
						getSubgrid(move, row, col, size, numArr, subgrids);
						
						if (npossible == 1) {
							options[move][++noptions[move]] = candidates[0];
						}
						else {
							for (i=1;i<npossible;i++) {
								for (j=move-1; j>=1; j--) {
									if (inSubgrid(j, rowcolTotal, subgrids)) {
										if (candidates[i-1] == options[j][noptions[j]]) {
											break;
										}
									}
									if (solTypes[h].equals("x")) {
										if (inXGrids(j, row, col, numArr, rowcolTotal)) {
											if (candidates[i-1] == options[j][noptions[j]]) {
												break;
											}
										}
									}
									if (move % rowcolTotal == 0) { //last column
										if(j>(move-rowcolTotal) || (move-j)%rowcolTotal == 0){
											if(candidates[i-1] == options[j][noptions[j]]) {
												break;
						                    }
										}
									}
									else if (move % rowcolTotal == 1) { //first column
										if((move-j)%rowcolTotal == 0){
											if(candidates[i-1] == options[j][noptions[j]]) {
												break;
						                    }
										}
									}
									else { //in between
										int currentRow = move/rowcolTotal;
										int leftmostIdx = (rowcolTotal * currentRow) + 1;
	
										if (j >= leftmostIdx) { //same row
											if(candidates[i-1] == options[j][noptions[j]]) break; 
						                }
										else { //not in same row
											if ((move-j)%rowcolTotal == 0) { //only check same column
												if(candidates[i-1] == options[j][noptions[j]]) break;
						                    }
										}
									}
								}
								if (!(j>=1)) {
									options[move][++noptions[move]] = candidates[i-1];
								}
							}
						}
					}
				}	
				else { //current stack empty, pop previous stack
					move--;
					noptions[move]--;
				}
			}
			break;
		}
	}
	
	public static int checkPossible(int[] candidates, String solutionType, int size, int row, int col, ArrayList<ArrayList<Integer>> grid) {

		int[] numcounter = new int[size+2];
		int subgridSize = (int)Math.sqrt(size);
		int counter = 0, flag;
		
		if(grid.get(row).get(col) != 0){
			candidates[counter] = grid.get(row).get(col);
		}
		else {
		    //Initialize everything to -1 and use it as indicator of true candidates
		    for(int i=0; i<size; i++) candidates[i] = -1;
		    
		    for(int i=size; i>0; i--){    //PRESORTS IN DESCENDING ORDER
		        flag = 1;

		        flag = checkSubgrid(i, subgridSize, row, col, grid);
		        if (flag == 0) continue;
		        
		        for(int j=0; j<size; j++){
		        	if(solutionType.equals("regular") || solutionType.equals("x")){
		        		if(grid.get(row).get(j) == i || grid.get(j).get(col) == i) {
		        			flag = 0;
		        			break;
		        		}
		        		if(solutionType.equals("x")) {
		        			for(int k=0; k<size; k++){
		        				if(row==col && (col+row) == (size-1)){
		        					if(j==k || (j+k) == (size-1)){
		        						if(grid.get(j).get(k) == i){
		        							if(numcounter[grid.get(j).get(k)] < 2){
		        								numcounter[grid.get(j).get(k)] += 1;
		        								continue;
		        							}
		        							flag = 0;
		        							break;
		        						}
		        					}
		        	            }
		        				else if(row == col){
		        					if(j==k){
		        						if(grid.get(j).get(k) == i){
		        							flag = 0;
		        							break;
		        		                }
		        		            }
		        				}
		        				else if((col+row) == (size-1)){
		        					if((j+k) == (size-1)){
		        						if(grid.get(j).get(k) == i){
		        							flag = 0;
		        							break;
		        						}
		        					}
	        		            }
		        			}
		        		}
		        		if (solutionType.equals("y")) {
		        			
		        		}
		        		if (solutionType.equals("xy")) {
		        			
		        		}
		            }
		        	if (solutionType.equals("y")) {
		        		
		        	}
		        	if (solutionType.equals("xy")) {
	        			
	        		}
		        }

		        if (flag == 1) {
		        	candidates[counter] = i;
		        	counter++;
		        }
		    }
		}
		
		return counter+1;
	}
	
	public static int checkSubgrid(int val, int size, int row, int col, ArrayList<ArrayList<Integer>> grid) {
		int startRow = (row/size)*size;
		int startCol = (col/size)*size;
		int i, j;

		for (i=startRow;i<startRow+size;i++) {
			for (j=startCol;j<startCol+size;j++) {
				if (grid.get(i).get(j) == val) {
					return 0;
				}
			}
		}
		return 1;
	}
	
	public static void getSubgrid(int move, int row, int col, int subgridSize, int[][] numArr, int[] subgrids){
		int startIndRow = (row/subgridSize)*subgridSize;
		int endIndRow = (startIndRow+subgridSize);
		int startIndCol = (col/subgridSize)*subgridSize;
		int endIndCol = (startIndCol+subgridSize);
		int i, j, counter=0;
	
		for(i=startIndRow; i<endIndRow; i++){
			for(j=startIndCol; j<endIndCol; j++){
				subgrids[counter] = numArr[i][j];
				counter++;
			}
		}
	}
	
	public static boolean inSubgrid(int x, int size, int[] subgrids) {
		for (int i=0;i<size;i++) {
			if (subgrids[i] == x) return true;
		}
		return false;
	}
	
	public static boolean inXGrids(int x, int row, int col, int[][] numArr, int total){
		if(row==col || ((col+row) == (total-1))){
			for(int i=0; i<total; i++){
				for(int j=0; j<total; j++){  
					if(row==col && (col+row) == (total-1)){
						if(i==j || ((i+j)==total-1)){
							if(((i*total)+j+1) == x){
								return true;
							}
						}
					}
					else if(row==col && i==j){
						if(((i*total)+j+1) == x){
							return true;
						}
					}
					else if((col+row) == (total-1) && ((i+j)==total-1)){
						if(((i*total)+j+1) == x){
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}