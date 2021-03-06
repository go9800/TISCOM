package Disassembler;


public class Microcode {

        short OpCode;
        short Address;
        short Label;
        short Label2;
        short Addr2;
        String Mnemonic;
        String LabelS;
        String Label2S;

        Microcode(short addr, short opcode, String mnemo, short to) {
            OpCode = opcode;
            Address = addr;
            Mnemonic = mnemo;
            Label = Label2 = -1;
            Addr2 = to;
            LabelS = Label2S = "            ";
        }

        // -------------------------------------------------------------------------------
        //
        // -------------------------------------------------------------------------------
        public short getAddr2() {
            return Addr2;
        }

        // -------------------------------------------------------------------------------
        //
        // -------------------------------------------------------------------------------
        short getLabel() {
            return Label;
        }

        // -------------------------------------------------------------------------------
        //
        // -------------------------------------------------------------------------------
        short getLabel2() {
            return Label2;
        }

        // -------------------------------------------------------------------------------
        //
        // -------------------------------------------------------------------------------
        String getLabel2S() {
            return Label2S;
        }

        // -------------------------------------------------------------------------------
        //
        // -------------------------------------------------------------------------------
        String getLabelS() {
            return LabelS;
        }

        // -------------------------------------------------------------------------------
        //
        // -------------------------------------------------------------------------------
        String getMnemonic() {
            return Mnemonic.substring(20);
        }

        // -------------------------------------------------------------------------------
        //
        // -------------------------------------------------------------------------------
        public String getMnemonicLine() {
            String m = "0x" + myStatics.Hex0(OpCode, 4) + " ";

            m += Mnemonic.substring(0, 20);
            String s = "            ";
            String d = "            ";

            if (Label != -1) {
                s = "" + Label + ":   ";
                s = "  L___________".substring(0, 12 - s.length()) + s;

            }
            if (Label2 != -1) {
                d = "" + Label2 + "   ";
                d = "  L___________".substring(0, 12 - d.length()) + d;

            }
            return "0x" + myStatics.Hex0(Address, 4) + "  " + m + LabelS
                    + Mnemonic.substring(20) + Label2S;
        }

        // -------------------------------------------------------------------------------
        //
        // -------------------------------------------------------------------------------
        void setLabel(short l) {
            Label = l;
            String s = "            ";

            s = "" + l + ":   ";
            s = "  L___________".substring(0, 12 - s.length()) + s;
            LabelS = s;
        }

        // -------------------------------------------------------------------------------
        //
        // -------------------------------------------------------------------------------
        void setLabel2(short l) {
            Label2 = l;
            String s = "            ";

            s = "" + l + ":   ";
            s = "  L___________".substring(0, 12 - s.length()) + s;
            Label2S = s;
        }

        // -------------------------------------------------------------------------------
        //
        // -------------------------------------------------------------------------------
        void setLabel2S(String s) {
            Label2S = s;
        }

        // -------------------------------------------------------------------------------
        //
        // -------------------------------------------------------------------------------
        void setLabelS(String s) {
            LabelS = s;
        }
}
