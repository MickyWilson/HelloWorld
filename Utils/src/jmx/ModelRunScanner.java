package jmx;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import notifier.RunWatcher;
import notifier.RunWatcherListener;

import org.apache.log4j.Logger;


public class ModelRunScanner implements LocalModelRunScannerMBean, RunWatcherListener
{
    private static final Logger logger = Logger.getLogger(ModelRunScanner.class);
    private String directory = null;
	private String status = "not set";
	private static String STATUS_WAITING = "waiting for (valid) directory";
	private static String STATUS_READY = "ready to start scanning";
	private static String STATUS_SCANNING = "scanning";
	private static String STATUS_STOPPED = "stopped scanning";
	
	private RunWatcher rw;
	
	final ModelRunScannerListener listener;
	
	private void setStatus(String status)
	{
		logger.info("Status set to '" + status + "'");
		this.status = status;
	}

	public ModelRunScanner(ModelRunScannerListener listener)
	{
		this.listener = listener;
		setStatus(STATUS_WAITING);
		setDirectory("c:/temp");
	}
	
	public ModelRunScanner(ModelRunScannerListener listener, Paths directory)
	{
		this.listener = listener;
		setStatus(STATUS_WAITING);
	}
	
	@Override
	public void startScanning()
	{
		logger.info("startScanning");
		try
        {
	        rw = new RunWatcher(Paths.get(directory), this);
        }
        catch (IOException e)
        {
        	logger.error("startScanning", e);
    		setStatus(STATUS_WAITING);
        	return;
        }
        rw.startWatchingRun();
		setStatus(STATUS_SCANNING);
		listener.startedScanning();
	}

	@Override
	public void stopScanning()
	{
		logger.info("Entering stopScanning");
		rw.stopWatchingRun();
		setStatus(STATUS_STOPPED);
		logger.info("calling listener.stoppedScanning()");			
		listener.stoppedScanning();
		logger.info("stopScanning finished");
	}

	@Override
	public void setDirectory(String directory)
	{
		logger.info("setDirectory to " + directory);
		setStatus(STATUS_READY);
		this.directory = directory;
	}

	@Override
	public String getDirectory()
	{
		return directory;
	}

	@Override
	public String getStatus()
	{
		return status;
	}
	@Override
    public int getNumberOfRuns()
	{
	    return rw.CountControlFiles();
    }
	
	@Override
    public int getNumberOfRunsCompleted()
    {
	    return rw.CountSummaryFiles();
    }

	@Override
    public void ready(Path rootDirectory)
    {
	    if (rootDirectory.equals(directory))
	    {
	        logger.error("Root directory != directory\n" + rootDirectory + "\n" + directory);
	    }
		logger.info("ready: " + directory);
		stopScanning();
    }

}
