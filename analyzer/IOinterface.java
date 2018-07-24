package analyzer;

public class IOinterface implements Runnable
{
  // SR-60 and PC-100 IO instructions
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


  protected Thread devThread;
  int timerValue = 100;
  
  IObus ioBus;

  public IOinterface(String name, IObus ioBus)
  {
  	this.ioBus = ioBus;

    devThread = (name == null) ? new Thread(this) : new Thread(this, name);
  }

  public void start()
  {
    // start only explicid named threads
    if(!devThread.getName().startsWith("Thread"))
      devThread.start();
  }
  
  public void run()
  {
    while(true) {
      // sleep until interrupted by IO-instruction
      try {
        Thread.sleep(timerValue);
      } catch(InterruptedException e) {
      }
      
      switch(ioBus.irgReg) {
      	case CHARDISP:
      		
      }
    }
  }
}
