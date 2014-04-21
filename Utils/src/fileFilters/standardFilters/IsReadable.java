package fileFilters.standardFilters;

import java.nio.file.Files;
import java.nio.file.Path;

import fileFilters.FileFilter;

public class IsReadable implements FileFilter
{

	@Override
	public boolean accept(Path test)
	{
		return Files.isReadable(test);
	}

}
