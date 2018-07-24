package analyzer;

public class IObus
{
  public long ioReg; // 16*4 bit value
  public int irgReg, extReg;
  
  final int WAIT = 0x0a00;
  
  public IObus()
  {
  	
  }
  
  public void setIRGreg(int value)
  {
  	irgReg = value;
  }
  
  public void setEXTreg(int value)
  {
  	extReg = value;
  }
  
  public void setIOreg(long value)
  {
  	ioReg = value;
  }
  
  public void cycle()
  {
  	// is IRG no WAIT instruction? 
  	if((irgReg & 0x0f00) != WAIT)
  		return;
  	
  	
  }
}
