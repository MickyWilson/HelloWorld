package directoryWatcher;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent.Kind;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

public class TestWatchDir
{
	volatile boolean testing = false;

	static Logger logger = Logger.getLogger("testWatchDir");
	@Before
	public void Start()
	{
		PropertyConfigurator.configure("log4j.properties");
		logger.info("Info statement");
		
	}
	class Starter implements Runnable
	{
		final WatchDir dir;
		final boolean doStop;
		Starter(WatchDir dir, boolean doStop)
		{
			this.dir = dir;
			this.doStop = doStop;
		}
		@Override
        public void run()
        {
			logger.info("testing " + doStop);
			while(testing)
			{
				if (doStop)
					dir.stop();
				else
					dir.start();
				try
	            {
		            Thread.sleep(100 * (doStop ? 7 : 5));
	            }
	            catch (InterruptedException e)
	            {
	            }				
			}
			logger.info("Done " + doStop);
        }
		
	}

	class NullPathListener implements PathListener
	{

		@Override
        public void PathEvent(Path file, Kind<?> kind)
        {
	        
        }
		
	}
	
	@Test
	public void testRegisterDirectoryToWatch() throws IOException, InterruptedException
	{
		int n = 2;
		ExecutorService es = Executors.newFixedThreadPool(n);
		WatchDir wd = new WatchDir(new NullPathListener());
		wd.registerDirectoryToWatch(Paths.get("c:\\temp"));
		testing = true;
		for(int i = 0; i != n/2; ++i)
		{
			es.execute(new Starter(wd, true));
		}
		for(int i = 0; i != n/2; ++i)
		{
			es.execute(new Starter(wd, false));
		}
		TimeUnit.SECONDS.sleep(60);
		testing = false;
		logger.info("Done");
	}

	@Test
	public void testStart()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testRun()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testStop()
	{
		fail("Not yet implemented");
	}

}
