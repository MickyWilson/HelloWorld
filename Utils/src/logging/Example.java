package logging;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public class Example {

  private static final Logger logger = Logger.getLogger(Example.class);

  void MyFunc()
  {
	  Logger logger = Logger.getLogger(getClass());
	  logger.info("My class name = " + getClass());
  }
  public
  static
  void main(String argv[]) 
  {
	PropertyConfigurator.configure("log4j.properties");
	logger.info("Info statement");
	logger.debug("Hello world.");
	logger.info("What a beatiful day.");
	Example e = new Example();
	e.MyFunc();
			
  }
}
