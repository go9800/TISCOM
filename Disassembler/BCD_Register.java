package Disassembler;

public class BCD_Register {
    byte[] BcdDigits;

    BCD_Register() {
        BcdDigits = new byte[16];
        Clr();
    }
    BCD_Register(int l) {
        BcdDigits = new byte[l];
        Clr();
    }
    void Fill(int val ) {
        for (int i = 0; i < BcdDigits.length ; i++) {
            BcdDigits[i] = (byte)val ;
        }      
    }  
    void Clr() {
        for (int i = 0; i < BcdDigits.length ; i++) {
            BcdDigits[i] = 0;
        }
    }
}
