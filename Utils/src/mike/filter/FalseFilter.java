package mike.filter;

public class FalseFilter<T> implements Filter<T> 
{

	@Override
	public boolean accept(T test) 
	{

		return false;
	}

}
