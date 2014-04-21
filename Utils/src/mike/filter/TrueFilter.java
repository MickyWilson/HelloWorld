package mike.filter;

public class TrueFilter<T> implements Filter<T> {

	@Override
	public boolean accept(T test) 
	{
		return true;
	}

}
