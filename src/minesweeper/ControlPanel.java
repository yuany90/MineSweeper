package minesweeper;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BorderFactory;

public class ControlPanel extends JPanel {
    
  int value;
  private final ImageIcon[] digits = 
  {new ImageIcon(getClass().getResource("/images/0.gif")),
   new ImageIcon(getClass().getResource("/images/1.gif")),
   new ImageIcon(getClass().getResource("/images/2.gif")),
   new ImageIcon(getClass().getResource("/images/3.gif")),
   new ImageIcon(getClass().getResource("/images/4.gif")),
   new ImageIcon(getClass().getResource("/images/5.gif")),
   new ImageIcon(getClass().getResource("/images/6.gif")),
   new ImageIcon(getClass().getResource("/images/7.gif")),
   new ImageIcon(getClass().getResource("/images/8.gif")),
   new ImageIcon(getClass().getResource("/images/9.gif"))
  };
  
  public ControlPanel (int v) {
    setLayout(new GridLayout(1,3));
    value = v;
    draw();
  }
  
  public int getValue() { return value; }
  public void setValue(int v) { 
    value = v; 
    draw();
  }
  
  public void increment() {
    if (value == 999) {
      return;
    }
    value++;
    draw();
  }
  
  public void decrement() {
    if (value == -999) {
      return;
    }
    value--;
    draw();
  }
  
  private void draw() {
    removeAll();
    add(getNumeric(1));
    add(getNumeric(2));
    add(getNumeric(3));
  }

  private JLabel getNumeric(int spot) {
    if (value < 0) {
      if (spot == 1) {
        return new JLabel(new ImageIcon(getClass().getResource("/images/neg.gif")));
      } else if (spot == 2) {
        return getLabel((Math.abs(value)/10)%10);
      } else {
        return getLabel(Math.abs(value)%10);
      }
    } else if (value < 10) {
      if (spot == 1 || spot == 2) {
        return getLabel(0);
      } else {
        return getLabel(value);
      }
    } else if (value >= 10 && value < 100) {
      if (spot == 1) {
        return getLabel(0);
      } else if (spot == 2) {
        return getLabel(value/10);
      } else {
        return getLabel(value%10);
      }
    } else { 
      if (spot == 1) {
        return getLabel(value/100);
      } else if (spot == 2) {
        return getLabel((value/10)%10);
      } else {
        return getLabel(value%10);
      }
    }
  }
  
  private JLabel getLabel(int val) {
      if(val >=0 && val <= 9)
          return new JLabel(digits[val]);
      return null;
  }
}
