package notifier;

import java.nio.file.Path;

public interface RunWatcherListener
{
	void ready(Path rootDirectory);
}
