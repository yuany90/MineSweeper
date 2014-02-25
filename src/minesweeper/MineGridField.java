package minesweeper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

public class MineGridField extends JButton {
  private static final Dimension fieldSize = new Dimension(16, 16);
  private static final Border fieldBorderDefault = BorderFactory.createRaisedBevelBorder();
  private static final Border fieldBorderPressed = BorderFactory.createMatteBorder(1, 1, 0, 0, Color.GRAY);
  private static final Color fieldColor = Color.LIGHT_GRAY;
  private final ImageIcon blackMine = new ImageIcon(getClass().getResource("/images/blackMine.gif"));
  private final ImageIcon blackMineFail = new ImageIcon(getClass().getResource("/images/blackMineFail.gif"));
  private final ImageIcon flagImage = new ImageIcon(getClass().getResource("/images/flagImage.gif"));
  private final ImageIcon questionImage = new ImageIcon(getClass().getResource("/images/questionImage.gif"));
  private final ImageIcon[] fields = {
      null,
      new ImageIcon(getClass().getResource("/images/oneField.gif")),
      new ImageIcon(getClass().getResource("/images/twoField.gif")),
      new ImageIcon(getClass().getResource("/images/threeField.gif")),
      new ImageIcon(getClass().getResource("/images/fourField.gif")),
      new ImageIcon(getClass().getResource("/images/fiveField.gif")),
      new ImageIcon(getClass().getResource("/images/sixField.gif")),
      new ImageIcon(getClass().getResource("/images/sevenField.gif")),
      new ImageIcon(getClass().getResource("/images/eightField.gif"))
  };
  private MineSweeper parent;
  private boolean mine = false;
  private boolean flag = false;
  private boolean question = false;
  private int row;
  private int col;
  private int value = 0;
  private boolean leftClick = false;
  private boolean rightClick = false;
  
  public MineGridField(int r, int c) {
    row = r;
    col = c;
    setPreferredSize(fieldSize);
    setBorder(fieldBorderDefault);
    setBackground(fieldColor);
     addMouseListener(new MouseAdapter(){
      @Override
      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
          leftClick = true;
        } else if (SwingUtilities.isRightMouseButton(e)) {
          rightClick = true;
        }
      }
      
      @Override
      public void mouseReleased(MouseEvent e) {
        fieldClicked(leftClick, rightClick);
        leftClick = false;
        rightClick = false;
      }
    });
  }
  public void setParent(MineSweeper p){
      parent = p;
  }
  public int getValue() { return value; }
  public void setValue(int v) { value = v; }
  public boolean isMine() { return mine; }
  public void setMine(boolean m) { mine = m; }
  public boolean isFlagged() { return flag; }
  public void setFlag(boolean f) { flag = f; }
  public boolean isQuestion() { return question; }
  
  public void showMinePicture() {
    setIcon(blackMine);
    setDisabledIcon(blackMine);
    setBorder(fieldBorderPressed);
  }
  
  public void showMineFailPicture() {
    setIcon(blackMineFail);
    setDisabledIcon(blackMineFail);
    setBorder(fieldBorderPressed);
  }
  
  public void showFieldPicture(){
    ImageIcon img = fields[value];
    setIcon(img);
    setDisabledIcon(img);
  }
  
  public void flag(){
     setIcon(flagImage);
     if(isMine()){
         setDisabledIcon(flagImage);
     }
     flag = true;
     question = false;
  }
  
  public void question(){
     setIcon(questionImage);
     flag = false;
     question = true;
  }
  public void fieldClicked(boolean left, boolean right) {
    if (isEnabled()) {
      if (left && !right) {
        parent.startTimer();
        if (flag) {
          return;
        }
        if (mine) {
          setBackground(Color.RED);
          showMinePicture();
          parent.gameOver(); 
          return;
        } else if(value == 0){
          setEnabled(false);
          setIcon(null);
          setDisabledIcon(null);
          parent.revealNeighbors(row, col);
        } else{
          showFieldPicture();
        }
        setBorder(fieldBorderPressed);
        setEnabled(false);
        if(parent.minesPanel.value == 0)
        parent.checkVictory(); 
      } else if (!left && right) {
        if (flag) {
          question();
          parent.minesPanel.increment();
        } else if (question) {
          setIcon(null);
          question = false;
        } else {
          flag();
          parent.minesPanel.decrement();
          if(parent.minesPanel.value == 0)
          parent.checkVictory(); 
        }
      }
    } else {
      if (left && right) {
        parent.revealNeighbors(row, col, value);
      }
    }
  }
  
}
