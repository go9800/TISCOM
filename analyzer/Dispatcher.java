package analyzer;

public class Dispatcher implements Runnable
{
  // TMC0501 EXT bit masks
  final int EXT_PREG = 0b0000_0000_0000_0001;
  final int EXT_COND = 0b0000_0000_0000_0010;
  final int EXT_HOLD = 0b0000_0000_0000_0100;
  final int EXT_CONST = 0b0000_0111_1000_0000;
  final int EXT_ADDR = 0b1111_1111_1111_1000;
  final int EXT_CHAR = 0b0000_0001_1111_1000;
  final int EXT_FUNC = 0b0000_0111_1111_1000;
  
  //TMC0501 IO bit masks
  final int IO_MEMADDR = 0b0000_0000_0000_1100;
  final int IO_MEMOP = 0b0000_0000_0000_0001;
  
  // TMC0501 IRG bits
  final int IRG_BRANCH = 0b1000000000000;
  final int IRG_COND = 0b0100000000000;

  // Memory, SCOM, Printer and Display IO instructions
  final int IRG_WAIT = 0x0a00;
  
  // Printer
  final int CHARPRNT = 0x0a68;
  final int FUNCPRINT = 0x0a78;
  final int CLRPRNT = 0x0a88;
  final int STEPPRNT = 0x0a98;
  final int PPRINT = 0x0aa8;
  final int PADV = 0x0ab8;
  // Memory
  final int MEMO = 0x0af8;
  // Magn. Card Reader
  final int DR8EXT = 0x0a28;
  final int EXTDR8 = 0x0a38;
  final int TOFF = 0x0a48;
  final int RDNT = 0x0a58;
  final int WRON = 0x0ac8;
  // AUX Port
  final int AUXID = 0x0ae8;
  final int AUXDATA = 0x0ad8;
  // Display
  final int CHARDISP = 0x0a66;
  final int FUNCDISP = 0x0a76;
  final int CLRDISP = 0x0a86;
  final int STEPDISP = 0x0a96;
  final int DPRINT = 0x0aa6;
  // ROM
  final int FETCH = 0x0a0e;
  final int LOADPC = 0x0a1e;
  final int UNLOADPC = 0x0a2e;
  final int FETCHHIGH = 0x0a3e;
  // SCOM
  final int STOF = 0x0a0f;
  final int RCLF = 0x0a1f;
  final int STOG = 0x0a2f;
  final int RCLG = 0x0a3f;

  long ioReg; // 16*4 bit value
  int irgReg, extReg;
  int[] irgBuffer;
  int[] extBuffer;
  long[] ioBuffer;
  int[] addrBuffer;
  char[] printerReg = new char[20];
  char[] displayReg = new char[20];
  char[][] functionTable = new char[3][100];
  int bufferSize, readPos, triggerValue;
  int displayPos = 0, printerPos = 0;
  Thread thread;
  Recorder recorder;

  public Dispatcher(int[] irgBuffer, int[] extBuffer, long[] ioBuffer, int[] addrBuffer, int bufferSize, Recorder recorder)
  {
    this.irgBuffer = irgBuffer;
    this.extBuffer = extBuffer;
    this.extBuffer = extBuffer;
    this.bufferSize = bufferSize;
    this.addrBuffer = addrBuffer;
    this.recorder = recorder;

    thread = new Thread(this, "TMC0501 Dispatcher");
  }

  public void start(int trigger)
  {
    triggerValue = trigger;

    // start only explicid named threads
    if(!thread.getName().startsWith("Thread"))
      thread.start();
  }

  public void stop()
  {
    if(thread != null)
    {
      thread.stop();
      thread = null;
    }
  }

  public void run()
  {
    while(true)
    for(readPos = 0; readPos < bufferSize; )
    {
      if(readPos == recorder.writePos)  // is end of analyze queue reached?
        continue;

      irgReg = irgBuffer[readPos];
      extReg = extBuffer[readPos];

      if((irgReg & 0xff00) == IRG_WAIT)  // is IRG a WAIT instruction?
      {
        switch(irgReg & 0x0a0f) {
        case 0x0a0f:
          scom();
          break;
          
        case 0x0a0e:
          rom();
          break;
          
        case 0x0a06:
          display();
          break;
        
        default:
          if(irgReg == 0x0af8)
           ram();
          else if(irgReg >= 0x0ad8)
            aux();
          else if(irgReg >= 0x0a68 && irgReg <= 0x0ab8)
            printer();
          else
            mcard();
         }
        }
      
      readPos++;
    }
  }
  
  void ram()
  {
    
  }
  
  void rom()
  {
    
  }
  
  void scom()
  {
    
  }
  
  void mcard()
  {
    
  }
  
  void aux()
  {
    
  }
  
  void printer()
  {
    
  }
  
  void display()
  {
    int func, i;
    char c;

    switch(irgReg) {
    case CHARDISP:
      displayReg[displayPos++] = (char)((extReg & EXT_CHAR) >> 3);
      break;

    case FUNCDISP:
      func = (extReg & EXT_FUNC) >> 3;
      for(i = 0; i < 3; i++) {
        c = functionTable[i][func];
        if(c == 0)
          break;
        if(displayPos >= 20)
          break;
        displayReg[displayPos++] = c;
      }

    case STEPDISP:
      displayPos++;
      break;

    case CLRDISP:
      for(displayPos = 0; displayPos < 20; displayPos++)
        displayReg[displayPos++] = 0;
      break;

    case DPRINT:
      break;
    }

    if(displayPos >= 20)
      displayPos = 0;
  }
}
