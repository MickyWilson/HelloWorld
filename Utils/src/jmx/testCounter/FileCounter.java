package jmx.testCounter;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class FileCounter 
{
	private int count = 0;
	static int N = 100;
	static CountDownLatch start = new CountDownLatch(1);
	static CountDownLatch end = new CountDownLatch(N);
		
	class Counter
	{
        public int processPath()
        {
	        return ++count;
        }
	}
	
	public int countFiles() 
	{
		count = 0;
		processCounter( new Counter());
		return count;
	}
	
	static void processCounter(Counter c)
	{
		for(int i = 0; i != 10; ++i)
		{
			c.processPath();
		}
	}

	static FileCounter FC = new FileCounter();
	
	static int max = 0;
	static int min = 100000000;
	
	static class Tester implements Runnable
	{
		@Override
        public void run()
        {
			try
            {
	            start.await();
            }
            catch (InterruptedException e)
            {
	            e.printStackTrace();
            }
			int m = FC.countFiles();
			if (m > max) max = m;
			if (m < min) min = m;
			end.countDown();
        }	
	}
	
	static public void main(String [] args) throws IOException, InterruptedException
	{
		System.out.println("S");
		ExecutorService e = Executors.newFixedThreadPool(N);
		for(int i = 0; i != N; ++i)
		{
			e.execute(new Tester());
		}
		start.countDown();
		end.await();
		System.out.println(max);
		System.out.println(min);
		e.shutdown();
		
	}
}
