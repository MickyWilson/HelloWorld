package notifier;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

public class BatchFileExecutor implements
        RunWatcherListener
{

    private final String batchFile;
    private final Logger logger;
    private AtomicBoolean executed = new AtomicBoolean(false);
    
    public BatchFileExecutor(String batchFile, Logger logger)
    {
        this.batchFile = batchFile;
        this.logger = logger;
    }
    
    @Override
    public void ready(Path rootDirectory)
    {
        logger.info("Ready:" + rootDirectory);
        if (batchFile == null)
            return;

        String cmd = "cmd /c start " + batchFile + " " + rootDirectory;
        
        if (!executed.compareAndSet(false, true))
        {
            logger.info("Ready signal received but already run [" + cmd + "]");
            return;
        } 
        try
        {
            logger.info("Executing [" + cmd + "]");
            Runtime.getRuntime().exec(cmd);
        }
        catch (IOException e)
        {
            logger.error("Error executing command " + cmd, e);
        }            
    }

}
