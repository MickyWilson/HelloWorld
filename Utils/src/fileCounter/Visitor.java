package fileCounter;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import mike.filter.Filter;
import mike.filter.TrueFilter;

final class Visitor extends SimpleFileVisitor<Path>
{
	private final FileProcessor fileProcessor;
	private final Filter<Path> filter;
	
	public Visitor(FileProcessor fileProcessor, Filter<Path> filter)
	{
		this.fileProcessor = fileProcessor;
		this.filter = filter;		
	}

	public Visitor(FileProcessor fileProcessor)
	{
		this(fileProcessor, new TrueFilter<Path>());
	}
	
	static public void visit(Path rootDirectory, FileProcessor fileProcessor, Filter<Path> filter) throws IOException
	{
		Visitor v = new Visitor(fileProcessor, filter);
		Files.walkFileTree(rootDirectory, v);
	}

	static public void visit(Path rootDirectory, FileProcessor fileProcessor) throws IOException
	{
		visit(rootDirectory, fileProcessor, new TrueFilter<Path>());
	}

	@Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
    {
    	if (filter.accept(file))
    	{
    		fileProcessor.processPath(file);
    	}
        return super.visitFile(file, attrs);
    }
}