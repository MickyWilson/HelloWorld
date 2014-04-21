package mike.filter;

public class AndFilter<T> implements Filter<T> 
{
	final private Filter<T> filter1;
	final private Filter<T> filter2;
	
	public AndFilter(Filter<T> filter1, Filter<T> filter2)
	{
		this.filter1 = filter1;
		this.filter2 = filter2;
	}
	
	@Override
	public boolean accept(T test) 
	{
		return filter1.accept(test) && filter2.accept(test);
	}

}
