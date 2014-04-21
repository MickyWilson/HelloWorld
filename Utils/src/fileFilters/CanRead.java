package fileFilters;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CanRead implements FileFilter
{

	@Override
	public boolean accept(Path test)
	{
		try(FileInputStream s = new FileInputStream(test.toString()))
        {
        }
        catch (IOException e)
        {
        	return false;
        }
		return true;
	}
	
	static public void main(String [] args)
	{
		CanLock cr = new CanLock();
		Path file = Paths.get("c://temp//text.txt");
		while(cr.accept(file))
		{
			System.out.print(".");
		}
		System.out.println(file.toString());
		
	}

}
