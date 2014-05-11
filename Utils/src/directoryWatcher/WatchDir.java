package directoryWatcher;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
/**
 * Example to watch a directory (or tree) for changes to files.
 */

public class WatchDir implements Runnable
{
    static String appName = new Object(){}.getClass().getEnclosingClass().getName();	
	private static final Logger logger = Logger.getLogger(WatchDir.class);

	private final WatchService	      watcher;
	private final Map<WatchKey, Path> keys;
	private final boolean             recursive = true;
	private final PathListener		  pathListener;
	private ExecutorService executor;
	private FutureTask<Void> future = null; // guarded by lock
	private volatile boolean isRunning = false;
	Lock lock = new ReentrantLock();
	
	public WatchDir(PathListener pathListener)
	{
		this.pathListener = pathListener;
		WatchService watchService = null;
		try
        {
			watchService = FileSystems.getDefault().newWatchService();
        }
        catch (IOException e)
        {
        	logger.error("New watch service failed!", e);
        }
        this.watcher = watchService;
		this.keys = new HashMap<WatchKey, Path>();
	}
	
	/**
	 * Registers the given directory
	 * @throws IOException 
	 */
	public void registerDirectoryToWatch(Path dir) throws IOException 
	{
		if (recursive)
		{
			registerAll(dir);
		}
		else
		{
			register(dir);
		}
	}

	public boolean start()
	{
		logger.info("S");
		try
		{
			lock.lock();
			if (isRunning)
				return false;
			isRunning = true;
			logger.info("start WatchDir");
			future = new FutureTask<Void>(this, null);
			executor = Executors.newFixedThreadPool(1);
			executor.submit(future);
			return true;
			
		}
		finally
		{
			lock.unlock();
		}
		
	}
	
	public boolean isRunning()
	{
		return isRunning;
	}
	
	public boolean stop()
	{
		try
		{
			lock.lock();
			if (!isRunning)
				return false;
			logger.info("stop WatchDir");
			//future.cancel(true);
			executor.shutdownNow();
			return true;
		}
		finally
		{
			lock.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> WatchEvent<T> cast(WatchEvent<?> event)
	{
		return (WatchEvent<T>) event;
	}

	/**
	 * Register the given directory with the WatchService
	 */
	private void register(Path dir) 
	{
		if (future != null && !isInterrupted())
			return;
		
		WatchKey key;
        try
        {
	        key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        }
        catch (IOException e)
        {
        	logger.error("Failed to resister directory for watching : " + dir, e);
        	return;
        }
		
		if (logger.isTraceEnabled())
		{
			Path prev = keys.get(key);
			if (prev == null)
			{
				logger.trace(String.format("register: %s", dir));
			}
			else
			{
				if (!dir.equals(prev))
				{
					logger.trace(String.format("update: %s -> %s", prev, dir));
				}
			}
		}
		
		keys.put(key, dir);
	}

	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 */
	private void registerAll(final Path start)
	{
	    logger.info("RegisterAll:"+ start);
		// register directory and sub-directories
        try
        {
	        Files.walkFileTree(start, new SimpleFileVisitor<Path>()
	        {
	        	@Override
	        	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException  
	        	{
	        		register(dir);
	        		return FileVisitResult.CONTINUE;
	        	}
	        });
        }
        catch (IOException e)
        {
        	logger.error("Failed to walk directory tree:" + start, e);
        }
	}


	/**
	 * Process all events for keys queued to the watcher
	 * @throws InterruptedException 
	 */
	private void proccessEvents() throws InterruptedException 
	{
		logger.info("Processing Events");
		
		while(!isInterrupted())
		{
			logger.trace("waiting for key to be signalled");
			// wait for key to be signalled
			WatchKey key = watcher.take();

			Path dir = keys.get(key);
			if (dir == null)
			{
				logger.error("WatchKey not recognized!!.  " + key.toString());
				continue;
			}
			
			for (WatchEvent<?> event : key.pollEvents())
			{
				if (isInterrupted())
					break;
				
				Kind<?> kind = event.kind();

				if (kind == OVERFLOW)
				{
					logger.warn("kind == OVERFLOW not processed");
					continue;
				}

				// Context for directory entry event is the file name of entry
				WatchEvent<Path> ev = cast(event);
				Path name = ev.context();
				Path fullPath = dir.resolve(name);

				// print out event
				logger.debug(String.format("%s: %s", event.kind().name(), fullPath));
				pathListener.PathEvent(fullPath, kind);
				
				if (isInterrupted())
					break;

				// if directory is created, and watching recursively, then
				// register it and its sub-directories
				if (recursive && (kind == ENTRY_CREATE))
				{
					if (Files.isDirectory(fullPath, NOFOLLOW_LINKS))
					{
						registerAll(fullPath);
					}
				}
			}
			
			if (isInterrupted())
				break;
			
			// reset key and remove from set if directory no longer accessible
			boolean valid = key.reset();
			if (!valid)
			{
				keys.remove(key);

				// all directories are inaccessible
				if (keys.isEmpty())
				{
					break;
				}
			}
		}			
		
	}

	private boolean isInterrupted()
	{
		return future.isCancelled();
	}

	public static void main(String[] args) throws IOException, InterruptedException
	{
		WatchDirArguments wda = WatchDirArguments.create(args, appName);
		if (wda == null)
		{
			System.exit(-1);
		}

        PropertyConfigurator.configure(wda.log4j);
		
		WatchDir wd = new WatchDir(new PathListener()
		{
			public void PathEvent(Path file, Kind<?> kind)
			{
		       logger.info(String.format("%s %s\n", file, kind));
			}
		});
		
		// register directory and process its events
		for(String dir : wda.directories)
		{
			Path directory = Paths.get(dir);
			wd.registerDirectoryToWatch(directory);			
		}
		wd.start();
	}

	@Override
    public void run()
    {
		String name = Thread.currentThread().getName();
		Thread.currentThread().setName("thread-WatchDir");

		try
		{
			proccessEvents();
		}
		catch(InterruptedException e)
		{
			logger.debug("Interrupted");
			Thread.currentThread().interrupt();
		}
		catch(Exception e)
		{
			logger.error("Unknown exception", e);
		}
		
		logger.info("Setting isRunning to false");			
	    Thread.currentThread().setName(name);
		isRunning = false;
    }

}
