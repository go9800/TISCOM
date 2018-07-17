package Disassembler;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;


// ----------------------------------------------------------------------------------------------------------------------------
class myStatics {
    static BufferedWriter debugOut = null;
    static BufferedWriter disasmOut = null;
    static int scaleFactor = 100;
    static boolean doTrace = true;
    static boolean doDebug = true;
    static boolean doBreakpoint = true;
    static String[] myArgs = null;

    static boolean dbgPreg  = false ;
    static boolean dbgPrt   = false ;
    static boolean dbgMem   = false ;
    static boolean dbgRom   = false ;
    static boolean dbgKeys  = false ;

    static List<String> debugList = null;

    public static String [] appendString( String [] lin , String l ){
           String [] a = new String[lin.length+1] ;
           int i ;
           for( i = 0 ; i < lin.length ; i++)a[i] = lin[i] ;
           a[i] = l ;
           return a ;  
    }  

    public static boolean ArgsBoolean(String[] args, String s) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean ArgsBoolean(String s) {
        for (int i = 0; i < myArgs.length; i++) {
            if (myArgs[i].equals(s)) {
                return true;
            }
        }
        return false;
    }

    public static void setArgs(String[] args) {
        myArgs = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            myArgs[i] = new String(args[i]);
        }       
    }

    public static int ArgsIntVal(String s) {
        for (int i = 0; i < myArgs.length; i++) {
            if (myArgs[i].equals(s)) {
                if (i + 1 == myArgs.length) {
                    return -1;
                }
                return Integer.parseInt(myArgs[i + 1]);
            }
        }
        return -1;

    }

    public static int ArgsIntVal(String[] args, String s) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(s)) {
                if (i + 1 == args.length) {
                    return -1;
                }
                return Integer.parseInt(args[i + 1]);
            }
        }
        return -1;

    }

    public static String ArgsString(String s) {
        for (int i = 0; myArgs != null && i < myArgs.length; i++) {
            if (myArgs[i].equals(s)) {
                if (i + 1 == myArgs.length) {
                    return "";
                }
                return myArgs[i + 1];
            }
        }
        return "";
    }

    public static String ArgsString(String[] args, String s) {
        for (int i = 0; args != null && i < args.length; i++) {
            if (args[i].equals(s)) {
                if (i + 1 == args.length) {
                    return "";
                }
                return args[i + 1];
            }
        }
        return "";
    }

    public static boolean ArgsStringFlag(String[] args, String s, String f) {
        String x = myStatics.ArgsString(args, s);

        if (x.indexOf(f) >= 0) {
            return true;
        }
        return false;
    }

    public static boolean ArgsStringFlag(String s, String f) {
        String x = myStatics.ArgsString(s);

        if (x.indexOf(f) >= 0) {
            return true;
        }
        return false;
    }

    public static String[] ArgsStringM(String[] args, String s) {
        String sL = "";

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(s)) {
                if (i + 1 == args.length) {
                    break;
                }
                sL += " " + args[i + 1];
            }
        }
        return sL.split(" ");

    }

    public static String[] ArgsStringM(String s) {
        String sL = "";

        for (int i = 0; i < myArgs.length; i++) {
            if (myArgs[i].equals(s)) {
                if (i + 1 == myArgs.length) {
                    break;
                }
                sL += " " + myArgs[i + 1];
            }
        }
        return sL.split(" ");

    }

    public static Color getColor(String s) {
        s = s.replace(" ", "");
        String[] sl = s.split(",");
        int r = 0;    
        int g = 0;
        int b = 0;

        try {
            r = Integer.parseInt(sl[0], 10);
            g = Integer.parseInt(sl[1], 10);
            b = Integer.parseInt(sl[2], 10);
        } catch (Exception e) {
            System.err.println("illegal color value: " + s);
        }
        return new Color(r, g, b);
    }

    public static boolean allowDebug() {
        return doDebug;
    }

    public static boolean allowDisasm() {
        return true;
    }

    public static boolean allowInternal() {
        return true;
    }

    public static boolean allowMemory() {
        return true;
    }

    public static boolean allowSingleStep() {
        return true;
    }

    public static boolean allowTrace() {
        return doTrace;
    }

    public static boolean allowBreakpoint() {
        return doBreakpoint;
    }

    public static void appendToFile(String fname, String line, boolean append) {
        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new FileWriter(fname, append));
            bw.write(line);
            bw.newLine();
            bw.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally { // always close the file
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ioe2) {// just ignore it
                }
            }
        }
    }

    public static String BitList(int val) {
        String s = "";

        for (int i = 0; i < 16; i++) {
            if ((val & (1 << ((i + 15) & 0xF))) != 0) {
                s += i + " ";
            }
        }
        return s;
    }

    public static String BitListX(short val, int ll) {
        String s = "";
        for (int i = ll-1; i >= 0; i--) {
            if ((val & (1 << i)) != 0) {
                s += "1";
            }else{
                s += "0";
            }  
        }
        return s ;
    }    
    public static String BitList2(short val, int ll) {
        String s = "";

        for (int i = ll-1; i >= 0; i--) {
            if ((val & (1 << ((i + ll-1) & 0xF))) != 0) {
                s += "1";
            } else {
                s += "0";
            }
        }
        return s;
    }
    public static String BitList2(short val) {
        return BitList2(val,16);
    }

    public static String BitList3(short val) {
        String s = "";

        for (int i = 15; i >= 0; i--) {
            if ((val & (1 << i)) != 0) {
                s += "1";
            } else {
                s += "0";
            }
        }
        return s;
    }

    public static String bitMask(short val, int mask) {
        return bitMask(val,mask,13);
    }
    public static String bitMask(short val, int mask, int len) {
        String bitm = "";

        for (int i = len-1; i >= 0; i--) {
            bitm += (val & (1 << i)) == 0 ? "0" : "1";
            if ((mask & (1 << i)) != 0) {
                bitm += " ";
            }
        }
        while (bitm.length() < 20) {
            bitm += " ";
        }
        return bitm;
    }

    public static byte[][] ccValues(String[] mcf) {
        return null;
    }

    public static void closeDebugFile() {
        try {
            if (debugOut != null) {
                debugOut.close();
            } else {
                System.out.println("NO TRACEFILE");
            }
        } catch (Exception e) {
            System.err.println("Error : " + e.getMessage());
        }
        debugOut = null;
    }

    public static String ConstTag(String s) {
        if ("230258509299400c".equals(s)) {
            return "0x00   : ln(10.0)       ";
        }
        if ("0693147180559945".equals(s)) {
            return "0x01   : ln(2.0)        ";
        }
        if ("0095310179804325".equals(s)) {
            return "0x02   : ln(1.1)        ";
        }
        if ("0009950330853168".equals(s)) {
            return "0x03   : ln(1.01)       ";
        }
        if ("0000999500333084".equals(s)) {
            return "0x04   : ln(1.001)      ";
        }
        if ("0000099995000333".equals(s)) {
            return "0x05   : ln(1.0001)     ";
        }
        if ("0000009999950000".equals(s)) {
            return "0x06   : ln(1.00001)    ";
        }
        if ("0000000999999500".equals(s)) {
            return "0x07   : ln(1.000001)   ";
        }

        if ("0785398163397450".equals(s)) {
            return "0x10   : atan(1.0)      ";
        }
        if ("0099668652491200".equals(s)) {
            return "0x11   : atan(0.1)      ";
        }
        if ("0009999666686670".equals(s)) {
            return "0x12   : atan(0.01)     ";
        }
        if ("0000999999666667".equals(s)) {
            return "0x13   : atan(0.001)    ";
        }
        if ("0000099999999667".equals(s)) {
            return "0x14   : atan(0.0001)   ";
        }
        if ("572957795130801c".equals(s)) {
            return "0x17   : 180.0/Pi       ";
        }
        if ("157079632679501c".equals(s)) {
            return "0x15   : Pi/2.0         ";
        }
        if ("314159265359000c".equals(s)) {
            return "0x16   : Pi             ";
        }
        return "unknown Constant!!";
    }

    public static void dbgPrint(String s) {
        try {
            if (debugOut != null) {
                debugOut.write(s);
            } // if( debugList != null ){
            // System.out.println("debugList size : "+debugList.size());
            // debugList.add(s);
            // }
            else {
                System.out.print(s);
            }

        } catch (Exception e) {
            System.err.println("Error : " + e.getMessage());
        }
        // System.out.print(s);
    }

    public static void dbgPrintln(String s) {
        dbgPrint(s + "\n");
    }

    public static String Dec(int val, int len) {
        String s = "" + val;

        while (s.length() < len) {
            s = " " + s;
        }
        return s;
    }

    public static String Dec0(int val, int len) {
        String s = "" + val;

        while (s.length() < len) {
            s = "0" + s;
        }
        return s;
    }

    public static boolean doDebugWindow() {
        return true;
    }

    public static String Dump(BCD_Register val) {
        String s = "";

        for (int i = val.BcdDigits.length-1; i >= 0; i--) {
            s += Hex0(val.BcdDigits[i] & 0x0F, 1);
        }
        return s;
    }

    public static String Dump(byte[] val, int lx) {
        String s = "";

        for (int i = 0; i <lx; i++) {
            s += Hex0(val[i],2)+" ";
        }
        return s;
    }


    public static String Dump(byte[] val) {
        String s = "";

        for (int i = 15; i >= 0; i--) {
            if (val[i] > 15) {
                s += "X";
            } else {
                s += Hex0(val[i] & 0xF, 1);
            }
        }
        return s;
    }

    public static byte getDigit(String s, int i) {
        return (byte) Integer.parseInt(s.substring(i, i + 1), 16);
    }

    public static int getScale() {
        return scaleFactor;
    }

    public static String Hex(int val, int len) {
        String s = Integer.toHexString(val);

        while (s.length() < len) {
            s = " " + s;
        }
        return s;
    }

    public static String Hex0(int val, int len) {
        String s = Integer.toHexString(val);

        while (s.length() < len) {
            s = "0" + s;
        }
        return s;
    }

    public static String[] mcTag(String mc, String tag) {
        int is = mc.indexOf(tag);

        if (is < 0) {
            return null;
        }
        int ie = mc.indexOf("}", is);

        if (ie < 0) {
            return null;
        }
        return mc.substring(is + tag.length(), ie).replace(" ", "").replace("\n", "").split(
                ",");
    }

    public static short[] mcValues(String[] mcf) {
        short[] mc = new short[mcf.length];

        for (int i = 0; i < mcf.length; i++) {
            mc[i] = (short) Integer.parseInt(mcf[i].substring(2), 16);
        }
        return mc;
    }

    public static void openDebugFile(String filePath)throws java.io.IOException {
        if (debugOut != null) {
            debugOut.close();
        }
        FileWriter fstream = new FileWriter(filePath);

        debugOut = new BufferedWriter(fstream);
        debugList = new ArrayList<String>();
    }

    public static String pBuffer(byte[] val) {
        String s = "";

        for (int i = 0; i < 20; i++) {
            s += Hex0(val[i] & 0x0F, 2) + " ";
        }
        return s;
    }

    public static String Prt(byte val) {
        switch (val) {
        case 0:
            return " ";

        case 1:
            return "0";

        case 2:
            return "1";

        case 3:
            return "2";

        case 4:
            return "3";

        case 5:
            return "4";

        case 6:
            return "5";

        case 7:
            return "6";

        case 8:
            return "7";

        case 9:
            return "8";

        case 10:
            return "9";

        case 11:
            return "A";

        case 12:
            return "B";

        case 13:
            return "C";

        case 14:
            return "D";

        case 15:
            return "E";

        case 16:
            return "-";

        case 17:
            return "F";

        case 18:
            return "G";

        case 19:
            return "H";

        case 20:
            return "I";

        case 21:
            return "J";

        case 22:
            return "K";

        case 23:
            return "L";

        case 24:
            return "M";

        case 25:
            return "N";

        case 26:
            return "O";

        case 27:
            return "P";

        case 28:
            return "Q";

        case 29:
            return "R";

        case 30:
            return "S";

        case 31:
            return "T";

        case 32:
            return ".";

        case 33:
            return "U";

        case 34:
            return "V";

        case 35:
            return "W";

        case 36:
            return "X";

        case 37:
            return "Y";

        case 38:
            return "Z";

        case 39:
            return "+";

        case 40:
            return "x";

        case 41:
            return "*";

        case 42:
            return "\u221a"; // Wurzel

        case 43:
            return "\u03c0"; // pi

        case 44:
            return "e"; // e

        case 45:
            return "(";

        case 46:
            return ")";

        case 47:
            return ",";

        case 48:
            return "\u2191"; // pfeil hoch

        case 49:
            return "%";

        case 50:
            return "\u25ca"; // <>

        case 51:
            return "/";

        case 52:
            return "=";

        case 53:
            return "'";

        case 54:
            return "\u00d7";

        case 55:
            return "x"; // x dach

        case 56:
            return "\u00b2"; // 2hoch

        case 57:
            return "?"; // ?

        case 58:
            return "\u00f7"; // div

        case 59:
            return "!";

        case 60:
            return "\u2551";

        case 61:
            return "\u25b2";

        case 62:
            return "\u03a0";

        case 63:
            return "\u03a3";

        }
        return myStatics.Dec(val, 3) + " ";
    }
    //-------------------------------------------------------------------------------------------
    public static byte[] readFileAsByte(String filePath)
        throws java.io.IOException {
        byte[] buffer = new byte[(int) new File(filePath).length()];
        FileInputStream f = new FileInputStream(filePath);

        f.read(buffer);
        f.close();
        return buffer;
    }

    public static String readFully(URL url) throws IOException {
        return readFully(url.openStream());
    }

    public static String readFully(Reader reader) throws IOException {
        char[] arr = new char[8*1024]; // 8K at a time
       StringBuffer buf = new StringBuffer();
      int numChars;

      while ((numChars = reader.read(arr, 0, arr.length)) > 0) {
          buf.append(arr, 0, numChars);
      }

       return buf.toString();
    } 
    
    public static String getTextFile(String fname) {
    try {
	 
	  InputStream inputStream = 
	    myStatics.class.getClassLoader().getResourceAsStream(fname);
		return readFully(inputStream);


	} catch (IOException e) {
		e.printStackTrace();
	}
       return "" ;
    }
    
    public static String readFully(InputStream stream) throws IOException {
        return readFully(new InputStreamReader(stream));
    }

    public static String readFileAsString(String filePath)
        throws java.io.IOException {
        byte[] buffer = new byte[(int) new File(filePath).length()];
        FileInputStream f = new FileInputStream(filePath);

        f.read(buffer);
        f.close();
        return new String(buffer);
    }

    public static String Reg(byte[] val) {
        String s = "";

        for (int i = 15; i >= 0; i--) {
            s += Hex0(val[i] & 0x0F, 1);
        }
        return s;
    }

    public static String replaceCharAt(String s, int pos, String c) {
        return s.substring(0, pos) + c + s.substring(pos + 1);
    }

    public static int setScale(int val) {
        scaleFactor = val;
        return scaleFactor;
    }

    public static int Swap(int val) {
        int nval = (val & 0xF) << 4;

        nval += (val & 0xf0) >> 4;
        return nval;
    }

    public static void writeFileAsByte(String filePath, byte[] data)
        throws java.io.IOException {
        FileOutputStream fos = new FileOutputStream(filePath);

        fos.write(data);
        fos.close();
    }

    public static int writeStringAsFile(String filePath, String data)
        throws java.io.IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(filePath));

        out.write(data);
        out.close();
        return 0;
    }

}
