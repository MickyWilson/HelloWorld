package fileFilters.standardFilters;

import java.nio.file.Path;
import java.nio.file.Paths;

import fileFilters.FileFilter;

public class EndsWith implements FileFilter
{

	private final Path path;
	
	public EndsWith(Path path)
	{
		this.path = path;
	}

	public EndsWith(String path)
	{
		this(Paths.get(path));
	}
	
	@Override
	public boolean accept(Path test)
	{
		return test.endsWith(path);
	}

}
