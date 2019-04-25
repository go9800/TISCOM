package analyzer;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;

import Disassembler.TMC0501Dis;

public class TMC0501Analyzer
{
  int triggerValue = 0x0a0a;
  int triggerAddr = 0x0001;
  int SIZE = 1024;

  // define wiringPI pin numbers // BCM GPIO #
  final int PHI1 = 10; // GPIO 8
  final int IDLE = 12; // GPIO 10
  final int IRG = 13;  // GPIO 9
  final int EXT = 14;  // GPIO 11
  final int IO1 = 3;   // GPIO 22
  final int IO2 = 4;   // GPIO 23
  final int IO4 = 5;   // GPIO 24
  final int IO8 = 6;   // GPIO 25

  // TMC0501 EXT bits
  final int EXT_PREG = 0b0000000000000001;
  final int EXT_COND = 0b0000000000000010;
  final int EXT_HOLD = 0b0000000000000100;
  final int EXT_CONST = 0b0000011110000000;
  final int EXT_ADDR = 0b1111111111111000;

  // TMC0501 IRG bits
  final int IRG_BRANCH = 0b1000000000000;
  final int IRG_COND = 0b0100000000000;
  
  // SR-60 and PC-100 output instructions
  final int CHARPRNT = 0x0a68;
  final int FUNCPRINT = 0x0a78;
  final int CLRPRNT = 0x0a88;
  final int STEPPRNT = 0x0a98;
  final int PPRINT = 0x0aa8;
  final int PADV = 0x0ab8;
  final int MEMO = 0x0af8;
  final int DR8EXT = 0x0a28;
  final int EXTDR8 = 0x0a38;
  final int TOFF = 0x0a48;
  final int RDNT = 0x0a58;
  final int WRON = 0x0ac8;
  final int AUXID = 0x0ae8;
  final int AUXDATA = 0x0ad8;
  final int CHARDISP = 0x0a66;
  final int FUNCDISP = 0x0a76;
  final int CLRDISP = 0x0a86;
  final int STEPDISP = 0x0a96;
  final int DPRINT = 0x0aa6;

  long t;
  long ioReg; // 16*4 bit value
  int irgReg, extReg;
  int[] irgBuffer = new int[SIZE];
  int[] extBuffer = new int[SIZE];
  long[] ioBuffer = new long[SIZE];
  int n, np, idle, addr, addr2;
  int revTrigger;

  static TMC0501Dis disasm;

  // reverse order of bits in a long value
  public long bitrev(long val, int bits)
  {
    long rev = 0;

    for(int i = 0; i < bits; i++) {
      rev = rev << 1 | (val & 1);
      val >>= 1;
    }

    return(rev);
  }

  public String longToDecString(long value, int digits)
  {
    String dec_value = Long.toString(value);
    return("00000000".substring(8 - digits).substring(dec_value.length()) + dec_value);
  }

  public String longToHexString(long value, int digits)
  {
    String hex_value = Long.toHexString(value);
    return("0000000000000000".substring(16 - digits).substring(hex_value.length()) + hex_value);
  }

  public String longToBinString(long value, int digits)
  {
    String bin_value = Long.toBinaryString(value);
    return("00000000000000000000000000000000".substring(32 - digits).substring(bin_value.length()) + bin_value);
  }

