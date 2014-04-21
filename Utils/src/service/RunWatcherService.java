package service;

import java.lang.management.ManagementFactory;
import java.util.concurrent.CountDownLatch;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import jmx.ModelRunScanner;
import jmx.ModelRunScannerListener;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.boris.winrun4j.Service;
import org.boris.winrun4j.ServiceException;

import com.sun.jdmk.comm.HtmlAdaptorServer;

/**
 * A basic service.
 */
public class RunWatcherService implements Service
{
	static Logger	       logger	= Logger.getLogger(RunWatcherService.class);
	final private CountDownLatch stopLatch = new CountDownLatch(1);
	private HtmlAdaptorServer htmlAdapter;

	public int serviceRequest(int control) throws ServiceException
	{
		switch (control)
		{
		case SERVICE_CONTROL_STOP:
		case SERVICE_CONTROL_SHUTDOWN:
			stopLatch.countDown();
			break;
		default:
			break;
		}
		return 0;
	}

	private void waitForStopSignal()
	{
		logger.info("Waiting for stop signal");
		try
		{
			stopLatch.await();
			logger.info("Received stop signal");
		}
		catch (InterruptedException e)
		{
			logger.error("stopLatch interrupted.", e);
		}		
	}
	
	private static ModelRunScannerListener createModelRunScannerListener(final CountDownLatch countDownLatch)
	{
		return new ModelRunScannerListener()
		{
			@Override
			public void stoppedScanning()
			{
				logger.info("stoppedScanning() message received");
				if (countDownLatch != null)
				{
					logger.info("Sending counting down signal");
					countDownLatch.countDown();
				}
			}

			@Override
			public void startedScanning()
			{
				logger.info("startedScanning() message received");
			}
		};
	}
	private static ObjectName createObjectName(String name)
	{
		ObjectName objectName = null;
		try
		{
			objectName = new ObjectName(name);
			logger.debug("Creating ObjectName for " + name);
		}
		catch (MalformedObjectNameException e)
		{
			logger.error("Error creating object name for " + name, e);
		}
		return objectName;
	}
	
	private static HtmlAdaptorServer createHtmlAdaptor(MBeanServer server)
	{
		logger.trace("Entering createHtmlAdapter...");
		int portNumber = 8090;
		HtmlAdaptorServer html = new HtmlAdaptorServer(portNumber);

		registerMBeam(server, html, "Adaptor:name=html,port=" + portNumber);
		return html;
	}

	private static boolean registerMBeam(MBeanServer mbs, Object mBean, ObjectName name)
	{
		try
		{
			mbs.registerMBean(mBean, name);
			return true;
		}
		catch (InstanceAlreadyExistsException | MBeanRegistrationException
		        | NotCompliantMBeanException e)
		{
			logger.error(e);
			return false;
		}
	}
	private static boolean registerMBeam(MBeanServer mbs, Object mBean, String nameStr)
	{
		ObjectName name = createObjectName(nameStr);
		return registerMBeam(mbs, mBean, name);
	}

	public int serviceMain(String[] args) throws ServiceException
	{
		PropertyConfigurator.configure("log4j.properties");
		logger.info("Starting serviceMain in " + this.getClass());

		main(true);

		logger.info("Exiting serviceMain in " + this.getClass());		
		return 0;
	}

	public static void main(String args[])
	{
		PropertyConfigurator.configure("log4j.properties");
		logger.info("Starting static main in " + RunWatcherService.class);
		
		RunWatcherService rws = new RunWatcherService();
		rws.main(false);

		logger.info("Exiting static main in " + RunWatcherService.class);		

	}
	

	public void main(boolean runAsService)
	{
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
//		MBeanServer mbs = MBeanServerFactory.createMBeanServer();

		ModelRunScannerListener modelRunScannerListener;
		if (runAsService)
		{
			modelRunScannerListener = createModelRunScannerListener(null);
		}
		else
		{
			modelRunScannerListener = createModelRunScannerListener(stopLatch);
		}
		
		ModelRunScanner mBean = new ModelRunScanner(modelRunScannerListener);		
		registerMBeam(mbs, mBean, "jmx:type=ModelRunScanner");
		
		htmlAdapter = createHtmlAdaptor(mbs);		
		if (htmlAdapter != null)
			htmlAdapter.start();
		
		if (!runAsService)
			mBean.startScanning();
		
		waitForStopSignal();
		
		mBean.stopScanning();
		
		if (htmlAdapter != null)
		{
			logger.info("Stopping htmlAdapter");
			htmlAdapter.stop();
		}
	}

	
}