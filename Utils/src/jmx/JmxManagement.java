package jmx;

import java.lang.management.ManagementFactory;
import java.util.concurrent.CountDownLatch;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import com.sun.jdmk.comm.HtmlAdaptorServer;

public class JmxManagement
{
	private static final Logger	logger	= Logger.getLogger(JmxManagement.class);

	private static HtmlAdaptorServer createHtmlAdapter(MBeanServer server)
	{
		logger.trace("Entering createHtmlAdapter...");
		int portNumber = 8090;
		try
		{
			HtmlAdaptorServer html = new HtmlAdaptorServer(portNumber);
			ObjectName html_name = null;
			html_name = new ObjectName("Adaptor:name=html,port=" + portNumber);
			logger.debug("\tRegistered MBean: OBJECT NAME = " + html_name);
			if (server == null)
				server = MBeanServerFactory.createMBeanServer();
			server.registerMBean(html, html_name);
			return html;
		}
		catch (Exception e)
		{
			logger.error("Could not create the HTML adaptor!", e);
			return null;
		}
	}

	public static void main(String[] args) throws MalformedObjectNameException,
	        InterruptedException, InstanceAlreadyExistsException, MBeanRegistrationException,
	        NotCompliantMBeanException
	{
		final CountDownLatch waitingToFinish = new CountDownLatch(1);

		PropertyConfigurator.configure("log4j.properties");
		logger.info("Starting " + JmxManagement.class);
		// Get the MBean server
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); //MBeanServerFactory.createMBeanServer(); // best for
																  // adapter
		// ManagementFactory.getPlatformMBeanServer(); // Works with JConsole
		// register the MBean
		ModelRunScanner mBean = new ModelRunScanner(new ModelRunScannerListener()
		{

			@Override
			public void stoppedScanning()
			{
				logger.info("waitingToFinish.countDown()");
				waitingToFinish.countDown();
			}

			@Override
			public void startedScanning()
			{
			}
		});
		ObjectName name = new ObjectName("jmx:type=ModelRunScanner");
		mbs.registerMBean(mBean, name);
		HtmlAdaptorServer htmlServer = createHtmlAdapter(mbs);
		if (htmlServer != null)
		{
			logger.info("Starting html server");
			htmlServer.start();
		}
		mBean.startScanning();
		logger.info("waitingToFinish.await");
		waitingToFinish.await();
		logger.info("Stoping " + JmxManagement.class);
		if (htmlServer != null)
		{
			logger.info("Stopping html server");
			htmlServer.stop();
		}

	}

}