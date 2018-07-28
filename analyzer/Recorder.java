package analyzer;

import com.pi4j.wiringpi.Gpio;

public class Recorder implements Runnable
{
  // define wiringPI pin numbers
  final int PHI1 = 10; // GPIO 8
  final int IDLE = 12; // GPIO 10
  final int IRG = 13;  //GPIO 9
  final int EXT = 14;  // GPIO 11
  final int IO1 = 3;   // GPIO 22
  final int IO2 = 4;   // GPIO 23
  final int IO4 = 5;   // GPIO 24
  final int IO8 = 6;   // GPIO 25

  long ioReg; // 16*4 bit value
  int irgReg, extReg;
  int[] irgBuffer;
  int[] extBuffer;
  long[] ioBuffer;
  int bufferSize, loopCount, writePos, triggerValue;
  double samples;
  
  Thread thread;

  public Recorder(int[] irgBuffer, int[] extBuffer, long[] ioBuffer, int bufferSize)
  {
    this.irgBuffer = irgBuffer;
    this.extBuffer = extBuffer;
    this.extBuffer = extBuffer;
    this.bufferSize = bufferSize;

    thread = new Thread(this, "TMC0501 Recorder");
  }
  
  public void start(int trigger, int loops)
  {
    triggerValue = trigger;
    loopCount = loops;
    
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
    int idle, numPhi = 0;
    long t = System.nanoTime();

    for( ; loopCount > 0; loopCount--) {
      for(writePos = 0; writePos < bufferSize; ) {
        idle = Gpio.digitalRead(IDLE); // get IDLE state
        while(Gpio.digitalRead(PHI1) == 1); // wait for phi1 going low
        while(Gpio.digitalRead(PHI1) == 0); // wait for rising edge of phi1

        // store registers after falling edge of IDLE
        if(Gpio.digitalRead(IDLE) == 0) {
          if(idle == 1) // IDLE was previously high
            if(writePos > 0 || (irgReg == triggerValue)) { // recording is running
              irgBuffer[writePos] = irgReg;
              extBuffer[writePos] = extReg;
              ioBuffer[writePos] = ioReg;
              writePos++;
            }

          // clear shift register if phi1 and IDLE low
          irgReg = extReg = 0;
          ioReg = 0;
        }

        //while(Gpio.digitalRead(PHI1) == 0); // wait for rising edge of phi1

        // shift IRG bit into IRG register (LSB first)
        irgReg = irgReg >> 1 | (Gpio.digitalRead(IRG) == 1? 0x1000 : 0); // ignore S0 - S2 bits
        // shift EXT bit into EXT register (LSB first)
        extReg = extReg >> 1 | (Gpio.digitalRead(EXT) == 1? 0x8000 : 0);
        // shift IO bits into IO register (LSD first)
        if(ioBuffer != null) {
          ioReg = ioReg >> 4 | (Gpio.digitalRead(IO8) == 1? 0x8000_0000_0000_0000L : 0);
          ioReg |= Gpio.digitalRead(IO4) == 1? 0x4000_0000_0000_0000L : 0;
          ioReg |= Gpio.digitalRead(IO2) == 1? 0x2000_0000_0000_0000L : 0;
          ioReg |= Gpio.digitalRead(IO1) == 1? 0x1000_0000_0000_0000L : 0;
        }
        
        numPhi++;
      }
    }

    t = System.nanoTime() - t;
    samples = numPhi * 1e9 / t;
  }
}
