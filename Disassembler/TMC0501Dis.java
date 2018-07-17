package Disassembler;

import java.util.Hashtable;


public class TMC0501Dis extends DisAssemble {




    final int _M_FLAG_ = 0;
    final int _M_ALL_ = 1;
    final int _M_DPT_ = 2;
    final int _M_DPT1_ = 3;
    final int _M_DPT12_ = 4;
    final int _M_LLSD1_ = 5;
    final int _M_EXP_ = 6;
    final int _M_EXP1_ = 7;
    final int _M_KBD_ = 8;
    final int _M_MANT_ = 9;
    final int _M_WAIT_ = 10;
    final int _M_MLSD5_ = 11;
    final int _M_MAEX_ = 12;
    final int _M_MLSD1_ = 13;
    final int _M_MMSD1_ = 14;

    final int _M_MAEX1_ = 15;
    String[] MaskList = {
        "FLAG", "ALL", "DPT", "DPT1", "DPT12", "LLSD1", "EXP",
        "EXP1", "KBD", "MANT", "WAIT", "MLSD5", "MAEX", "MLSD1", "MMSD1",
        "MAEX1" };
    // String [] DestList =
    // {"  -->A  ","  -->IO ","A<->B  ","  -->B  ","  -->C  ","C<->D  ","  -->D  ","A<->E  "
    // };
    String[] DestList = { "A", "IO", "AB", "B", "C", "CD", "D", "AE" };
    String RA = "A";
    String RB = "B";
    String RC = "C";
    String RD = "D";
    String ZR = "0";

    String[] xPla = { RA, ZR, RC, ZR, RA, RB, RC, RD, RA, RC, RC, RA, RA, ZR, RC, ZR};
    String[] yPla = { ZR, RB, ZR, RD, ZR, ZR, ZR, ZR, RB, RB, RD, RD, ZR, ZR, ZR, ZR};

    int[] MaskV = { 0, 0, 0, 1, 12, 1, 0, 1, 0, 0, 0, 5, 0, 1, 1, 1 };

    String [] mS = {
        "................",
        "0000000000000000",
        "...............0",
        "...............1",
        "...............C",
        "............1...",
        ".............00.",
        ".............01.",
        "................",
        "0000000000000...",
        "................",
        "0000000000005...",
        "000000000000000.",
        "000000000000100.",
        "100000000000000.",
        "000000000000001.",
    };

    boolean isSR60 = false ;

