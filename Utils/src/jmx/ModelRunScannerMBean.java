package jmx;

import java.io.IOException;

public interface ModelRunScannerMBean
{
	void startScanning() throws IOException;
	void stopScanning() throws IOException;
	void setDirectory(String directory)  throws IOException;
	String getDirectory() throws IOException;
	
	String getStatus() throws IOException;	
	int getNumberOfRuns() throws IOException;
	int getNumberOfRunsCompleted() throws IOException;
	
}
