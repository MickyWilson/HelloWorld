package directoryWatcher;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;

public class ThreadWaitingFutureTask<T> extends FutureTask<T>
{

	private final Semaphore	semaphore;

	public ThreadWaitingFutureTask(Callable<T> callable)
	{
		this(callable, new Semaphore(1));
	}

	public ThreadWaitingFutureTask(final Runnable runnable)
	{
		this(new Callable<T>()
		{
			@Override
            public T call() throws Exception
            {
				runnable.run();
	            return null;
            }
		}, new Semaphore(1));
	}

	public T getWithJoin() throws InterruptedException, ExecutionException 

	{
		try
		{
			return super.get();
		}
		catch (CancellationException e)
		{
			semaphore.acquire();
			semaphore.release();
//			throw e; // why throw this when this is reason for this class 
			return null;
		}
	}

	private ThreadWaitingFutureTask(final Callable<T> callable, final Semaphore semaphore)
	{
		super(new Callable<T>()
		{
			public T call() throws Exception
			{
				semaphore.acquire();
				try
				{
					return callable.call();
				}
				finally
				{
					semaphore.release();
				}
			}
		});
		this.semaphore = semaphore;
	}
}