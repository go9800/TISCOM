package Disassembler;

import java.util.Arrays;
import java.util.Hashtable;


public class DisAssemble {

    Hashtable<String, Integer> OpCodeInfo;

    Object [] fastMcDecode ;

    public Microcode[] RomListing;
    int RomSize;
    int LabelNumber;

    int [] romIndex = {0xFFFF} ;
    String [] romIndexTag = {""} ;
    
    boolean romTag = true ;
    
    // -------------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------------
    DisAssemble( int size ){
    }
    // -------------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------------
    String disasmGetRomTag( int addr ){
        if(!romTag)return "" ;
	for( int i = 0 ; i < romIndex.length ; i++){
            if( addr < romIndex[i])return romIndexTag[i];
        }
        return "" ;
    }
    // -------------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------------
    void disasmSetRomTags(int[] ra,String [] ts){
	romIndex = ra ;
	romIndexTag = ts ;
    }
    // -------------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------------
      
    String Mnemonic(short addr) {
        return "XXXX" ;
    }
    // -------------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------------
    String DisAssembleIt(short opcode) {
        String mnemonic = "";
        return mnemonic ;
    }
    // -------------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------------
    public void SetLabel(int index, String label) {

    }

    // -------------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------------
    void DisAssembleLabelsO() {
    }
    // -------------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------------

    void DisAssembleLabels() {
        System.out.println("DisAssembleLabels");
        LabelNumber = 1;
        short i;



        short[] LabelIndex = new short[RomSize];

        System.out.println("DisAssembleLables "+RomSize);

        for (i = 0; i < RomSize; i++) {
            LabelIndex[i] = -1;
        }

        for (i = 0; i < RomSize; i++) {
            int index = RomListing[i].getAddr2();
            if (index != -1) {
                if( index >= RomSize || index < 0 ){
                    System.err.println("Index: "+myStatics.Hex0(i,4)+" "+myStatics.Hex0(index,4));
                }
                else{
                    LabelIndex[index] = 0;
                }
            }
        }
        for (i = 0; i < RomSize; i++) {
            if (LabelIndex[i] == 0) {
                LabelIndex[i] = (short) LabelNumber++;
            }
        }

        for (i = 0; i < RomSize; i++) {
            // System.out.println("IIII "+myStatics.Hex0(i,4));
            short a2 = RomListing[i].getAddr2();

            if( a2 >= RomSize ){
                System.out.println("IIII "+myStatics.Hex0(i,4)+" "+myStatics.Hex0(a2,4));
            }  
            else if (a2 != -1) {
                short lx = RomListing[a2].getLabel();

                if (lx == -1) {
                    lx = LabelIndex[a2];
                }// (short)LabelNumber++ ;
                RomListing[a2].setLabel(lx);
                RomListing[i].setLabel2(lx);
            }
        }

        SetLabel(0, "Start:::");

    }
    // -------------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------------
    void DumpLabelList() {
        myStatics.dbgPrintln("\n\n");
        int LabelList[][] = new int[LabelNumber][];

        for (int l = 1; l < LabelNumber; l++) {
            int cnt = 0;

            for (int j = 0; j < RomSize; j++) {
                if (RomListing[j].getLabel2() == l) {
                    cnt++;
                }
            }
            LabelList[l] = new int[cnt + 1];
            for (int j = 0; j < RomSize; j++) {
                if (RomListing[j].getLabel() == l) {
                    LabelList[l][0] = j;
                }
            }
            cnt = 1;
            for (int j = 0; j < RomSize; j++) {
                if (RomListing[j].getLabel2() == l) {
                    LabelList[l][cnt++] = j;
                }
            }
        }
        for (int l = 1; l < LabelNumber; l++) {
          
            String lNew = RomListing[LabelList[l][0]].getLabelS();
            //System.err.println(lNew);
            while( lNew.length() < 10 )lNew += " ";

            //String s = "" + l + ":   ";
            //s = "  L___________".substring(0, 12 - s.length()) + s;
            String s = lNew ;
            String sa = Integer.toHexString(LabelList[l][0]) + "   ";

            sa = "000000000000".substring(0, 7 - sa.length()) + sa;
            myStatics.dbgPrint(s + "  ");
            myStatics.dbgPrint(sa + " : ");

            for (int j = 1; j < LabelList[l].length; j++) {
                String lh = Integer.toHexString(LabelList[l][j]);

                myStatics.dbgPrint(
                        "000000000000".substring(0, 4 - lh.length()) + lh + " ");
            }
            myStatics.dbgPrintln("");
        }
        myStatics.dbgPrintln("\n\n");
    }

    // -------------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------------
    void DumpOpCodes() {
        myStatics.dbgPrintln("\r\n\r\n");
        String[] keys = (String[]) OpCodeInfo.keySet().toArray(new String[0]);

        Arrays.sort(keys);
        for (String key : keys) {
            String ks = key;

            while (ks.length() < 40) {
                ks += " ";
            }
            myStatics.dbgPrintln(ks + " : " + OpCodeInfo.get(key));

        }
        myStatics.dbgPrintln("");
        myStatics.dbgPrintln("\n");
    }

    void DisAssembleIt(short addr, short opcode) {
    }
}
