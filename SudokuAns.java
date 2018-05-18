import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.text.PlainDocument;

public class SudokuAns extends JFrame {
	private int puzzleNumber;
	private int subSize;
	private ArrayList<ArrayList<Integer>> puzzle = new ArrayList<ArrayList<Integer>>();
	private JPanel panel = null;
	private JPanel topPanel = null;
	private JPanel gridPanel = null;
	private JPanel bottomPanel = null;
	private JButton solRegButton = null;
	private JButton solXButton = null;
	private JButton solYButton = null;
	private JButton solXYButton = null;
	private JButton nextSolButton = null;
	private JButton prevSolButton = null;
	private JLabel numSolLabel = null;
	private JTextField[][] grid = null;
	private int[][] numArr;
	private int currentSubgrid = 0;
	private ArrayList<Integer> ygrids = new ArrayList<Integer>();
	private BufferedWriter bufferedWriter = null;
	private ArrayList<ArrayList<ArrayList<Integer>>> solReg;
	private ArrayList<ArrayList<ArrayList<Integer>>> solX;
	private ArrayList<ArrayList<ArrayList<Integer>>> solY;
	private ArrayList<ArrayList<ArrayList<Integer>>> solXY;
	private String currentSolType = null;
	private int currentSolIdx = 0; 
	
	public SudokuAns(int puzzleNumber, int subSize, ArrayList<ArrayList<Integer>> puzzle, int x, int y) {
		super("Puzzle " + puzzleNumber + " Solutions");
		this.puzzleNumber = puzzleNumber;
		this.subSize = subSize;
		this.puzzle = puzzle;
		
		solReg = new ArrayList<ArrayList<ArrayList<Integer>>>();
		solX = new ArrayList<ArrayList<ArrayList<Integer>>>();
		solY = new ArrayList<ArrayList<ArrayList<Integer>>>();
		solXY = new ArrayList<ArrayList<ArrayList<Integer>>>();
		
		findSolution(subSize,puzzle); //get all solutions
		
		writeToFile(); //file writing
	
		initSubgridLabels(subSize*subSize);
		
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
	    setBounds(100, 100, 500, 500);
		
		topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(1,4));
		topPanel.setSize(50, 50);
		
