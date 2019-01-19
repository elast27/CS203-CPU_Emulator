/***
 * The hardware controler for driving data path.
 */
public class Controler {

    /* Object data fields */
    int memory_size;
    RTN control_memory[];
    CPU data_path;

    int current_entry;

    /* Advancement Types */
    private final int NEXT    = 0;
    private final int START   = 1;
    private final int CMDNAME = 2;
    private final int UNKNOWN = 3;

    /* Primary constructor */
    public Controler(CPU cpu) {

        memory_size = 512;
        data_path   = cpu;

        control_memory = new RTN[memory_size];
        load_cntl_code_1();

        reset();
    }

    /**
     * Command called from CPU increment clock event.
     * Uses a switch to advance the simulation -laste
     */
    public void increment_clock() {

        System.err.println(control_memory[current_entry]);
        /* Executes the current instruction -laste */
        control_memory[current_entry].execute();

        switch (control_memory[current_entry].advance()) {
            //If advance value is NEXT increment position in control_memory -laste
            case NEXT: {
                current_entry++;
                break;
            }
            //If advance value is START go back to the beginning of the control_memory -laste
            case START: {
                current_entry = 0;
                break;
            }
            //Fetch2:
            case CMDNAME: {
                try {
                    //data_path is a CPU, IR is the instruction register -laste
                    //sets current_entry to the top 4 bits of the instruction register +1 and *5 -laste
                    current_entry = (data_path.IR.decimal(15,12) + 1) * 5;
                    System.err.println("--------");
                    System.err.println(data_path.IR.binary());
                    System.err.println(current_entry);
                    System.err.println("--------");
                } catch (Exception e) {
                    System.err.println("In Controler:increment_clock");
                    System.err.println(e);
                }

                break;
            }
        }
    }

    public String current_rtn() {
        return control_memory[current_entry].toString();
    }

    /* resets the the simulation by returning to the beginning -laste */
    public void reset() {
        current_entry = 0;
    }

    /**
     * initializes the control code array for the simulation and
     * changes each RTN into the correct command type -laste
     */
    public void load_cntl_code_1() {

        control_memory[0] = new Fetch0();
        control_memory[1] = new Fetch1();
        control_memory[2] = new Fetch2();
        control_memory[5] = new NOP(); //0
        control_memory[10] = new LOADI0(); //1
        control_memory[15] = new ADD0();//2
        control_memory[16] = new ADD1();
        control_memory[17] = new ADD2();
        control_memory[20] = new SUB0();//3
        control_memory[21] = new SUB1();
        control_memory[22] = new SUB2();
        control_memory[25] = new ADDI0();//4
        control_memory[26] = new ADDI1();
        control_memory[27] = new ADDI2();
        control_memory[30] = new SUBI0();//5
        control_memory[31] = new SUBI1();
        control_memory[32] = new SUBI2();
        control_memory[35] = new ADDS0();//6
        control_memory[36] = new ADDS1();
        control_memory[37] = new ADDS2();
        control_memory[40] = new SUBS0();//7
        control_memory[41] = new SUBS1();
        control_memory[42] = new SUBS2();
        control_memory[45] = new AND0();//8
        control_memory[46] = new AND1();
        control_memory[47] = new AND2();
        control_memory[50] = new OR0();//9
        control_memory[51] = new OR1();
        control_memory[52] = new OR2();
        control_memory[55] = new XOR0();//A
        control_memory[56] = new XOR1();
        control_memory[57] = new XOR2();
        control_memory[60] = new LSL0();//B
        control_memory[61] = new LSL1();
        control_memory[62] = new LSL2();
        control_memory[65] = new LSR0();//C
        control_memory[66] = new LSR1();
        control_memory[67] = new LSR2();
        control_memory[70] = new B();//D
        control_memory[75] = new LDUR();//E
        control_memory[80] = new STUR0();//F
        control_memory[81] = new STUR1();
    }

    /**
     * Parent RTN for ease of creating Fetch0, Fetch1 and Fetch2 -laste
     */
    public class RTN {