  public TMC0501Analyzer()
  {
    addr = triggerAddr;

    // setup wiring pi
    if (Gpio.wiringPiSetup() == -1) {
      System.out.println("GPIO SETUP FAILED");
      return;
    }

    // export all required GPIO pins
    GpioUtil.export(PHI1, GpioUtil.DIRECTION_IN);
    GpioUtil.export(IDLE, GpioUtil.DIRECTION_IN);
    GpioUtil.export(IRG, GpioUtil.DIRECTION_IN);
    GpioUtil.export(EXT, GpioUtil.DIRECTION_IN);
    GpioUtil.export(IO1, GpioUtil.DIRECTION_IN);
    GpioUtil.export(IO2, GpioUtil.DIRECTION_IN);
    GpioUtil.export(IO4, GpioUtil.DIRECTION_IN);
    GpioUtil.export(IO8, GpioUtil.DIRECTION_IN);

    // configure GPIO pins
    Gpio.pinMode(PHI1, Gpio.INPUT);
    Gpio.pullUpDnControl(PHI1, Gpio.PUD_OFF); // no pull-down resistor        
    Gpio.pinMode(IDLE, Gpio.INPUT);
    Gpio.pullUpDnControl(IDLE, Gpio.PUD_OFF); // no pull-down resistor        
    Gpio.pinMode(EXT, Gpio.INPUT);
    Gpio.pullUpDnControl(EXT, Gpio.PUD_OFF); // no pull-down resistor        
    Gpio.pinMode(IO1, Gpio.INPUT);
    Gpio.pullUpDnControl(IO1, Gpio.PUD_OFF); // no pull-down resistor        
    Gpio.pinMode(IO2, Gpio.INPUT);
    Gpio.pullUpDnControl(IO2, Gpio.PUD_OFF); // no pull-down resistor        
    Gpio.pinMode(IO4, Gpio.INPUT);
    Gpio.pullUpDnControl(IO4, Gpio.PUD_OFF); // no pull-down resistor        
    Gpio.pinMode(IO8, Gpio.INPUT);
    Gpio.pullUpDnControl(IO8, Gpio.PUD_OFF); // no pull-down resistor  

    t = System.nanoTime();
    np = 0;
    revTrigger = (int)bitrev(triggerValue << 3, 16); // add S0 - S2 states then reverse bit order

    for(n = 0; n < SIZE; ) {
      idle = Gpio.digitalRead(IDLE); // get IDLE state
      while(Gpio.digitalRead(PHI1) == 1); // wait for phi1 going low
      while(Gpio.digitalRead(PHI1) == 0); // wait for rising edge of phi1

      // store registers after falling edge of IDLE
      if(Gpio.digitalRead(IDLE) == 0) {
        if(idle == 1) // IDLE was previously high
          if(n > 0 || (irgReg & 0x1fff) == revTrigger) { // recording is running
            irgBuffer[n] = irgReg;
            extBuffer[n] = extReg;
            ioBuffer[n] = ioReg;
            n++;
          }

        // clear shift register if phi1 and IDLE low
        irgReg = extReg = 0;
        ioReg = 0;
      }

      // shift IRG bit into IRG register (LSB first, bit reversal necessary!)
      irgReg = irgReg << 1 | Gpio.digitalRead(IRG);
      // shift EXT bit into EXT register (LSB first)
      extReg = extReg << 1 | Gpio.digitalRead(EXT);
      // shift IO bits into IO register (LSB first)
      ioReg = (((ioReg << 1 | Gpio.digitalRead(IO1)) << 1 | Gpio.digitalRead(IO2)) << 1 | Gpio.digitalRead(IO4)) << 1 | Gpio.digitalRead(IO8);
      np++;
    }

    t = System.nanoTime() - t;
    System.out.println("Sample rate: " + np * 1e9 / t);
    System.out.println("IRG trigger: " + longToHexString(triggerValue, 4));

    for(n = 0; n < SIZE; n++) {
      irgReg = (int)bitrev(irgBuffer[n], 16) >> 3; // eliminate S0 - S2 bits
    extReg = (int)bitrev(extBuffer[n], 16);
    ioReg = bitrev(ioBuffer[n], 64);

    disasm.DisAssembleIt((short)addr, (short)irgReg);
    System.out.println(longToDecString(n, 4) + ": " + longToHexString(irgReg,4) + "  " + longToBinString(extReg, 16) + "  " + longToHexString(ioReg, 16)  + " " + disasm.RomListing[addr].getMnemonicLine() );

    // get EXT bus of next sample
    if(n + 1 < SIZE)
      extReg = (int)bitrev(extBuffer[n+1], 16);

    // check EXT bus for HOLD bit
    if((extReg & EXT_HOLD) != 0)
      continue;  // don't increment address

    // check for branch
    addr2 = disasm.RomListing[addr].getAddr2();
    addr++; // increment address in case of no branch

    if((irgReg & IRG_BRANCH) != 0) {
      // branch only if condition bits in EXT and IRG match 
      if(!((extReg & EXT_COND) != 0) ^ ((irgReg & IRG_COND) != 0))
        addr = addr2; // set next address to branch destination
    }
    }
  }

  public static void main(String[] args)
  {
    // prepare disassembler
    disasm = new TMC0501Dis(0x2000);

    new TMC0501Analyzer();
  }
}