		solRegButton = new JButton("Regular");
		solRegButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleButtons(solRegButton); //set color
				currentSolType = "regular";
				currentSolIdx = 0; //go back to the start
				if (solReg.size() != 0) {
					numSolLabel.setText("Solution: " + (currentSolIdx+1) + " of " + solReg.size());	
				}
				else {
					emptyGrid();
					numSolLabel.setText("No solution");
				}
				placeNextAndPrev();
				showRegularSolutions();
				//switch to solution regular
			}
		});
		
		solXButton = new JButton("X");
		solXButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleButtons(solXButton);
				currentSolType = "x";
				currentSolIdx = 0; //go back to the start
				if (solX.size() != 0) {
					numSolLabel.setText("Solution: " + (currentSolIdx+1) + " of " + solX.size());	
				}
				else {
					emptyGrid();
					numSolLabel.setText("No solution");
				}
				placeNextAndPrev();
				showXSolutions();
				//switch to solution x
			}
		});
		
		solYButton = new JButton("Y");
		solYButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleButtons(solYButton);
				currentSolType = "y";
				currentSolIdx = 0; //go back to the start
				if (solY.size() != 0) {
					numSolLabel.setText("Solution: " + (currentSolIdx+1) + " of " + solY.size());	
				}
				else {
					emptyGrid();
					numSolLabel.setText("No solution");
				}
				placeNextAndPrev();
				showYSolutions();
				//switch to solution y
			}
		});
		
		solXYButton = new JButton("XY");
		solXYButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleButtons(solXYButton);
				currentSolType = "xy";
				currentSolIdx = 0; //go back to the start
				if (solXY.size() != 0) {
					numSolLabel.setText("Solution: " + (currentSolIdx+1) + " of " + solXY.size());	
				}
				else {
					emptyGrid();
					numSolLabel.setText("No solution");
				}
				placeNextAndPrev();
				showXYSolutions();
				//switch to solution xy
			}
		});
		
		toggleButtons(solRegButton);
		currentSolType = "regular";
		
		topPanel.add(solRegButton);
		topPanel.add(solXButton);
		topPanel.add(solYButton);
		topPanel.add(solXYButton);
		
		panel.add(topPanel, BorderLayout.NORTH);
	    
	    gridPanel = new JPanel();    
	    
	    initGrid(subSize, puzzle);
	 
	    panel.add(gridPanel, BorderLayout.CENTER);
	    
	    bottomPanel = new JPanel();
		bottomPanel.setSize(50, 50);
		bottomPanel.setLayout(new GridLayout(1,3));
		
		nextSolButton = new JButton("Next");
		nextSolButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//go to next solution
				currentSolIdx++;
				if (currentSolType.equals("regular")) {
					numSolLabel.setText("Solution: " + (currentSolIdx+1) + " of " + solReg.size());
					placeNextAndPrev();
					showRegularSolutions();
				}
				else if (currentSolType.equals("x")) {
					numSolLabel.setText("Solution: " + (currentSolIdx+1) + " of " + solX.size());
					placeNextAndPrev();
					showXSolutions();
				}
				else if (currentSolType.equals("y")) {
					numSolLabel.setText("Solution: " + (currentSolIdx+1) + " of " + solY.size());
					placeNextAndPrev();
					showYSolutions();
				}
				else if (currentSolType.equals("xy")) {
					numSolLabel.setText("Solution: " + (currentSolIdx+1) + " of " + solXY.size());
					placeNextAndPrev();
					showXYSolutions();
				}
			}
		});
		
		prevSolButton = new JButton("Back");
		prevSolButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//go to prev solution
				currentSolIdx--;
				if (currentSolType.equals("regular")) {
					placeNextAndPrev();
					showRegularSolutions();
					numSolLabel.setText("Solution: " + (currentSolIdx+1) + " of " + solReg.size());
				}
				else if (currentSolType.equals("x")) {
					placeNextAndPrev();
					showXSolutions();
					numSolLabel.setText("Solution: " + (currentSolIdx+1) + " of " + solX.size());
				}
				else if (currentSolType.equals("y")) {
					placeNextAndPrev();
					showYSolutions();
					numSolLabel.setText("Solution: " + (currentSolIdx+1) + " of " + solY.size());
				}
				else if (currentSolType.equals("xy")) {
					placeNextAndPrev();
					showXYSolutions();
					numSolLabel.setText("Solution: " + (currentSolIdx+1) + " of " + solXY.size());
				}
			}
		});
		
		placeNextAndPrev();
		showRegularSolutions();
		
		if (solReg.size() != 0) {
			numSolLabel = new JLabel("Solution: " + (currentSolIdx+1) + " of " + solReg.size(), SwingConstants.CENTER);
		}
		else {
			numSolLabel = new JLabel("No solution", SwingConstants.CENTER);
		}
		
		bottomPanel.add(prevSolButton);
		bottomPanel.add(numSolLabel);
		bottomPanel.add(nextSolButton);
	
	    panel.add(bottomPanel, BorderLayout.SOUTH);
	    
	    getContentPane().add(panel);
	    
	    System.out.println("SolReg: " + solReg.size());
	    System.out.println("SolX: " + solX.size());
	    System.out.println("SolY: " + solY.size());
	    System.out.println("SolXY: " + solXY.size());
	    
	    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    
	    setLocation(x,y);
	    setVisible(true);
	}	
	
	public void placeNextAndPrev() { //position next and prev button if there will be next and prev grids, respectively
		if (currentSolIdx > 0) { //regardless of solution type
			prevSolButton.setVisible(true);
		}
		else {
			prevSolButton.setVisible(false);
		}
		
		if (currentSolType.equals("regular") && solReg.size() != 0) { //has regular solutions
			if (currentSolIdx + 1 != solReg.size()) {
				nextSolButton.setVisible(true);
			}
			else {
				nextSolButton.setVisible(false);
			}
		}
		else if (currentSolType.equals("x") && solX.size() != 0) { //has x solutions
			if (currentSolIdx + 1 != solX.size()) {
				nextSolButton.setVisible(true);
			}
			else {
				nextSolButton.setVisible(false);
			}
		}
		else if (currentSolType.equals("y") && solY.size() != 0) { //has y solutions
			if (currentSolIdx + 1 != solY.size()) {
				nextSolButton.setVisible(true);
			}
			else {
				nextSolButton.setVisible(false);
			}
		}
		else if (currentSolType.equals("xy") && solXY.size() != 0) { //has xy solutions
			if (currentSolIdx + 1 != solXY.size()) {
				nextSolButton.setVisible(true);
			}
			else {
				nextSolButton.setVisible(false);
			}
		}
		else {
			nextSolButton.setVisible(false);
		}
	}
	
	public void emptyGrid() { //remove all grid contents
		for (int i=0;i<subSize*subSize;i++) {
			for (int j=0;j<subSize*subSize;j++) {
				grid[i][j].setText("");
				grid[i][j].setEditable(false);
			}
		}
	}
	
	public void writeToFile() { //file writing per puzzle solutions
		try {
			bufferedWriter = new BufferedWriter(new FileWriter("puzzle" + puzzleNumber + "solutions.txt"));
			ArrayList<ArrayList<Integer>> toWrite;
			
			bufferedWriter.write("SOLUTION REGULAR \n");
			for (int i=0;i<solReg.size();i++) {
				bufferedWriter.write("Solution regular #" + (i+1) + "\n");
				toWrite = solReg.get(i);
				for (int j=0;j<toWrite.size();j++) {
					for (int k=0;k<toWrite.get(j).size();k++) {
						bufferedWriter.write(toWrite.get(j).get(k) + " ");
					}
					bufferedWriter.write("\n");
				}
			}
			bufferedWriter.write("=====================================================================================================================================\n");
			
			bufferedWriter.write("SOLUTION X \n");
			for (int i=0;i<solX.size();i++) {
				bufferedWriter.write("Solution x #" + (i+1) + "\n");
				toWrite = solX.get(i);
				for (int j=0;j<toWrite.size();j++) {
					for (int k=0;k<toWrite.get(j).size();k++) {
						bufferedWriter.write(toWrite.get(j).get(k) + " ");
					}
					bufferedWriter.write("\n");
				}
			}
			bufferedWriter.write("=====================================================================================================================================\n");
			
			bufferedWriter.write("SOLUTION Y \n");
			for (int i=0;i<solY.size();i++) {
				bufferedWriter.write("Solution y #" + (i+1) + "\n");
				toWrite = solY.get(i);
				for (int j=0;j<toWrite.size();j++) {
					for (int k=0;k<toWrite.get(j).size();k++) {
						bufferedWriter.write(toWrite.get(j).get(k) + " ");
					}
					bufferedWriter.write("\n");
				}
			}
			bufferedWriter.write("=====================================================================================================================================\n");
			
			bufferedWriter.write("SOLUTION XY \n");
			for (int i=0;i<solXY.size();i++) {
				bufferedWriter.write("Solution xy #" + (i+1) + "\n");
				toWrite = solXY.get(i);
				for (int j=0;j<toWrite.size();j++) {
					for (int k=0;k<toWrite.get(j).size();k++) {
						bufferedWriter.write(toWrite.get(j).get(k) + " ");
					}
					bufferedWriter.write("\n");
				}
			}
			bufferedWriter.write("=====================================================================================================================================\n");
			
			bufferedWriter.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showRegularSolutions() { //show the results from the arraylist to the grid
		if (solReg.size() != 0) {
			ArrayList<ArrayList<Integer>> toShow = solReg.get(currentSolIdx);
			for (int i=0;i<toShow.size();i++) {
				for (int j=0;j<toShow.get(i).size();j++) {
					grid[i][j].setText(String.valueOf(toShow.get(i).get(j)));
					grid[i][j].setEditable(false);
				}
			}
		}
	}
	
	public void showXSolutions() { //show the results from the arraylist to the grid
		if (solX.size() != 0) {
			ArrayList<ArrayList<Integer>> toShow = solX.get(currentSolIdx);
			for (int i=0;i<toShow.size();i++) {
				for (int j=0;j<toShow.get(i).size();j++) {
					grid[i][j].setText(String.valueOf(toShow.get(i).get(j)));
					grid[i][j].setEditable(false);
				}
			}
		}
	}
	
	public void showYSolutions() { //show the results from the arraylist to the grid
		if (solY.size() != 0) {
			ArrayList<ArrayList<Integer>> toShow = solY.get(currentSolIdx);
			for (int i=0;i<toShow.size();i++) {
				for (int j=0;j<toShow.get(i).size();j++) {
					grid[i][j].setText(String.valueOf(toShow.get(i).get(j)));
					grid[i][j].setEditable(false);
				}
			}
		}
	}
	
	public void showXYSolutions() { //show the results from the arraylist to the grid
		if (solXY.size() != 0) {
			ArrayList<ArrayList<Integer>> toShow = solXY.get(currentSolIdx);
			for (int i=0;i<toShow.size();i++) {
				for (int j=0;j<toShow.get(i).size();j++) {
					grid[i][j].setText(String.valueOf(toShow.get(i).get(j)));
					grid[i][j].setEditable(false);
				}
			}
		}
	}
	
	public void initSubgridLabels(int squaredLen) { //initialize the labels of subgrids to -1
		numArr = new int[squaredLen][];
		
		for(int i=0; i<squaredLen; i++){
		    numArr[i] = new int[squaredLen];
		    for(int j=0; j<squaredLen; j++){
		      numArr[i][j] = -1;
		    }
		}
	}
	
	public void toggleButtons(JButton button) { //indicates current type of solution by changing the button background
		solRegButton.setBackground(null);
		solXButton.setBackground(null);
		solYButton.setBackground(null);
		solXYButton.setBackground(null);
		
		button.setBackground(Color.CYAN);
	}
	
	public void initGrid(int subSize, ArrayList<ArrayList<Integer>> puzzle) { //prepare the grid components
		int squaredLen = subSize*subSize;
		
		gridPanel.setLayout(new GridLayout(subSize,subSize,5,5));
	    JPanel[] subPanels = new JPanel[subSize*subSize];
	    for (int i=0;i<squaredLen;i++) {
	    	subPanels[i] = new JPanel();
	    	subPanels[i].setLayout(new GridLayout(subSize,subSize));
	    	subPanels[i].setBorder(new LineBorder(Color.BLACK));
	    }
	    
	    grid = new JTextField[squaredLen][squaredLen];
	    for (int i=0;i<squaredLen;i++) {
	    	for (int j=0;j<squaredLen;j++) {
	    		getUISubgrid(i, j, subSize, numArr); //label the subgrids
	    	}
	    }
	    
	    for (int i = 0; i < squaredLen; i++){
	        for (int j = 0; j < squaredLen; j++){
	        	int subgridNum = numArr[i][j]; //get the subgrid number generated previously
	        	
	            grid[i][j] = new JTextField();
	            Font font = new Font("Segoe Script", Font.BOLD, 500/(2*squaredLen)); //adjust font size based on cell size
	            grid[i][j].setFont(font);
	            grid[i][j].setHorizontalAlignment(JTextField.CENTER);
	            grid[i][j].setBorder(new LineBorder(Color.BLACK));
	            grid[i][j].setOpaque(true);
	            
	            PlainDocument doc = (PlainDocument) grid[i][j].getDocument(); //filter wrong inputs
	            doc.setDocumentFilter(new MyIntFilter(squaredLen)); //pass max val
	            subPanels[subgridNum].add(grid[i][j]); //then place the current grid to the appropriate subgrid
	        }
	    }
	    for (int i=0;i<subPanels.length;i++) {
	    	gridPanel.add(subPanels[i]); //add each subgrid to the puzzle
	    }
	}
	
	public void getUISubgrid(int row, int col, int subgridSize, int[][] numArr){ //label the cells according to their subgrid number
		int startIndRow = (row/subgridSize)*subgridSize;
		int endIndRow = (startIndRow+subgridSize);
		int startIndCol = (col/subgridSize)*subgridSize;
		int endIndCol = (startIndCol+subgridSize);
		int i, j, counter=0;
		
		if (numArr[startIndRow][startIndCol] != -1) { //subgrid number already identified
			return;
		}
	
		for(i=startIndRow; i<endIndRow; i++){
			for(j=startIndCol; j<endIndCol; j++){
				numArr[i][j] = currentSubgrid; //same subgrid labels if within the same subgrid
			}
		}
		currentSubgrid++;
	}
	
	@SuppressWarnings("unchecked")
	public void findSolution(int size, ArrayList<ArrayList<Integer>> puzzle) { //solve the puzzle using backtracking
		int gridTotal = (int)Math.pow(size,4);
		int rowcolTotal = (int)Math.pow(size,2);
		int npossible, row, col, i, j, count = 0;
		int[][] options = new int[gridTotal+2][rowcolTotal+2];
		int[] noptions = new int[gridTotal+2];
		int[] candidates = new int[rowcolTotal];
		int[][] numArr = new int[rowcolTotal][];
		int[] subgrids = new int[rowcolTotal];
		String[] solTypes = {"regular","x","y","xy"};
		ArrayList<ArrayList<Integer>> grid = puzzle;
		ArrayList<ArrayList<Integer>> solution;

		for(i=0; i<rowcolTotal; i++){
		    numArr[i] = new int[rowcolTotal];
		    for(j=0; j<rowcolTotal; j++){
		      numArr[i][j] = ++count;
		    }
		}

		getYGrids(numArr, rowcolTotal);

		for (int h=0; h<4; h++) {
			
			if (solTypes[h].equals("y") && rowcolTotal%2==0){
				try{
					System.out.println("SOLUTION " + solTypes[h]);
					continue;
				}catch(Exception e){
					e.printStackTrace();
				}
			}else if(solTypes[h].equals("xy") && rowcolTotal%2==0){
				try{
					System.out.println("SOLUTION " + solTypes[h]);
					continue;
				}catch(Exception e){
					e.printStackTrace();
				}
			}else if (solTypes[h].equals("x")) {
		      	int correctX = checkXGiven(rowcolTotal, grid);
		      	int correct = checkGiven(rowcolTotal, grid);
		      	if (correctX == 0 || correct == 0) {
	          		try{
						System.out.println("SOLUTION " + solTypes[h]);
					}catch(Exception e){
						e.printStackTrace();
					}  
		        continue;
    		  	}
    		}else if (solTypes[h].equals("y")) {
		      	int correctY = checkYGiven(rowcolTotal, grid);
		      	int correct = checkGiven(rowcolTotal, grid);
		      	if (correctY == 0 || correct == 0) {
	          		try{
						System.out.println("SOLUTION " + solTypes[h]);
					}catch(Exception e){
						e.printStackTrace();
					} 
		        continue;
    		  	}
    		}else if (solTypes[h].equals("xy")) {
		      	int correctX = checkXGiven(rowcolTotal, grid);
		      	int correctY = checkYGiven(rowcolTotal, grid);
		      	int correct = checkGiven(rowcolTotal, grid);
		      	if (correctX == 0 || correctY == 0 || correct == 0) {
	          		try{
						System.out.println("SOLUTION " + solTypes[h]);
					}catch(Exception e){
						e.printStackTrace();
					}  
		        continue;
    		  	}
    		}else if(solTypes[h].equals("regular")){
    			int correct = checkGiven(rowcolTotal, grid);
    			if (correct == 0) {
	          		try{
						System.out.println("SOLUTION " + solTypes[h]);
					}catch(Exception e){
						e.printStackTrace();
					}  
		        continue;
    		  	}
    		}

			//if (solTypes[h].equals("xy") || solTypes[h].equals("y")) break;
			try{
				System.out.println("SOLUTION " + solTypes[h]);
			}catch(Exception e){
				e.printStackTrace();
			}
			int move = 0, start = 0, solCounter = 0;
			noptions[start] = 1;
			//find solution using backtracking
			while (noptions[start] > 0) {
				solution = new ArrayList<ArrayList<Integer>>();
				if (noptions[move] > 0) {
					move++;
					noptions[move] = 0; //initialize new move-1

					if (move == gridTotal+1) { //solution found
						try {
							System.out.println("SOLUTION " + solTypes[h] + " " + (solCounter+1));
							ArrayList<Integer> solRow = new ArrayList<Integer>();
							for(i=1;i<move;i++) {
								System.out.print(String.valueOf(options[i][noptions[i]]) + " ");
								solRow.add(options[i][noptions[i]]); //add to solution row
								
					        	if(i % rowcolTotal == 0) { //finished row
					        		System.out.println();
					        		solution.add(solRow); //add the row
					        		solRow = new ArrayList<Integer>(); //new row
					        	}
					    	}
							if (solTypes[h].equals("regular")) {
								solReg.add(solution);
							}
							else if (solTypes[h].equals("x")) {
								solX.add(solution);
							}
							else if (solTypes[h].equals("y")) {
								solY.add(solution);
							}
							else if (solTypes[h].equals("xy")) {
								solXY.add(solution);
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
									if (solTypes[h].equals("x") || solTypes.equals("xy")) {
										if (inXGrids(j, row, col, numArr, rowcolTotal)) {
											if (candidates[i-1] == options[j][noptions[j]]) {
												break;
											}
										}
									}
									if (solTypes[h].equals("y") || solTypes.equals("xy")) {
										if (inYGrids(j, row, col, numArr, rowcolTotal)) {
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
			//break;
		}
	}
	
	public int checkPossible(int[] candidates, String solutionType, int size, int row, int col, ArrayList<ArrayList<Integer>> grid) {

		int[] numcounter = new int[size+2];
		int subgridSize = (int)Math.sqrt(size);
		int counter = 0, flag, flagx, flagy;
		
		if(grid.get(row).get(col) != 0){
			candidates[counter] = grid.get(row).get(col);
		}
		else {
		    //Initialize everything to -1 and use it as indicator of true candidates
		    for(int i=0; i<size; i++) candidates[i] = -1;
		    
		    for(int i=size; i>0; i--){    //PRESORTS IN DESCENDING ORDER
		        flag = 1;
		        flagx = 1;
		        flagy = 1;

		        flag = checkSubgrid(i, subgridSize, row, col, grid);
		        if (flag == 0) continue;
		        
		        for(int j=0; j<size; j++){
		        	if(solutionType.equals("regular") || solutionType.equals("x") || solutionType.equals("y") || solutionType.equals("xy")){
		        		if(grid.get(row).get(j) == i || grid.get(j).get(col) == i) {
		        			flag = 0;
		        			break;
		        		}
		        	}

	        		if(solutionType.equals("x")  || solutionType.equals("xy")) {
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
	        					if(j==k) {
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

	        		if (solutionType.equals("y") || solutionType.equals("xy")) {
			        	if(flag != 0){  
				            for(int l=0; l<size; l++){
				              if((row == col && row < (size/2)) || ((row+col) == (size-1) && row < (size/2))){
				                if((l == (size/2) && j >= (size/2))){
				                  if(grid.get(j).get(l) == i){
				                    flag = 0; 
				                    flagy = 0; 
				                    break;
				                  }
				                }
				              }
				              if(row == col && row < (size/2) && (j == l && j < (size/2))){
				                if(grid.get(j).get(l) == i){
				                    flag = 0; 
				                    flagy = 0; 
				                    break;
				                }
				              }
				              else if(((row+col) == (size-1) && row < (size/2)) && ((j+l) == (size-1) && j < (size/2))){
				                if(grid.get(j).get(l) == i){
				                  flag = 0; 
				                  flagy = 0; 
				                  break;
				                }
				              }
				            }
      					}
	        		}

	        		if (solutionType.equals("xy")){
	        			if(flagx == 0 || flagy == 0){
            				flag = 0; break;
          				}
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
	
	public int checkSubgrid(int val, int size, int row, int col, ArrayList<ArrayList<Integer>> grid) {
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
	
	public void getSubgrid(int move, int row, int col, int subgridSize, int[][] numArr, int[] subgrids){
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
	
	public void getYGrids(int[][] numArr, int total){
		for(int i=0; i<total; i++){
	    	for(int j=0; j<total; j++){
	      		if(i==j && i < (total/2) || ((i+j)==(total-1)) && i < (total/2) || j == (total/2) && i >= (total/2)){
	        		ygrids.add(numArr[i][j]); 
	      		}
	    	}
	  	}
	}

	public boolean inSubgrid(int x, int size, int[] subgrids) {
		for (int i=0;i<size;i++) {
			if (subgrids[i] == x) return true;
		}
		return false;
	}
	
	public boolean inXGrids(int x, int row, int col, int[][] numArr, int total){
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

	public boolean inYGrids(int x, int row, int col, int[][] numArr, int total){
	  	if((row >= (total/2) && col==(total/2))  || ((row==col) && row < (total/2)) || ((row+col) == (total-1) && row < (total/2))){
	    	for(int i=0; i<(total+total/2); i++){
	      		if(ygrids.get(i) == x){
	        	return true;
	      		}
	    	}
	  	}		
	  	return false;	
	}

	public int checkGiven(int size, ArrayList<ArrayList<Integer>> puzzle){
		int i,j,k,l;
		int[] countArrRow = new int[size+2];
		int[] countArrCol = new int[size+2];
		int subgridSize = (int)Math.sqrt(size);
		
		for (l=1;l<size+2;l++) {
			countArrRow[l] = 0;
	    	countArrCol[l] = 0;
		}

		for(i=0;i<size;i++){
			for(j=0;j<size;j++){
				if(puzzle.get(i).get(j) != 0){
					countArrRow[puzzle.get(i).get(j)]++; 
					if (countArrRow[puzzle.get(i).get(j)] > 1) return 0;
				}
				if(puzzle.get(j).get(i) != 0){
					countArrCol[puzzle.get(j).get(i)]++;
					if (countArrCol[puzzle.get(j).get(i)] > 1) return 0;
				}
			}
			for (l=1;l<size+2;l++) {
				countArrRow[l] = 0;
		    	countArrCol[l] = 0;
			}
		}

		for(i=0; i<size; i+=subgridSize){
			for(j=0; j<size; j+=subgridSize){
				int checker = checkGivenSubgrid(size,i,j,puzzle);
				if(checker==0) return 0;
			}
		}	

		return 1;
	}

	public int checkGivenSubgrid(int size, int row, int col, ArrayList<ArrayList<Integer>> puzzle) {
		int[] countArrSubGrid = new int[size+2];
		int subgridSize = (int)Math.sqrt(size);

		int i, j;

		for(i=1;i<size+2;i++){
			countArrSubGrid[i] = 0;
		}

		for (i=row;i<row+subgridSize;i++) {
			for (j=col;j<col+subgridSize;j++) {
				if (puzzle.get(i).get(j) != 0) {
					countArrSubGrid[puzzle.get(i).get(j)]++;
					if(countArrSubGrid[puzzle.get(i).get(j)] > 1) return 0;
				}
			}
		}

		return 1;
	}

	public int checkXGiven(int size, ArrayList<ArrayList<Integer>> puzzle){
		int[] countArrLeft = new int[size+2];
  		int[] countArrRight = new int[size+2];
  		int i,j;
		for (i=1;i<size+2;i++) {
			countArrLeft[i] = 0;
		    countArrRight[i] = 0;
		}

		for (i=0;i<size;i++) {
		    for (j=0;j<size;j++) {
		     	if (i==j && puzzle.get(i).get(j) != 0) {
		        	countArrLeft[puzzle.get(i).get(j)]++;
		      	}
		      	if (i+j==size-1 && puzzle.get(i).get(j) != 0) {
		        	countArrRight[puzzle.get(i).get(j)]++; 
		      	}
		    }
		  }

		for (i=1;i<size+1;i++) {
		    if (countArrRight[i] > 1 || countArrLeft[i] > 1) return 0;
		}
  		return 1;
	}

	public int checkYGiven(int size, ArrayList<ArrayList<Integer>> puzzle){
		int i,j;
		int[] countArrLeft = new int[size+2];
		int[] countArrRight = new int[size+2];

		for (i=1;i<size+2;i++) {
			countArrLeft[i] = 0;
		    countArrRight[i] = 0;
		}

		for (i=0;i<size;i++) {
		    for (j=0;j<size;j++) {
		     	if (i==j && puzzle.get(i).get(j) != 0 && i<(size/2)) {
		        	countArrLeft[puzzle.get(i).get(j)]++;

		      	}
		      	if (i+j==size-1 && puzzle.get(i).get(j) != 0 && i<(size/2)) {
		        	countArrRight[puzzle.get(i).get(j)]++; 
		      	}
		      	if (j==(size/2) && i>=(size/2) && puzzle.get(i).get(j) != 0){
		      		countArrLeft[puzzle.get(i).get(j)]++;
		      		countArrRight[puzzle.get(i).get(j)]++; 
		      	}
		    }
		}

		for (i=1;i<size+1;i++) {
		    if (countArrRight[i] > 1 || countArrLeft[i] > 1) return 0;
		}
		return 1;
	}

}
