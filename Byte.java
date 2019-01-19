/***
  * Low-leve class for storing a single byte of information,
  * 8-bits.
  */
public class Byte {

  /* Object data fields */
  private int bits[];
 
  /* Primary constructor */
  public Byte() {
    bits = new int[8];
    
    for(int index = 0; index < bits.length; index++) {
      bits[index] = 0;
    }
  }
  
  //returns hex representation of the Byte -laste
  public String hex() {
    
    int pow_value = 1;
    int value     = 0;
    
    for(int index = 0; index < 8; index++) {
      
      if (bits[index] == 1) {
        value += pow_value;
      }
      pow_value *= 2;
    }
    
    return String.format("%02X", value);
  }
  
  //returns binary representation of the byte -laste
  public String binary() {
    
    String result = "";
    
    for(int index = 7; index >= 0; index--) {
      
      if (bits[index] == 1) {
        result += "1";
      } else {
        result += "0";
      }
    }
    
    return result;
  }
  
  public String binary(int high, int low) {
    
    String result = "";
    
    for(int index = high; index >= low; index--) {
      
      if (bits[index] == 1) {
        result += "1";
      } else {
        result += "0";
      }
    }
    
    return result;
  }

  //Takes a decimal int value and stores into the byte -laste
  public void store(int value) throws Exception {
    
    // System.err.println("--" + value + "--");
    if (value < 0 || 255 < value) 
      throw new Exception("Passed value is too large for Byte");

    for(int index = 0; index < 8; index++) {
      bits[index] = value % 2;
      value       = value / 2;
    }
  }
  //takes in a hex string value and stores into the bute -laste
  public void store(String value) throws Exception {
    
    // System.err.println("--" + value + "--");
    if (value.length() != 2) 
      throw new Exception("Passed value is not the right length for Byte");

    int int_value = Integer.parseInt(value, 16);
    
    for(int index = 0; index < 8; index++) {
      bits[index] = int_value % 2;
      int_value   = int_value / 2;
    }
  }

  public static void main(String args[]) {
    
    /*** Examples of usage. ***/
    Byte a = new Byte();
    
    try {
      a.store(0xAA);
    
      System.out.println(a.hex());
      System.out.println(a.binary());
      System.out.println(a.binary(3,0));
      a.store(5);
      System.out.println(a.binary(2,0));
      
      a.store("AB");
      System.out.println(a.binary());
      
      a.store("D9");
      System.out.println(a.binary());
      
      a.store("00");
      System.out.println(a.hex());
      System.out.println(a.binary());
      
    } catch (Exception e) {
      System.out.println(e);
    }
  }

}

