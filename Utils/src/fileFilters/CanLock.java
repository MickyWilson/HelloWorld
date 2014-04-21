package fileFilters;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import org.apache.log4j.Logger;

public class CanLock implements FileFilter
{
	static Logger logger = Logger.getLogger(CanLock.class);
	
	static boolean canLock(String file) 
	{
        try(RandomAccessFile raf = new RandomAccessFile(file, "rw"))
        {
			try(java.nio.channels.FileLock lock = raf.getChannel().lock())
			{
			}			
        }
        catch (IOException e)
        {
    		logger.error("Err " + file, e);        	
        	return false;
        }
        return true;
	}
	
	@Override
    public boolean accept(Path test)
    {
	    return CanLock.canLock(test.toString());
    }
}
