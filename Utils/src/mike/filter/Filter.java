package mike.filter;

public interface Filter<T> 
{
	boolean accept(T test);
}
