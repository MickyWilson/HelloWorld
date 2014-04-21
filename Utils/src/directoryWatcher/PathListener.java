package directoryWatcher;

import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;

public interface PathListener
{
	void PathEvent(Path file, Kind<?> kind);
	
}