    TMC0501Dis(int size,boolean sr60) {
        super(size);
        OpCodeInfo = new Hashtable<String, Integer>();
        RomSize = size;
        RomListing = new Microcode[size];

        isSR60 = sr60 ;

        myStatics.dbgPrintln("TMC0501Dis(" + size + ")");

    }
    public TMC0501Dis(int size) {
        super(size);
        OpCodeInfo = new Hashtable<String, Integer>();
        RomSize = size;
        RomListing = new Microcode[size];
        myStatics.dbgPrintln("TMC0501Dis(" + size + ")");
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------
    String opxPla(int opx, int dir, int dst, int mask) {
        String m = "";
        String d = dir == 0 ? "+" : "-";

        m += xPla[opx];
        m += d;
        if (MaskV[mask] != 0 && !yPla[opx].equals("0")) {
            m += "(";
        }
        if (yPla[opx].equals("0") && MaskV[mask] != 0) {
            m += MaskV[mask];
        } else if (!yPla[opx].equals("0") && MaskV[mask] != 0) {
            m += yPla[opx] + "|" + MaskV[mask];
        } else if (yPla[opx].equals("0") && MaskV[mask] == 0) {
            m += yPla[opx];
        } else if (!yPla[opx].equals("0") && MaskV[mask] == 0) {
            m += yPla[opx];
        }
            
        if (MaskV[mask] != 0 && !yPla[opx].equals("0")) {
            m += ")";
        }
        while (m.length() < 9) {
            m += " ";
        }
        m += DestList[dst];
        while (m.length() < 17) {
            m += " ";
        }
        m += "(" + MaskList[mask] + ")";
        return m;
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------
    String AddSub(int opx, int dir, int dst, int mask) {
        return opxPla(opx, dir, dst, mask);
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------
    String AddSub_(int opx, int dir, int dst, int mask) {
        String m = "";

        if (dir == 0) {
            switch (opx) {
            case 8:
                m += "A+B";
                break;

            case 9:
                m += "C+B";
                break;

            case 10:
                m += "C+D";
                break;

            case 11:
                m += "A+D";
                break;
            }
        } else {
            switch (opx) {
            case 8:
                m += "A-B";
                break;

            case 9:
                m += "C-B";
                break;

            case 10:
                m += "C-D";
                break;

            case 11:
                m += "A-D";
                break;
            }
        }
        while (m.length() < 9) {
            m += " ";
        }
        m += DestList[dst];
        while (m.length() < 17) {
            m += " ";
        }
        m += "(" + MaskList[mask] + ")";
        return m;

    }

    // -------------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------------
    short BranchDest(short addr, short opcode) {
        short dist = (short)((opcode >> 1) & 0x3FF) ;
        if ((opcode & 1) != 0) {
            //if( dist > addr )addr+= 0x2000 ;
            addr -= ((opcode >> 1) & 0x3FF);
        } else {
            addr += ((opcode >> 1) & 0x3FF);
            //if( addr >= 0x2000)addr -= 0x2000;
        }
        addr &= 0x1FFF ;
        return addr;
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------
    String Constant(int opx, int dir, int dst, int mask) {
        String m = "";

        if (opx == 12) {
            m += "A";
        } else {
            m += "C";
        }
        if (dir == 0) {
            m += "+Const";
        } else {
            m += "-Const";
        }
        while (m.length() < 9) {
            m += " ";
        }
        m += DestList[dst];
        while (m.length() < 17) {
            m += " ";
        }
        m += "(" + MaskList[mask] + ")";
        return m;
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------
    String DisAddN(int opx, int dir, int dst, int mask) {
        return opxPla(opx, dir, dst, mask);
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------
    String DisAddN_(int opx, int dir, int dst, int mask) {
        String m = "";

        if (dir == 0 && dst == 1 && MaskV[mask] == 0) {
            switch (opx) {
            case 0:
                m += "AIO";
                break;

            case 1:
                m += "BIO";
                break;

            case 2:
                m += "CIO";
                break;

            case 3:
                m += "DIO";
                break;
            }
        } else if (dir == 1 && dst == 1) {
            switch (opx) {
            case 0:
                m += "CAK";
                break;

            case 1:
                m += "CBK";
                break;

            case 2:
                m += "CCK";
                break;

            case 3:
                m += "CDK";
                break;
            }
        } else {
            switch (opx) {
            case 0:
                m += "A";
                break;

            case 1:
                m += "B";
                break;

            case 2:
                m += "C";
                break;

            case 3:
                m += "D";
                break;
            }
            if (dir == 0) {
                m += "+N";
            } // +MaskV[mask];
            else {
                m += "-N";
            }// +MaskV[mask];
            while (m.length() < 9) {
                m += "  ";
            }
            m += DestList[dst];

        }
        while (m.length() < 17) {
            m += " ";
        }
        m += "(" + MaskList[mask] + ")";
        return m;
    }

    // -------------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------------
    String DisAssembleIt(short opcode) {
        String mnemonic = "";

        switch ((opcode >> 8) & 0xF) {
        case _M_FLAG_:
            mnemonic += DisFlag(opcode);
            break;

        case _M_WAIT_:
            mnemonic += DisWait(opcode);
            break;

        case _M_KBD_:
            mnemonic += DisKbd(opcode);
            break;

        default:
            mnemonic += DisOpx(opcode);
            break;
        }
        return mnemonic;
    }

    // -------------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------------
    public void DisAssembleIt(short addr, short opcode) {

        String mnemonic = "";
        short addr2 = -1;

        if ((opcode & 0x1000) != 0) {
            addr2 = BranchDest(addr, opcode);
            mnemonic += DisBranch(addr, opcode, addr2);
        } else {
            mnemonic = DisAssembleIt(opcode) ;
            /*switch ((opcode >> 8) & 0xF) {
            case _M_FLAG_:
                mnemonic += DisFlag(opcode);
                break;

            case _M_WAIT_:
                mnemonic += DisWait(opcode);
                break;

            case _M_KBD_:
                mnemonic += DisKbd(opcode);
                break;

            default:
                mnemonic += DisOpx(opcode);
                break;
            }
            */
            Integer i = OpCodeInfo.get(mnemonic.substring(20));

            if (i == null) {
                i = 1;
            } else {
                i++;
            }
            OpCodeInfo.put(mnemonic.substring(20), i);
        }
        RomListing[addr] = new Microcode(addr, opcode, mnemonic, addr2);
    }


    // -------------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------------
    String DisBranch(short addr, short opcode, short addr2) {
        String mnemonic = myStatics.bitMask(opcode,
                (1 << 12) | (1 << 11) | (1 << 1))
                + " "; // "B ";

        mnemonic += "BR";
        if ((opcode & 0x0800) != 0) {
            mnemonic += "O    ";
        } else {
            mnemonic += "Z    ";
        }
        if ((opcode & 1) != 0) {
            mnemonic += "-";
        } else {
            mnemonic += "+";
        }
        mnemonic += "0x" + myStatics.Hex0((opcode >> 1) & 0x3FF, 4);
        mnemonic += "   (0x" + myStatics.Hex0(addr2, 4) + ")";
        return mnemonic;
    }

    // -------------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------------
    String DisFlag(short opcode) {
        String mnemonic = myStatics.bitMask(opcode,
                (1 << 12) | (1 << 8) | (1 << 4))
                + " "; // "F ";
        int FlagData = (opcode >> 4) & 0xF;
        int FlagOperation = (opcode & 0x7) << 1;

        FlagOperation += (opcode >> 3) & 0x1;

        switch (FlagOperation) {
        case 0:
            mnemonic += "Test   FlagA     (" + FlagData + ")";
            break;

        case 1:
            mnemonic += "Test   FlagB     (" + FlagData + ")";
            break;

        case 2:
            mnemonic += "Set    FlagA     (" + FlagData + ")";
            break;

        case 3:
            mnemonic += "Set    FlagB     (" + FlagData + ")";
            break;

        case 4:
            mnemonic += "Zero   FlagA     (" + FlagData + ")";
            break;

        case 5:
            mnemonic += "Zero   FlagB     (" + FlagData + ")";
            break;

        case 6:
            mnemonic += "Inv    FlagA     (" + FlagData + ")";
            break;

        case 7:
            mnemonic += "Inv    FlagB     (" + FlagData + ")";
            break;

        case 8:
            mnemonic += "Xchg   FlagAB    (" + FlagData + ")";
            break;

        case 9:
            mnemonic += "Comp   FlagAB    (" + FlagData + ")";
            break; // !!!!!!! bei gleichheit

        case 10:
            mnemonic += "Set    FlagKR    (" + FlagData + ")";
            break;

        case 11:
            mnemonic += "Zero   FlagKR    (" + FlagData + ")";
            break;

        case 12:
            mnemonic += "Cpy    FlagBA    (" + FlagData + ")";
            break;

        case 13:
            mnemonic += "Cpy    FlagAB    (" + FlagData + ")";
            break;

        case 14:
            mnemonic += "Reg5   FlagA     (" + FlagData + ")";
            break;

        case 15:
            mnemonic += "Reg5   FlagB     (" + FlagData + ")";
            break;
        }
        if (opcode == 0x0015) {
            mnemonic += " (PREG) ";
        }

        return mnemonic;
    }

    // -------------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------------
    String DisKbd(short opcode) {
        String m = myStatics.bitMask(opcode, (1 << 12) | (1 << 8)) + " "; // "K ";

        m += "Keyboard (";
        if ((opcode & (1 << 0)) == 0) {
            m += ",KN";
        }
        if ((opcode & (1 << 1)) == 0) {
            m += ",KO";
        }
        if ((opcode & (1 << 2)) == 0) {
            m += ",KP";
        }
        if ((opcode & (1 << 4)) == 0) {
            m += ",KQ";
        }
        if ((opcode & (1 << 5)) == 0) {
            m += ",KR";
        }
        if ((opcode & (1 << 6)) == 0) {
            m += ",KS";
        }
        if ((opcode & (1 << 7)) == 0) {
            m += ",KT";
        }
        if ((opcode & (1 << 3)) == 0) {
            m += ",SCAN";
        }

        m += ")";
        return m;
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------
    String DisOpx(short opcode) {
        String mnemonic = myStatics.bitMask(opcode,
                (1 << 12) | (1 << 8) | (1 << 4) | (1 << 3))
                + " "; // "A " ;
        int opx = (opcode >> 4) & 0xF;
        int dir = (opcode >> 3) & 0x1;
        int dst = (opcode >> 0) & 0x7;
        int mask = (opcode >> 8) & 0xF;

        switch (opx) {
        case 0:
        case 1:
        case 2:
        case 3:
            mnemonic += DisAddN(opx, dir, dst, mask);
            break;

        case 4:
        case 5:
        case 6:
        case 7:
            mnemonic += Shift(opx, dir, dst, mask);
            break;

        case 8:
        case 9:
        case 10:
        case 11:
            mnemonic += AddSub(opx, dir, dst, mask);
            break;

        case 13:
            mnemonic += NoOp(opx, dir, dst, mask);
            break;

        case 15:
            mnemonic += R5Adder(opx, dir, dst, mask);
            break;

        case 12: // A+-Constant
        case 14: // C+-Constant
            mnemonic += Constant(opx, dir, dst, mask);
            break;
        }

        return mnemonic;
    }

    // -------------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------------
    String DisWait(short opcode) {
        String mnemonic = myStatics.bitMask(opcode,
                (1 << 12) | (1 << 8) | (1 << 4))
                + " "; // "W ";
        int WaitData = (opcode >> 4) & 0xF;
        int WaitOperation = (opcode & 0x7) << 1;

        WaitOperation += (opcode >> 3) & 0x1;

        switch (opcode) {

        case 0x0AE8:
            mnemonic += "AUX-ID";
            break;
        case 0x0AD8:
            mnemonic += "AUX-DATA";
            break;
        case 0x0A68:
            mnemonic += "CharacterPrint";
            break;

        case 0x0A78:
            mnemonic += "FunctionPrint";
            break;

        case 0x0A88:
            mnemonic += "ClearPrint";
            break;

        case 0x0A98:
            mnemonic += "StepPrint";
            break;

        case 0x0AA8:
            mnemonic += "Print";
            break;

        case 0x0AB8:
            mnemonic += "PaperAdv";
            break;


        case 0x0A66:
            mnemonic += "DisplayCharacter";
            break;
        case 0x0A76:
            mnemonic += isSR60 ? "DisplayFunction" : "MemoryWR";
            break;
        case 0x0A86:
            mnemonic += isSR60 ? "ClearDisplay" : "MemoryRD";
            break;
        case 0x0A96:
            mnemonic += "StepDisplay";
            break;
        case 0x0AA6:
            mnemonic += "Display";
            break;
        case 0x0A0E:
            mnemonic += "FetchRom";
            break;

        case 0x0A3E:
            mnemonic += "FetchRomHigh";
            break;

        case 0x0A1E:
            mnemonic += "LoadRomPc";
            break;

        case 0x0A2E:
            mnemonic += "UnloadRomPc";
            break;


        case 0x0A38:
            mnemonic += "MagExtDr8";
            break;

        case 0x0A28:
            mnemonic += "MagDrExt8";
            break;

        case 0x0A48:
            mnemonic += "MagToff";
            break;

        case 0x0A58:
            mnemonic += "MagRdnt";
            break;

        case 0x0AC8:
            mnemonic += "MagWrOn";
            break;

        case 0x0A0F:
            mnemonic += "StoreF";
            break;

        case 0x0A1F:
            mnemonic += "RecallF";
            break;

        case 0x0A2F:
            mnemonic += "StoreG";
            break;

        case 0x0A3F:
            mnemonic += "RecallG";
            break;

        case 0x0A4F:
            mnemonic += "StoreH";
            break;

        case 0x0A5F:
            mnemonic += "RecallH";
            break;

        case 0x0A6F:
            mnemonic += "StoreI";
            break;

        case 0x0A7F:
            mnemonic += "RecallI";
            break;

        case 0x0AF8:
            mnemonic += "MemoryOp";
            break;


        default:

            switch (WaitOperation) {
            case 0:
                mnemonic += "Wait   Digit     (" + WaitData + ")";
                break;

            case 1:
                if (WaitData == 0) {
                    mnemonic += "KRR5             (" + WaitData + ")";
                } else {
                    mnemonic += "R5KR             (" + WaitData + ")";
                }
                break;

            case 2:
                mnemonic += "Zero   Idle      (" + WaitData + ")";
                break;

            case 3:
                mnemonic += "Set    Idle      (" + WaitData + ")";
                break;

            case 4:
                mnemonic += "Clr    FlagA     (" + WaitData + ")";
                break;

            case 5:
                mnemonic += "Clr    FlagB     (" + WaitData + ")";
                break;

            case 6:
                mnemonic += "Wait   Busy      (" + WaitData + ")";
                break;

            case 7:
                mnemonic += "Test   Busy      (" + WaitData + ")";
                break;

            case 8:
                mnemonic += "IncKR            (" + WaitData + ")";
                break;

            case 9:
                mnemonic += "ExtKR            (" + WaitData + ")";
                break;

            case 10:
                mnemonic += "Test   FlagKR    (" + WaitData + ")";
                break;

            case 11:
                mnemonic += "XKRSR            (" + WaitData + ")";
                break;

            case 12:
                if (WaitData == 0) {
                    mnemonic += "FlagA  Reg5      (" + WaitData + ")";
                } else {
                    mnemonic += "FlagB  Reg5      (" + WaitData + ")";
                }
                break;

            case 13:
                mnemonic += "NoOp             (" + WaitData + ")";
                break;

            case 14:
                mnemonic += "Number           (" + WaitData + ")";
                break;

            case 15:
                mnemonic += "Register         (" + WaitData + ")";
                break;
            }
        }
        return mnemonic;
    }


    String Mnemonic(short addr) {
        String ds = RomListing[addr].getMnemonicLine();

        if (ds.indexOf(" 0x1801") > 4) {
            ds += "           // UNKNOWN MICROCODE";
        }
        return disasmGetRomTag(addr)+ds;
    }

    String MnemonicS(short addr) {
        return RomListing[addr].getMnemonicLine().substring(37);
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------
    String NoOp(int opx, int dir, int dst, int mask) {
        String m = "";
        int ConstMask = MaskV[mask];

        if (dir == 0 && dst == 5) {
            m += "XCHG";
        } // CD";//      ("+ MaskList[mask]+")";
        // //+" DD "+_DestString(Dest); // Xchange oder Copy ?
        else if (dir == 0 && dst == 2) {
            m += "XCHG";
        } // AB";//      ("+ MaskList[mask]+")";
        // //+" DD "+_DestString(Dest); // Xchange oder Copy ?
        else if (dir == 0 && dst == 7) {
            m += "XCHG";
        } // AE";//      ("+ MaskList[mask]+")";
        // //+" DD "+_DestString(Dest); // Xchange oder Copy ?
        else if (dir == 0 && dst == 0 && ConstMask == 0) {
            m += "LOAD";
        } // A ";//     ("+ MaskList[mask]+")  ";//+DestList[dst];
        // // Laed vom IO-BUS
        else if (dir == 0 && dst == 3 && ConstMask == 0) {
            m += "LOAD";
        } // B ";//     ("+ MaskList[mask]+")  ";//+DestList[dst];
        // // nach RECALL-F
        else if (dir == 0 && dst == 4 && ConstMask == 0) {
            m += "LOAD";
        } // C ";//     ("+ MaskList[mask]+")  ";//+DestList[dst];
        // // oder MEMORY
        else if (dir == 0 && dst == 6 && ConstMask == 0) {
            m += "LOAD";
        } // D ";//     ("+ MaskList[mask]+")  ";//+DestList[dst];
        // // Operation
        else if (dir == 0 && dst == 0 && ConstMask != 0) {
            m += "CONST";
        } // A ";//     ("+
        // MaskList[mask]+")  ";//+DestList[dst]; // Laed
        // Mask Constante
        else if (dir == 0 && dst == 3 && ConstMask != 0) {
            m += "CONST";
        } // B ";//     ("+
        // MaskList[mask]+")  ";//+DestList[dst]; // in das
        else if (dir == 0 && dst == 4 && ConstMask != 0) {
            m += "CONST";
        } // C ";//     ("+
        // MaskList[mask]+")  ";//+DestList[dst]; //
        // entsprechende
        else if (dir == 0 && dst == 6 && ConstMask != 0) {
            m += "CONST";
        } // D ";//     ("+
        // MaskList[mask]+")  ";//+DestList[dst]; //
        // Register
        else if (dir == 1 && dst == 0) {
            m += "CLR  ";
        } // A ";//    ("+ MaskList[mask]+")  ";//+DestList[dst];
        // // C-C - Const
        else if (dir == 1 && dst == 3) {
            m += "CLR  ";
        } // B ";//    ("+ MaskList[mask]+")  ";//+DestList[dst];
        // // A-A - Const
        else if (dir == 1 && dst == 4) {
            m += "CLR  ";
        } // C ";//    ("+ MaskList[mask]+")  ";//+DestList[dst];
        // // B-B - Const
        else if (dir == 1 && dst == 6) {
            m += "CLR  ";
        }// D ";//    ("+ MaskList[mask]+")  ";//+DestList[dst];
        // // D-D - Const

        while (m.length() < 9) {
            m += " ";
        }
        m += DestList[dst];
        while (m.length() < 17) {
            m += " ";
        }
        m += "(" + MaskList[mask] + ")";
        return m;
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------
    String R5Adder(int opx, int dir, int dst, int mask) {
        String m = "";

        m += "R5Add ";
        while (m.length() < 9) {
            m += " ";
        }
        m += DestList[dst];
        while (m.length() < 17) {
            m += " ";
        }
        m += "(" + MaskList[mask] + ")";
        return m;
    }

    public void SetLabel(int index, String label) {
        // System.out.println( "SET: "+myStatics.Hex0(index,4)+" "+label );

        String labelS = "F" + label.substring(1);

        label = "  " + label + ":";
        while (label.length() < 12) {
            label += " ";
        }
        String ls = RomListing[index].getLabelS();

        RomListing[index].setLabelS(label);
        String line = RomListing[index].getMnemonic();

        if (line.indexOf("BRO") > 0) {
            int dest = line.indexOf("(");
            int index2 = 0;

            index2 = Integer.parseInt(line.substring(dest + 3, dest + 7), 16);
            // System.out.println(
            // RomListing[index].getMnemonicLine()+" "+myStatics.Hex0(index2,4)
            // );
            SetLabel(index2, labelS);
        }
        if (RomListing[index].getLabel() == -1) {
            return;
        }
        for (int i = 0; i < RomSize; i++) {
            if (RomListing[i].getLabel2S().equals(ls)) {
                RomListing[i].setLabel2S(label);
            }
        }
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------
    String Shift(int opx, int dir, int dst, int mask) {
        String m = "Sh";

        if (dir == 0) {
            m += "l";
        } else {
            m += "r";
        }
        switch (opx) {
        case 4:
            m += "A";
            break;

        case 5:
            m += "B";
            break;

        case 6:
            m += "C";
            break;

        case 7:
            m += "D";
            break;
        }
        while (m.length() < 9) {
            m += " ";
        }
        m += DestList[dst];
        while (m.length() < 17) {
            m += " ";
        }
        m += "(" + MaskList[mask] + ")";
        return m;
    }
}