        public String toString() {
            return new String("RTN parent toString method.");
        }

        public void execute() {
            System.err.println("You are executing the RTN parent.");
        }

        public int advance() {
            return UNKNOWN;
        }
    }

    /**** FETCH: 0 ****/
    /**
     * Fetch0 stores the value of the program counter to the bus
     * and loads it into the memory address register when executed -laste
     */
    // The RTN for fetching the instruction.
    // Should always be in location zero of the
    // control memory.
    public class Fetch0 extends RTN {
        public String toString() {
            return new String("Fetch0");
        }

        public void execute() {
            data_path.PC.store();
            data_path.MA.load();
        }

        public int advance() {
            return NEXT;
        }

    }

    /**
     * Fetch1 when executed will increment the value stored
     * in the PC twice and stores the value in the address register
     * to the data register -laste
     */
    public class Fetch1 extends RTN {

        public String toString() {
            return new String("Fetch1");
        }

        public void execute() {
            data_path.PC.increment(2);
            data_path.main_memory.memory_load();
        }

        public int advance() {
            return NEXT;
        }
    }

    /** 
     * Fetch2 when executed will store the value of the MD register
     * to the bus and loads it into the IR so it is ready to execute
     * the next instruction -laste
     */
    public class Fetch2 extends RTN {

        public String toString() {
            return new String("Fetch2");
        }

        public void execute() {
            data_path.MD.store();
            data_path.IR.load();
        }

        public int advance() {
            return CMDNAME;
        }
    }

    /**** No Operation: 1 ****/
    // The RTN for doing nothing with the processor.
    // Should always be in location 1 of the
    // control memory.
    public class NOP extends RTN {

        public String toString() {
            return new String("NOP");
        }

        public void execute() {
        }

        public int advance() {
            return START;
        }
    }

    /**** Load Immediate: 2 ****/
    // The RTN for implementing the add immediate operation.
    // Should always be in location 2 of the control memory.
    // Will pull the destination register immediate from.
    // the IR and store in destation register
    public class LOADI0 extends RTN {

        public String toString() {
            return new String("LOADI0");
        }

        public void execute() {
            // IR representation
            // |1  1|1  0|0      0|
            // |5  2|1  8|7      0|
            // | op |dest| immed  |

            try {
                int source      = data_path.IR.decimal(7, 0);
                int destination = data_path.IR.decimal(11, 8);

                data_path.master_bus.store(source);
                data_path.bank.load(destination);

            } catch (Exception e) {
                System.err.println("In Controler:LOADI0:increment_clock");
                System.err.println(e);
            }
        }

        public int advance() {
            return START;
        }
    }

    public class ADD0 extends RTN {

        public String toString() {
            return new String("ADD0");
        }

