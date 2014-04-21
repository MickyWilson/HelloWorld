package directoryWatcher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class TestConcurrency implements Runnable
{
	public static void main(String args[]) throws InterruptedException
	{
		ExecutorService e = Executors.newFixedThreadPool(1);
		TestConcurrency tc = new TestConcurrency();
		FutureTask<Void> future =new FutureTask<Void>(tc, null);
		e.submit(future);
		Thread.sleep(1000);
		future.cancel(true);
		System.out.println(future.isCancelled() + " " + future.isDone());
		while(!future.isDone())
		{
			System.out.println("main sleeping");
			Thread.sleep(1000);
			System.out.println(future.isCancelled() + " " + future.isDone());			
		}
		System.out.println("Done");
	}

	@Override
    public void run()
    {
		try
        {
	        Thread.sleep(10000);
        }
        catch (InterruptedException e)
        {
        	System.out.println("Interrupted");
        }
		
		System.out.println("Sleep again");
		try
        {
	        Thread.sleep(3000);
        }
        catch (InterruptedException e)
        {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		System.out.println("TC.run done");
    }
}
