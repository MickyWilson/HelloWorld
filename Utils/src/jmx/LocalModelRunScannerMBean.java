package jmx;


public interface LocalModelRunScannerMBean extends ModelRunScannerMBean
{

	void startScanning();
	void stopScanning();
	void setDirectory(String directory);
	String getDirectory();
	String getStatus();

}
