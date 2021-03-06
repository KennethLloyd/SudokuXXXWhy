import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.PlainDocument;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class SudokuUI extends JFrame {
	final JFileChooser fc = new JFileChooser();
	private File file = null;
	private static ArrayList<Integer> subgridSize;
	private static ArrayList<ArrayList<ArrayList<Integer>>> puzzles;
	private JPanel panel = null;
	private JPanel topPanel = null;
	private JPanel gridPanel = null;
	private JPanel bottomPanel = null;
	private JTextField[][] grid = null;
	private int currentPuzzle = -1;
	private int n; //number of puzzles
	private int[][] numArr;
	private int currentSubgrid = 0;
	private JButton nextPuzzleButton = null;
	private JButton prevPuzzleButton = null;
	private JLabel puzzleNumLabel = null;
	private SudokuAns sudokuAns = null;
	
	public SudokuUI() {
		//default if the user did not select a dimension
		int subSize = 3;
	    int row = 9;
	    int col = 9;
		
		String[] gridChoices = {"4x4","9x9","16x16"};
		String s = (String)JOptionPane.showInputDialog(this,
				"Select default grid dimensions",
                "Choose Grid",
                JOptionPane.PLAIN_MESSAGE,
                null,
                gridChoices,
                "9x9");
		
		if ((s != null) && (s.length() > 0)) {
		    if (s.equals("4x4")) {
		    	subSize = 2;
		    	row = 4;
		    	col = 4;
		    }
		    else if (s.equals("9x9")) {
		    	subSize = 3;
		    	row = 9;
		    	col = 9;
		    }
		    else if (s.equals("16x16")) {
		    	subSize = 4;
		    	row = 16;
		    	col = 16;
		    }
		}
	    
		subgridSize = new ArrayList<Integer>();
		subgridSize.add(subSize);
		currentPuzzle++;
		
	    initSubgridLabels(row);
		
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(1,3));
		topPanel.setSize(50, 50);
		
		JButton browseButton = new JButton("Open"); //file chooser
		browseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(getContentPane());

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            file = fc.getSelectedFile();
		            readFile(file); //parse
		        }
			}
		});
		
		nextPuzzleButton = new JButton("Next Puzzle");
		nextPuzzleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentPuzzle++;
				refreshGrid(currentPuzzle);
			}
		});
		
		nextPuzzleButton.setVisible(false);
		
		prevPuzzleButton = new JButton("Previous Puzzle");
		prevPuzzleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentPuzzle--;
				refreshGrid(currentPuzzle);
			}
		});
		
		prevPuzzleButton.setVisible(false);
		
		topPanel.add(prevPuzzleButton);
		topPanel.add(browseButton);
		topPanel.add(nextPuzzleButton);
		
		panel.add(topPanel, BorderLayout.NORTH);
	    
	    gridPanel = new JPanel();
	    
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setLocationRelativeTo(null);
	    setBounds(100, 100, 500, 500);
	    
	    initGrid(subSize, null);
	    
	    panel.add(gridPanel,BorderLayout.CENTER);
	    
	    bottomPanel = new JPanel();
	    bottomPanel.setLayout(new GridLayout(1,3));
		bottomPanel.setSize(50, 50);
		
		puzzleNumLabel = new JLabel("Puzzle " + (currentPuzzle+1) + " of 1", SwingConstants.CENTER);
		bottomPanel.add(puzzleNumLabel);
		
		JButton solveButton = new JButton("Solve");
		solveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (sudokuAns != null) { //has an opened window
					sudokuAns.dispose(); //close that window
					sudokuAns = null;
				}
				
				ArrayList<ArrayList<Integer>> finalGrid = new ArrayList<ArrayList<Integer>>();
				//get updated values
				int subSize = subgridSize.get(currentPuzzle);
				int row = subSize*subSize;
				int col = subSize*subSize;
				
				for (int i=0;i<row;i++) {
					ArrayList<Integer> tempGrid = new ArrayList<Integer>();
					for (int j=0;j<col;j++) {
						int val;
						String textVal = grid[i][j].getText();
						if (textVal.equals("")) { //empty cell
							val = 0;
						}
						else {
							val = Integer.parseInt(textVal); //convert to int
						}
						tempGrid.add(val); //copy final values from the ui grid
					}
					finalGrid.add(tempGrid);
				}
				for (int i=0;i<row;i++) {
					for (int j=0;j<col;j++) {
						System.out.print(finalGrid.get(i).get(j) + " ");
					}
					System.out.println();
				}
				sudokuAns = new SudokuAns(currentPuzzle+1, subSize, finalGrid, getX() + getWidth() + 20, getY() - 25);
			}
		});
		bottomPanel.add(solveButton);
		bottomPanel.add(new JLabel());
		
		panel.add(bottomPanel, BorderLayout.SOUTH);

	    getContentPane().add(panel);
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
				numArr[i][j] = currentSubgrid;
			}
		}
		currentSubgrid++;
	}
	
	public void readFile(File file) {
		currentPuzzle = -1;
		String line = null;
		subgridSize = new ArrayList<Integer>();
		puzzles = new ArrayList<ArrayList<ArrayList<Integer>>>();

		//file reading
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
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
			currentPuzzle++;
			refreshGrid(currentPuzzle);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void refreshGrid(int currentPuzzle) { //display the new grid
		if (currentPuzzle > 0) prevPuzzleButton.setVisible(true); //has a previous puzzle
		else {
			prevPuzzleButton.setVisible(false);
		}
		
		if (n > 1 && (currentPuzzle+1 != n)) nextPuzzleButton.setVisible(true); //has next puzzle
		else {
			nextPuzzleButton.setVisible(false);
		}
		
		currentSubgrid = 0; //start again
		int subSize = subgridSize.get(currentPuzzle);
		int squaredLen = subSize*subSize;
		ArrayList<ArrayList<Integer>> puzzle = puzzles.get(currentPuzzle);
		
		initSubgridLabels(squaredLen);
		
		puzzleNumLabel.setText("Puzzle " + (currentPuzzle+1) + " of " + puzzles.size());
		
		//remove all components in the panel
		gridPanel.removeAll();
		gridPanel.revalidate();
		gridPanel.repaint();
		
		initGrid(subSize, puzzle);
	}
	
	public void initSubgridLabels(int squaredLen) {
		numArr = new int[squaredLen][];
		
		for(int i=0; i<squaredLen; i++){
		    numArr[i] = new int[squaredLen];
		    for(int j=0; j<squaredLen; j++){
		      numArr[i][j] = -1;
		    }
		}
	}
	
	public void initGrid(int subSize, ArrayList<ArrayList<Integer>> puzzle) {
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
	            Font font = new Font("Segoe Script", Font.BOLD, 500/(2*squaredLen));
	            grid[i][j].setFont(font);
	            grid[i][j].setHorizontalAlignment(JTextField.CENTER);
	            grid[i][j].setBorder(new LineBorder(Color.BLACK));
	            grid[i][j].setOpaque(true);
	            if (puzzle != null && puzzle.get(i).get(j) != 0) { //has a given value
	            	grid[i][j].setText(String.valueOf(puzzle.get(i).get(j))); //display
	            	grid[i][j].setEditable(false); //cannot be changed
	            }
	            
	            PlainDocument doc = (PlainDocument) grid[i][j].getDocument();
	            doc.setDocumentFilter(new MyIntFilter(squaredLen)); //pass max val
	            subPanels[subgridNum].add(grid[i][j]); //then place the current grid to the appropriate subgrid
	        }
	    }
	    for (int i=0;i<subPanels.length;i++) {
	    	gridPanel.add(subPanels[i]); //add each subgrid to the puzzle
	    }
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SudokuUI sudoku = new SudokuUI();
		sudoku.setTitle("Sudoku XY");
		//sudoku.setLocationRelativeTo(null);
		sudoku.setVisible(true);
	}
}