        public void execute() {
            try{
                int sourceA = data_path.IR.decimal(3,0);
                data_path.bank.store(sourceA);
                data_path.B.load();
            }catch(Exception e){
                System.err.println("In Controler:ADD0:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return NEXT;
        }
    }
    public class ADD1 extends RTN {

        public String toString() {
            return new String("ADD1");
        }

        public void execute() {
            try{
                int sourceB = data_path.IR.decimal(7,4);
                data_path.bank.store(sourceB);
                data_path.alu.add();
            }catch(Exception e){
                System.err.println("In Controler:ADD1:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return NEXT;
        }
    }
    public class ADD2 extends RTN {

        public String toString() {
            return new String("ADD2");
        }

        public void execute() {
            try{
                data_path.C.store();
                int dest = data_path.IR.decimal(11,8);
                data_path.bank.load(dest);
            }catch(Exception e){
                System.err.println("In Controler:ADD2:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return START;
        }
    }

    public class ADDI0 extends RTN {

        public String toString() {
            return new String("ADDI0");
        }

        public void execute() {
            try{
                int sourceA = data_path.IR.decimal(3,0);
                data_path.B.store(sourceA);
            }catch(Exception e){
                System.err.println("In Controler:ADDI0:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return NEXT;
        }
    }
    public class ADDI1 extends RTN {

        public String toString() {
            return new String("ADDI1");
        }

        public void execute() {
            try{
                int sourceB = data_path.IR.decimal(7,4);
                data_path.bank.store(sourceB);
                data_path.alu.add();
            }catch(Exception e){
                System.err.println("In Controler:ADDI1:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return NEXT;
        }
    }
    public class ADDI2 extends RTN {

        public String toString() {
            return new String("ADDI2");
        }

        public void execute() {
            try{
                data_path.C.store();
                int dest = data_path.IR.decimal(11,8);
                data_path.bank.load(dest);
            }catch(Exception e){
                System.err.println("In Controler:ADDI2:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return START;
        }
    }

    public class ADDS0 extends RTN {

        public String toString() {
            return new String("ADDS0");
        }

        public void execute() {
            try{
                int sourceA = data_path.IR.decimal(3,0);
                data_path.bank.store(sourceA);
                data_path.B.load();
            }catch(Exception e){
                System.err.println("In Controler:ADDS0:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return NEXT;
        }
    }
    public class ADDS1 extends RTN {

        public String toString() {
            return new String("ADDS1");
        }

        public void execute() {
            try{
                int sourceB = data_path.IR.decimal(7,4);
                data_path.bank.store(sourceB);
                data_path.alu.addS();
            }catch(Exception e){
                System.err.println("In Controler:ADDS1:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return NEXT;
        }
    }
    public class ADDS2 extends RTN {

        public String toString() {
            return new String("ADDS2");
        }

        public void execute() {
            try{
                data_path.C.store();
                int dest = data_path.IR.decimal(11,8);
                data_path.bank.load(dest);
            }catch(Exception e){
                System.err.println("In Controler:ADDS2:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return START;
        }
    }

    public class SUB0 extends RTN {
        public String toString() {
            return new String("SUB0");
        }

        public void execute() {
            try{
                int sourceA = data_path.IR.decimal(3,0);
                data_path.bank.store(sourceA);
                data_path.bank.negate(sourceA);
                data_path.bank.store(sourceA);
                data_path.B.load();
            }catch(Exception e){
                System.err.println("In Controler:SUB0:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return NEXT;
        }
    }
    public class SUB1 extends RTN {

        public String toString() {
            return new String("SUB1");
        }

        public void execute() {
            try{
                int sourceB = data_path.IR.decimal(7,4);
                data_path.bank.store(sourceB);
                data_path.alu.add();
            }catch(Exception e){
                System.err.println("In Controler:SUB1:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return NEXT;
        }
    }
    public class SUB2 extends RTN {

        public String toString() {
            return new String("SUB2");
        }

        public void execute() {
            try{
                data_path.C.store();
                int dest = data_path.IR.decimal(11,8);
                data_path.bank.load(dest);
            }catch(Exception e){
                System.err.println("In Controler:SUB2:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return START;
        }
    }

    public class SUBI0 extends RTN {

        public String toString() {
            return new String("SUBI0");
        }

        public void execute() {
            try{
                int sourceA = data_path.IR.decimal(3,0) * -1;
                data_path.B.store(sourceA);
            }catch(Exception e){
                System.err.println("In Controler:SUBI0:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return NEXT;
        }
    }
    public class SUBI1 extends RTN {

        public String toString() {
            return new String("SUBI1");
        }

        public void execute() {
            try{
                int sourceB = data_path.IR.decimal(7,4);
                data_path.bank.store(sourceB);
                data_path.alu.add();
            }catch(Exception e){
                System.err.println("In Controler:SUBI1:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return NEXT;
        }
    }
    public class SUBI2 extends RTN {

        public String toString() {
            return new String("SUBI2");
        }

        public void execute() {
            try{
                data_path.C.store();
                int dest = data_path.IR.decimal(11,8);
                data_path.bank.load(dest);
            }catch(Exception e){
                System.err.println("In Controler:SUBI2:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return START;
        }
    }
    public class SUBS0 extends RTN {

        public String toString() {
            return new String("SUBS0");
        }

        public void execute() {
            try{
                int sourceA = data_path.IR.decimal(3,0);
                data_path.bank.store(sourceA);
                data_path.bank.negate(sourceA);
                data_path.bank.store(sourceA);
                data_path.B.load();
            }catch(Exception e){
                System.err.println("In Controler:SUBS0:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return NEXT;
        }
    }
    public class SUBS1 extends RTN {

        public String toString() {
            return new String("SUBS1");
        }

        public void execute() {
            try{
                int sourceB = data_path.IR.decimal(7,4);
                data_path.bank.store(sourceB);
                data_path.alu.addS();
            }catch(Exception e){
                System.err.println("In Controler:SUBS1:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return NEXT;
        }
    }
    public class SUBS2 extends RTN {

        public String toString() {
            return new String("SUBS2");
        }

        public void execute() {
            try{
                data_path.C.store();
                int dest = data_path.IR.decimal(11,8);
                data_path.bank.load(dest);
            }catch(Exception e){
                System.err.println("In Controler:SUBS2:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return START;
        }
    }

    public class AND0 extends RTN {
        public String toString() {
            return new String("AND0");
        }

        public void execute() {
            try{
                int sourceA = data_path.IR.decimal(3,0);
                data_path.bank.store(sourceA);
                data_path.B.load();
            }catch(Exception e){
                System.err.println("In Controler:AND0:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return NEXT;
        }
    }
    public class AND1 extends RTN {

        public String toString() {
            return new String("AND1");
        }

        public void execute() {
            try{
                int sourceB = data_path.IR.decimal(7,4);
                data_path.bank.store(sourceB);
                data_path.alu.and();
            }catch(Exception e){
                System.err.println("In Controler:AND1:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return NEXT;
        }
    }
    public class AND2 extends RTN {

        public String toString() {
            return new String("AND2");
        }

        public void execute() {
            try{
                data_path.C.store();
                int dest = data_path.IR.decimal(11,8);
                data_path.bank.load(dest);
            }catch(Exception e){
                System.err.println("In Controler:AND2:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return START;
        }
    }

    public class OR0 extends RTN {

        public String toString() {
            return new String("OR0");
        }

        public void execute() {
            try{
                int sourceA = data_path.IR.decimal(3,0);
                data_path.bank.store(sourceA);
                data_path.B.load();
            }catch(Exception e){
                System.err.println("In Controler:OR0:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return NEXT;
        }
    }
    public class OR1 extends RTN {

        public String toString() {
            return new String("OR1");
        }

        public void execute() {
            try{
                int sourceB = data_path.IR.decimal(7,4);
                data_path.bank.store(sourceB);
                data_path.alu.or();
            }catch(Exception e){
                System.err.println("In Controler:OR1:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return NEXT;
        }
    }
    public class OR2 extends RTN {

        public String toString() {
            return new String("OR2");
        }

        public void execute() {
            try{
                data_path.C.store();
                int dest = data_path.IR.decimal(11,8);
                data_path.bank.load(dest);
            }catch(Exception e){
                System.err.println("In Controler:OR2:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return START;
        }
    }

    public class XOR0 extends RTN {

        public String toString() {
            return new String("XOR0");
        }

        public void execute() {
            try{
                int sourceA = data_path.IR.decimal(3,0);
                data_path.bank.store(sourceA);
                data_path.B.load();
            }catch(Exception e){
                System.err.println("In Controler:XOR0:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return NEXT;
        }
    }
    public class XOR1 extends RTN {

        public String toString() {
            return new String("XOR1");
        }

        public void execute() {
            try{
                int sourceB = data_path.IR.decimal(7,4);
                data_path.bank.store(sourceB);
                data_path.alu.xor();
            }catch(Exception e){
                System.err.println("In Controler:XOR1:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return NEXT;
        }
    }
    public class XOR2 extends RTN {

        public String toString() {
            return new String("XOR2");
        }

        public void execute() {
            try{
                data_path.C.store();
                int dest = data_path.IR.decimal(11,8);
                data_path.bank.load(dest);
            }catch(Exception e){
                System.err.println("In Controler:XOR2:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return START;
        }
    }

    public class LSL0 extends RTN {

        public String toString() {
            return new String("LSL0");
        }

        public void execute() {
            try{
                int sourceA = data_path.IR.decimal(7,4);
                data_path.bank.store(sourceA);
                data_path.B.load();

            }catch(Exception e){
                System.err.println("In Controler:LSL0:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return NEXT;
        }
    }
    public class LSL1 extends RTN {

        public String toString() {
            return new String("LSL1");
        }

        public void execute() {
            try{
                int sourceB = data_path.IR.decimal(3,0);
                data_path.master_bus.store(sourceB);
                data_path.alu.lsl();
            }catch(Exception e){
                System.err.println("In Controler:LSL1:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return NEXT;
        }
    }
    public class LSL2 extends RTN {

        public String toString() {
            return new String("LSL2");
        }

        public void execute() {
            try{
                data_path.C.store();
                int dest = data_path.IR.decimal(11,8);
                data_path.bank.load(dest);
            }catch(Exception e){
                System.err.println("In Controler:LSL2:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return START;
        }
    }

    public class LSR0 extends RTN {

        public String toString() {
            return new String("LSR0");
        }

        public void execute() {
            try{
                int sourceA = data_path.IR.decimal(7,4);
                data_path.bank.store(sourceA);
                data_path.B.load();

            }catch(Exception e){
                System.err.println("In Controler:LSR0:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return NEXT;
        }
    }
    public class LSR1 extends RTN {

        public String toString() {
            return new String("LSR1");
        }

        public void execute() {
            try{
                int sourceB = data_path.IR.decimal(3,0);
                data_path.master_bus.store(sourceB);
                data_path.alu.lsr();
            }catch(Exception e){
                System.err.println("In Controler:LSR1:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return NEXT;
        }
    }
    public class LSR2 extends RTN {

        public String toString() {
            return new String("LSR2");
        }

        public void execute() {
            try{
                data_path.C.store();
                int dest = data_path.IR.decimal(11,8);
                data_path.bank.load(dest);
            }catch(Exception e){
                System.err.println("In Controler:LSR2:increment_clock");
                System.err.println(e);
            }

        }

        public int advance() {
            return START;
        }
    }

    public class B extends RTN {
        public String toString() {
            return new String("B");
        }

        public void execute(){
            try{
                String dest = data_path.IR.hex();
                dest = dest.substring(1,4);
                data_path.PC.store(dest);
            } catch(Exception e){
                System.err.println("In Controler:B:increment_clock");
                System.err.println(e);
            }
        }

        public int advance(){
            return START;
        }
    }
    
    public class LDUR extends RTN {
        public String toString() {
            return new String("LDUR");
        }

        public void execute(){
            try{
//                 Unimplemented. Design proposal below does not work
//                 int dest = data_path.IR.decimal(11,8);
//                 String value = data_path.IR.binary(7,0);
//                 data_path.main_memory.setMemoryWord(dest,value);
            } catch(Exception e){
                System.err.println("In Controler:LDUR:increment_clock");
                System.err.println(e);
            }
        }

        public int advance(){
            return NEXT;
        }
    }
    
    public class STUR0 extends RTN {
        public String toString() {
            return new String("STUR0");
        }

        public void execute(){
            try{
                //Unimplemented
            } catch(Exception e){
                System.err.println("In Controler:STUR0:increment_clock");
                System.err.println(e);
            }
        }

        public int advance(){
            return NEXT;
        }
    }
    public class STUR1 extends RTN {
        public String toString() {
            return new String("STUR1");
        }

        public void execute(){
            try{
                //Unimplemented
            } catch(Exception e){
                System.err.println("In Controler:STUR1:increment_clock");
                System.err.println(e);
            }
        }

        public int advance(){
            return START;
        }
    }
}
