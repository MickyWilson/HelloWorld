package notifier;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class MainRunWatcher
{
    private static String appName = new Object(){}.getClass().getEnclosingClass().getName();    
    private static final Logger logger = Logger.getLogger(RunWatcher.class);

    static public void main(String args[]) throws IOException
    {
        System.out.println("Running from " + Paths.get(".").toAbsolutePath().normalize().toString());
        RunWatcherArguments rwa = RunWatcherArguments.create(args, appName);
        if (rwa == null)
        {
            System.exit(-1);
        }
        
        PropertyConfigurator.configure(rwa.log4j);
        logger.info("Starting RunWatcher.Main");
        
        Path directory = Paths.get(rwa.directory);
        RunWatcher rw = new RunWatcher(directory, new BatchFileExecutor(rwa.batchFile, logger));
        rw.startWatchingRun();
        logger.info("Finished Main");
        
    }

}
