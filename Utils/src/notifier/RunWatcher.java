package notifier;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent.Kind;

import mike.filter.AndFilter;

import org.apache.log4j.Logger;

import directoryWatcher.PathListener;
import directoryWatcher.WatchDir;
import fileCounter.FileCounter;
import fileFilters.CanLock;
import fileFilters.FileFilter;
import fileFilters.standardFilters.EndsWith;

public class RunWatcher implements PathListener
{
	private final WatchDir watchDir;
	private final FileCounter fileCounter = new FileCounter();
	private final Path rootDirectory;
	private final Path controlFile = Paths.get("Control.dat");
	private final Path summaryFile = Paths.get("Summary.dat");
	private final SummaryFilter summaryFilter = new SummaryFilter();
	private final RunWatcherListener listener;
    private static final Logger logger = Logger.getLogger(RunWatcher.class);

	public RunWatcher(Path runDirectory, RunWatcherListener listener) throws IOException
	{
	    logger.debug("Created run watcher for " + runDirectory);
		this.rootDirectory = runDirectory;
		this.watchDir = new WatchDir(this);
		this.listener = listener;
	}

	class SummaryFilter implements FileFilter
	{

		final EndsWith endsWith = new EndsWith(summaryFile);
		final CanLock canLock = new CanLock();
		final AndFilter<Path> andFilter = new AndFilter<Path>(endsWith, canLock);
		
		@Override
        public boolean accept(Path test)
        {
	        return andFilter.accept(test); 
        }
		
	}
	
	public int CountControlFiles()
	{
		int result = Integer.MAX_VALUE;
		try
        {
	        result = fileCounter.countFiles(rootDirectory, controlFile);
        }
        catch (IOException e)
        {
        	logger.error("CountControlFiles exception", e);
        	return Integer.MAX_VALUE;
        }
		logger.debug("Found " + result + " " + controlFile);
		return result;
	}
	
	public int CountSummaryFiles()
	{
		int result = 0;
		try
        {
	        result = fileCounter.countFiles(rootDirectory, summaryFilter);
        }
        catch (IOException e)
        {
        	logger.error("CountSummaryFiles exception", e);
        }
		logger.debug("Found " + result + " " + summaryFile);
		return result;
	}
	
	@Override
    public void PathEvent(Path file, Kind<?> kind)
    {
		int numControlFiles = CountControlFiles();
	
		int numSummaryFiles = CountSummaryFiles();
		
		if (numControlFiles == numSummaryFiles)
		{
			listener.ready(rootDirectory);
		}
    }

	public void startWatchingRun()
	{
		try
        {
            watchDir.registerDirectoryToWatch(rootDirectory);
        }
        catch (IOException e)
        {
        	logger.error("Error registering directory " + rootDirectory, e);
        }
		
	    watchDir.start();		
	}
	
	public void stopWatchingRun()
	{
		watchDir.stop();
	}
		
}
