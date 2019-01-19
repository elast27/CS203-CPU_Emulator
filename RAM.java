import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Hashtable;
 
import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;

/***
  * Memory simulation, including visual representation.
  */
public class RAM extends JPanel implements ActionListener {

  /* Object data fields */
  protected JButton button;

  protected JTextArea textArea;
  private final static String newline = "\n";

  private Byte memory[];
  private int  size;
  private int  byte_width;
  private int  last_access;
  
  private Register address_register;
  private Register data_register;

  /* Primary constructor */
  public RAM(int sizeValue, int byteWidth) {
    
      super(new GridBagLayout());

      button = new JButton("Refresh");
      button.addActionListener(this);

      textArea = new JTextArea(30, 70);
      textArea.setFont(new Font("Courier", Font.PLAIN, 16));
      textArea.setEditable(false);
      JScrollPane scrollPane = new JScrollPane(textArea);

      //Add Components to this panel.
      GridBagConstraints c = new GridBagConstraints();
      c.gridwidth = GridBagConstraints.REMAINDER;

      c.fill = GridBagConstraints.HORIZONTAL;
      add(button, c);

      c.fill = GridBagConstraints.BOTH;
      c.weightx = 1.0;
      c.weighty = 1.0;
      add(scrollPane, c);

      size        = sizeValue;      // store memory organization
      byte_width  = byteWidth;
      last_access = 0;

      memory     = new Byte[size];  // construct memory

      for(int cnt = 0; cnt < size; cnt++) {
        memory[cnt] = new Byte();
      }

      load_memory("test_file.as");  // load memory file
      refresh_display();            // redraw the display
  }

  public void actionPerformed(ActionEvent evt) {

    if (evt.getActionCommand().equals("Refresh")) {
      refresh_display();
    } else {
      System.err.println ("Unknown action (RAM.java): " + 
                          evt.getActionCommand());
    }
  }

  /**
   * Create the GUI and show it.  For thread safety,
   * this method should be invoked from the
   * event dispatch thread.
   */
  public static RAM createAndShowGUI(int sizeValue, int byteWidth) {
      //Create and set up the window.
      JFrame frame = new JFrame("RAM");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      //Add contents to the window.
      RAM mem = new RAM(sizeValue, byteWidth);
      frame.add(mem);

      //Display the window.
      frame.pack();
      frame.setVisible(true);
      
      return mem;
  }

  /**
   * loads each byte in memory by using the Byte store method
   * to load each string of hex. Initializes the entire memory
   * array -laste
   */
  public void load_memory(String file_name) {

    Scanner sc = null;       // initialize and create scanner mechanism
    String   curr_line;

    try {
      sc = new Scanner(new File(file_name));
    } catch (FileNotFoundException e) {
      System.err.println("Error in load_memory");
      System.err.println(e);
    }

    try {                    // load file into memory
      
      int line_cnt = 0;

      while (sc.hasNextLine()) {
        
        curr_line = sc.nextLine();               // get next line
        // System.out.println(">>>>" + curr_line + "<<<<");
        
        if(curr_line.length() == 0) continue;    // skip blank lines
        
        for(int cnt = 0; cnt <= byte_width; cnt += 2 ) {
          memory[(line_cnt * byte_width) + (cnt / 2)].store(
            String.format("%c%c", 
                          curr_line.charAt(cnt),
                          curr_line.charAt(cnt+1)
            )
          );
          
        }

        line_cnt++;
      }
    } catch (Exception e) {
      System.out.println(e);
    }

    last_access = 0;
  }

  public void refresh_display() {

    // textArea.setText(""); // clears the text
    textArea.setText(build_display()); // stores memory as a string to widget
  }

  public String build_display(){
    
    int    offset = 0;
    String result = "    Address  Hex Display   Binary Display" + newline;

    result +=       "    -------  ------------  --------------" + newline;
    
    for (int cnt = 0; cnt < size; cnt += byte_width) {

      if(cnt == last_access) {                    // add pointer code here
        result += "==> ";
      } else {
        result += "    ";
      }

      result += String.format("0x%05X  ", cnt);   // display address column

      offset = 12 - (byte_width * 2);  // compute offset between hex and bin

      for (int cnt2 = 0; cnt2 < byte_width; cnt2++) { // display hex value
        result += memory[cnt + cnt2].hex();
      }
      
      for (int cnt2 = 0; cnt2 < offset; cnt2++) {     // add offset
        result += " ";
      }

      result += "  ";

      for (int cnt2 = 0; cnt2 < byte_width; cnt2++) { // display binary value
        result += memory[cnt + cnt2].binary() + " ";
      }

      result += newline;
    }
    
    return result;
  }

  //Returns the a word of memory at the given address -laste
  public String getMemoryWord_binary(int address) {
    
    String result = "";

    last_access = address;

    for(int cnt = 0; cnt < byte_width; cnt++) {
      
      result += memory[address + cnt].binary();
    }

    return result;
  }

  public String getMemoryWord_hex(int address) {
    
    String result = "";

    last_access = address;

    for(int cnt = 0; cnt < byte_width; cnt++) {
      
      result += memory[address + cnt].hex();
    }

    return result;
  }

  //Sets a word of memory at a given address with a given value -laste
  public void setMemoryWord(int address, String value) {
    
    last_access = address;

    try {
      for(int cnt = 0; cnt < byte_width; cnt++) {
        memory[address + cnt].store(value.substring((cnt*2), (cnt*2)+1));
      }
    } catch (Exception e) {
      System.err.println("In RAM:setMemoryWord.");
      System.err.println(e);
    }

    refresh_display();
  }

  public void set_address_register(Register reg) {
    address_register = reg;
  }

  public void set_data_register(Register reg) {
    data_register = reg;
  }

  public void memory_load() {
    
    int address = address_register.decimal();
    // System.err.println(address);
    // System.err.println(getMemoryWord(address));
    data_register.store(getMemoryWord_hex(address));
  }

  public void memory_store() {
    int address = address_register.decimal();
    setMemoryWord(address, data_register.hex());
    refresh_display();
  }
  
}

