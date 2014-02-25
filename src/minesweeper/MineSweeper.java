package minesweeper;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.BevelBorder;

public class MineSweeper extends JFrame {
  
  private final Image mineSweeperIcon = new ImageIcon(getClass().getResource("/images/mineSweeper.gif")).getImage();
  private final ImageIcon smileFace = new ImageIcon(getClass().getResource("/images/smileFace.gif"));
  private final ImageIcon oFace = new ImageIcon(getClass().getResource("/images/oFace.gif"));
  private final ImageIcon glassFace = new ImageIcon(getClass().getResource("/images/glassFace.gif"));
  
  public MineSweeper(String name) {
    setIconImage(mineSweeperIcon);
    setTitle("Minesweeper");
    restart.setIcon(smileFace);
    setResizable(false);
    createAndShowGUI();
  }
  
  public static final int EASY_DIFFICULTY_GRID_COL = 9;
  public static final int EASY_DIFFICULTY_GRID_ROW = 9;
  public static final int EASY_DIFFICULTY_MINES = 10;
  public static final int MEDIUM_DIFFICULTY_GRID_COL = 16;
  public static final int MEDIUM_DIFFICULTY_GRID_ROW = 16;
  public static final int MEDIUM_DIFFICULTY_MINES = 40;
  public static final int HARD_DIFFICULTY_GRID_COL = 30;
  public static final int HARD_DIFFICULTY_GRID_ROW = 16;
  public static final int HARD_DIFFICULTY_MINES = 99;
  private static final int COL_MIN = 9;
  private static final int COL_MAX = 30;
  private static final int ROW_MIN = 9;
  private static final int ROW_MAX = 24;

  
  public static final ControlPanel minesPanel = new ControlPanel(EASY_DIFFICULTY_MINES);
  private static final ControlPanel timerPanel = new ControlPanel(0);
  private JPanel controls;
  private final JButton restart = new JButton();
  private static Timer timeUpdater;
  private static ArrayList<ArrayList<MineGridField>> grid = new ArrayList<ArrayList<MineGridField>>();
  private int numOfCol;
  private int numOfRow;
  private int numOfMines;
  private boolean gameDone = false;
  private JPanel createBoard(int col, int row, int mines) {
    grid.clear();
    gameDone = false;
    numOfCol = col;
    numOfRow = row;
    numOfMines = mines;
    minesPanel.setValue(mines);
    timerPanel.setValue(0);
    
    GridLayout gameBoard = new GridLayout(row, col);
    JPanel gamePanel = new JPanel();
    gamePanel.setLayout(gameBoard);
    gamePanel.setSize(new Dimension(col*16, row*16));
    
    for (int i = 0; i < row; i++) {
      ArrayList<MineGridField> buttonArray = new ArrayList<MineGridField>();
      for (int j = 0; j < col; j++) {
        MineGridField button = new MineGridField(i, j);
        button.setParent(this);
        buttonArray.add(button);
        gamePanel.add(button);
      }
      grid.add(buttonArray);
    }
    
    Random r = new Random();
    for (int i = 0; i < mines; i++) {
      boolean setMine = false;
      while (!setMine) {
        int x = r.nextInt(row);
        int y = r.nextInt(col);
        if (!grid.get(x).get(y).isMine()) {
          grid.get(x).get(y).setMine(true);
          setMine = true;
        }
      }
    }
    int value = 0;
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
          value = 0;
          for(int m = i-1; m <= i+1 ; m++)
              for(int n = j-1; n <= j+1; n++)
                  if(m>=0 && m<row && n>=0 && n<col && grid.get(m).get(n).isMine())
                      value++;
        grid.get(i).get(j).setValue(value);
      }
    }
    
    return gamePanel;
  }

  public static void revealNeighbors(int row, int col) {
    for(int m = row-1; m <= row+1 ; m++)
        for(int n = col-1; n <= col+1; n++)
           if(m>=0 && m<grid.size() && n>=0 && n<grid.get(m).size() &&!(m==row&&n==col)){
               MineGridField neighbor = grid.get(m).get(n);
               if(neighbor.isEnabled() && !neighbor.isMine()){
                   neighbor.fieldClicked(true, false);
               }
           } 
  }

  public static void revealNeighbors(int row, int col, int value) {
    int neighFlag = 0;
    ArrayList<MineGridField> neighbors = new ArrayList<MineGridField>();
    for(int m = row-1; m <= row+1 ; m++)
        for(int n = col-1; n <= col+1; n++)
           if(m>=0 && m<grid.size() && n>=0 && n<grid.get(m).size() &&!(m==row&&n==col)){
               MineGridField neighbor = grid.get(m).get(n);
               if(neighbor.isFlagged()){
                   neighFlag++;
               } else{
                   neighbors.add(neighbor);
               }
           } 
    if(neighFlag == value){
        for(int index = 0; index<neighbors.size(); index++){
            neighbors.get(index).fieldClicked(true, false);
        }
    }
  }
  
  public void checkVictory() {
    if (!gameDone) {
      if (isVictory()) {
        for (int i = 0; i < grid.size(); i++) {
          for (int j = 0; j < grid.get(i).size(); j++) {
            MineGridField gridField = grid.get(i).get(j);
//            if (gridField.isMine() && !gridField.isFlagged()) {
//              gridField.flag();
//            }
            gridField.setEnabled(false);
          }
        }
        timeUpdater.stop();
        gameDone = true;
        restart.setIcon(glassFace);
        JOptionPane.showMessageDialog(null, "Congratulations! You won in "+timerPanel.getValue()+" seconds!");
      }
    }
  }
  
  private boolean isVictory() {
    for (int i = 0; i < grid.size(); i++) {
      for (int j = 0; j < grid.get(i).size(); j++) {
        MineGridField gridField = grid.get(i).get(j);
        if (!gridField.isMine() && gridField.isEnabled()) {
          return false;
        }
      }
    }
    return true;
  }

  public void gameOver() {
    if (!gameDone) {
      for (int i = 0; i < grid.size(); i++) {
        for (int j = 0; j < grid.get(i).size(); j++) {
          MineGridField gridField = grid.get(i).get(j);
          gridField.setEnabled(false);
          if (gridField.isFlagged() && !gridField.isMine()) {
            gridField.showMineFailPicture();
          } else if (gridField.isMine() && !gridField.isFlagged()) {
            gridField.showMinePicture();
          }
        }
      }
      timeUpdater.stop();
      gameDone = true;
      restart.setIcon(oFace);
    }
  }
  
  /**
   * Restarts the game at the current Col/Row/Mines values
   */
  public void restart() {
    getContentPane().removeAll();
    customDifficulty(getContentPane(), numOfCol, numOfRow, numOfMines);
    pack();
    setVisible(true);
  }
  
  private void initTimeCounter() {
    timerPanel.setValue(0);        
    timeUpdater = new Timer(1000, new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
        controls.setVisible(false);
        timerPanel.increment();
        controls.setVisible(true);
      }
    });
  }
  
  public void startTimer() {
    if (!timeUpdater.isRunning()) {
      timeUpdater.setRepeats(true);
      timeUpdater.start();
    }
  }

  public void setUpPanels(final Container pane, int col, int row, int mines) {
    JPanel generalPanel = new JPanel();
    generalPanel.setLayout(new BorderLayout());

    JPanel gameField = createBoard(col, row, mines);
    gameField.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    
   
    restart.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        restart.setIcon(smileFace);
        restart();
      }
    });

    restart.setPreferredSize(new Dimension(25, 25));
    controls = new JPanel();
    controls.setLayout(new BorderLayout());
    controls.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    controls.add(minesPanel, BorderLayout.WEST);
    JPanel restartPanel = new JPanel();
    restartPanel.add(restart);
    
    controls.add(restartPanel, BorderLayout.CENTER);
    controls.add(timerPanel, BorderLayout.EAST);
    generalPanel.add(controls, BorderLayout.NORTH);
    generalPanel.add(gameField, BorderLayout.SOUTH);
    
    generalPanel.setSize(new Dimension(gameField.getWidth(), gameField.getHeight()+controls.getHeight()));
    pane.add(generalPanel, BorderLayout.CENTER);
  }
  
  private void easyDifficulty(Container pane) {
    setUpPanels(pane, EASY_DIFFICULTY_GRID_COL, EASY_DIFFICULTY_GRID_ROW, EASY_DIFFICULTY_MINES);
  }
  private void mediumDifficulty(Container pane) {
    setUpPanels(pane, MEDIUM_DIFFICULTY_GRID_COL, MEDIUM_DIFFICULTY_GRID_ROW, MEDIUM_DIFFICULTY_MINES);
  }
  private void hardDifficulty(Container pane) {
    setUpPanels(pane, HARD_DIFFICULTY_GRID_COL, HARD_DIFFICULTY_GRID_ROW, HARD_DIFFICULTY_MINES);
  }
  private void customDifficulty(Container pane, int col, int row, int mines) {
    setUpPanels(pane, col, row, mines);
  }
    
  private JMenuBar setUpMenuBar() {
    
    final JMenuBar menuBar = new JMenuBar();
    
    JMenu menuGame = new JMenu("Game");
    JMenu menuHelp = new JMenu("Help");
    
    JMenuItem menuGameEasy = new JMenuItem("Easy");
    JMenuItem menuGameMedium = new JMenuItem("Medium");
    JMenuItem menuGameHard = new JMenuItem("Hard");
    JMenuItem menuGameCustom = new JMenuItem("Custom..");
//    JMenuItem menuStatistics = new JMenuItem("Statistics");
    JMenuItem menuHelpAbout = new JMenuItem("About");
    menuGameEasy.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
        getContentPane().removeAll();
        easyDifficulty(getContentPane());
        pack();
        setVisible(true);
      }
    });
    
    menuGameMedium.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
        getContentPane().removeAll();
        mediumDifficulty(getContentPane());
        pack();
        setVisible(true);
      }
    });
    
    menuGameHard.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
        getContentPane().removeAll();
        hardDifficulty(getContentPane());
        pack();
        setVisible(true);
      }
    });
    
    menuGameCustom.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
        JLabel colLabel = new JLabel("Columns:");
        JLabel rowLabel = new JLabel("Rows:");
        JLabel minesLabel = new JLabel("Mines:");
        JTextField colField = new JTextField(String.valueOf(numOfCol));
        JTextField rowField = new JTextField(String.valueOf(numOfRow));
        JTextField minesField = new JTextField(String.valueOf(numOfMines));
        Object[] selections = {colLabel, colField, rowLabel, rowField, minesLabel, minesField};
        JOptionPane.showMessageDialog(MineSweeper.this, selections, "Custom Difficulty", JOptionPane.QUESTION_MESSAGE);
        int columns = numOfCol, rows = numOfRow, mines = numOfMines;
        try {
          columns = Integer.parseInt(colField.getText());
          rows = Integer.parseInt(rowField.getText());
          mines = Integer.parseInt(minesField.getText());
        } catch(Exception ex) {
          System.out.println("Invalid Custom Dimensions");
          return;
        }
        
        columns = (columns < COL_MIN) ? COL_MIN: columns;
        columns = (columns > COL_MAX) ? COL_MAX: columns;
        rows = (rows < ROW_MIN) ? ROW_MIN: rows;
        rows = (rows > ROW_MAX) ? ROW_MAX: rows;
        int MINES_MAX = (columns-1)*(rows-1);
        mines = (mines < 10) ? 10: mines;
        mines = (mines > MINES_MAX) ? MINES_MAX: mines;
        
        getContentPane().removeAll();
        customDifficulty(getContentPane(), columns, rows, mines);
        pack();
        setVisible(true);
      }
    });
    
    menuHelpAbout.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
        String about = "MineSweeper";
        JOptionPane.showMessageDialog(MineSweeper.this, about, "About", JOptionPane.INFORMATION_MESSAGE);
      }
    });
    
    menuGame.add(menuGameEasy);
    menuGame.add(menuGameMedium);
    menuGame.add(menuGameHard);
    menuGame.add(menuGameCustom);
    
    menuHelp.add(menuHelpAbout);
    
    menuBar.add(menuGame);
    menuBar.add(menuHelp);
    
    return menuBar;    
  }

  private void createAndShowGUI() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    initTimeCounter();
    Dimension screen = getToolkit().getScreenSize();
    setLocation((screen.width - getWidth())/2, (screen.height - getHeight())/2);

    // Set up the Menu Bar
    setJMenuBar(setUpMenuBar());

    easyDifficulty(getContentPane());
    pack();
    setVisible(true);
  }

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
    } catch (UnsupportedLookAndFeelException ex) {
      ex.printStackTrace();
    } catch (IllegalAccessException ex) {
      ex.printStackTrace();
    } catch (InstantiationException ex) {
      ex.printStackTrace();
    } catch (ClassNotFoundException ex) {
      ex.printStackTrace();
    }
    UIManager.put("swing.boldMetal", Boolean.FALSE);
    
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        new MineSweeper("Minesweeper").setVisible(true);
      }
    });
  }
  
}
