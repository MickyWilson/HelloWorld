package mike.filter;

public class NotFilter<T> implements Filter<T> {

	final private Filter<T> filter;
	
	public NotFilter(Filter<T> filter)
	{
		this.filter = filter;
	}
	
	@Override
	public boolean accept(T test) 
	{
		return !filter.accept(test);
	}

}
