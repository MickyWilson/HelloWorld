package fileCounter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import fileFilters.FileFilter;
import fileFilters.standardFilters.EndsWith;


public class FileCounter
{
		
	class Counter implements FileProcessor
	{
		int count = 0;
		
		@Override
        public int processPath(Path path)
        {
	        return ++count;
        }
	}
	
	public int countFiles(final Path rootDirectory, final FileFilter fileFilter) throws IOException
	{
		Counter counter = new Counter();
		Visitor.visit(rootDirectory, counter, fileFilter);
		return counter.count;
	}

	public int countFiles(final Path rootDirectory, Path fileName) throws IOException
	{
		return countFiles(rootDirectory, new EndsWith(fileName));
	}

	static public void main(String [] args) throws IOException
	{
		Path p = Paths.get("c:\\xx");
		final Path n = Paths.get("control.dat");
		FileCounter fc = new FileCounter();
		System.out.println(fc.countFiles(p,n));
		
	}
}
