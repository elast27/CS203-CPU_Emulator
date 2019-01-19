public class ALU{

    private int wordsize;
    private Bus source_A;
    private Register source_B;
    private Register dest_C;

    //flags
    Register     Z,C,N,V;

    public ALU(int wordSize){

        wordsize = wordSize;

        Z = new Register(1);
        C = new Register(1);
        N = new Register(1);
        V = new Register(1);
    }

    public void set_source_A(Bus a){
        source_A = a;
    }

    public void set_source_B(Register b){
        source_B = b;
    }

    public void set_dest_C(Register c){
        dest_C = c;
    }

    public void add(){
        String s1 = source_A.binary();
        String s2 = source_B.binary();
        String res = "";
        if(s1.length() == s2.length()){
            boolean carry = false;
            for(int cnt = s1.length()-1; cnt > -1; cnt--){
                if((s1.charAt(cnt)==s2.charAt(cnt)) && (s1.charAt(cnt)=='0')){
                    if(carry){
                        res = "1" + res;
                        carry = false;
                    }
                    else{
                        res = "0" + res;
                        carry = false;
                    }
                }
                else if((s1.charAt(cnt)==s2.charAt(cnt)) && (s1.charAt(cnt)=='1')){
                    if(carry){
                        res = "1" + res;
                        carry = true;
                    }
                    else{
                        res = "0" + res;
                        carry = true;
                    }
                }
                else{
                    if(carry){
                        res = "0" + res;
                        carry = true;
                    }
                    else{
                        res = "1" + res;
                        carry = false;
                    }
                }
            }
            int result = Integer.parseInt(res,2);
            dest_C.store(result);
        }
    }

    public void addS(){
        String s1 = source_A.binary();
        String s2 = source_B.binary();
        String res = "";
        if(s1.length() == s2.length()){
            boolean carry = false;
            int s1Sign = Integer.parseInt(s1.substring(0,1));
            int s2Sign = Integer.parseInt(s2.substring(0,1));
            for(int cnt = s1.length()-1; cnt > -1; cnt--){
                if((s1.charAt(cnt)==s2.charAt(cnt)) && (s1.charAt(cnt)=='0')){
                    if(carry){
                        res = "1" + res;
                        carry = false;
                    }
                    else res = "0" + res;
                }
                else if((s1.charAt(cnt)==s2.charAt(cnt)) && (s1.charAt(cnt)=='1')){
                    if(carry) res = "1" + res;
                    else{
                        res = "0" + res;
                        carry = true;
                    }
                }
                else{
                    if(carry) res = "0" + res;
                    else{
                        res = "1" + res;
                        carry = false;
                    }
                }
            }
            int intres = Integer.parseInt(res, 2);
            if(intres < 0) N.store(1);
            if(intres == 0) Z.store(1);
            if(carry) C.store(1);
            int resSign = Integer.parseInt(res.substring(0,1));
            if((s1Sign == s2Sign) && s1Sign != resSign) V.store(1);
            dest_C.store(intres);
        }
    }

    public void and(){
        String s1 = source_A.binary();
        String s2 = source_B.binary();
        String res = "";
        if (s1.length() == s2.length()){
            for(int cnt = s1.length()-1; cnt > -1; cnt--){
                if(s1.charAt(cnt)==s2.charAt(cnt)) res = (s1.charAt(cnt))+res;
                else res = "0"+res;
            }
            int result = Integer.parseInt(res, 2);
            dest_C.store(result);
        }
    }

    public void or(){
        String s1 = source_A.binary();
        String s2 = source_B.binary();
        String res = "";
        if (s1.length() == s2.length()){
            for(int cnt = s1.length()-1; cnt > -1; cnt--){
                if(s1.charAt(cnt)== '1') res = "1"+res;
                else if (s2.charAt(cnt)== '1') res = "1"+res;
                else res = "0"+res;
            }
            int result = Integer.parseInt(res, 2);
            dest_C.store(result);
        }
    }

    public void xor(){
        String s1 = source_A.binary();
        String s2 = source_B.binary();
        String res = "";
        if (s1.length() == s2.length()){
            for(int cnt = s1.length()-1; cnt > -1; cnt--){
                if(s1.charAt(cnt)== s2.charAt(cnt)) res = "0"+res;
                else res = "1"+res;
            }
            int result = Integer.parseInt(res, 2);
            dest_C.store(result);
        }
    }
    
    public void lsl(){
        String value = source_B.binary();
        int offset = Integer.parseInt(source_A.binary(),2);
        String res = "";
        for(int i = 0; i < offset; i++){
            res+="0";
        }
        res = value + res;
        String finalres = "";
        for(int i = res.length()-1; i > res.length()-17; i--){
            finalres = res.charAt(i) + finalres;
        }
        int result = Integer.parseInt(finalres,2);
        dest_C.store(result);
    }
    public void lsr(){
        String value = source_B.binary();
        int offset = Integer.parseInt(source_A.binary(),2);
        String res = value;
        for(int i = 0; i < offset; i++){
            res = res.substring(0,res.length()-1);
            res = "1"+res;
        }
        int result = Integer.parseInt(res,2);
        dest_C.store(result);
    }
}