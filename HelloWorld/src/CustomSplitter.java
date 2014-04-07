
public class CustomSplitter implements
        LineProcessor
{

    final int size = 8;
    String [] result = new String[size];
    boolean done = false;
    
    @Override
    public void processLine(String line)
    {
        int pos0 = 0;
        for(int i = 0; i != size - 1; ++i)
        {
            int pos1 = line.indexOf(",", pos0);
            result[i] = line.substring(pos0, pos1);
            pos0 = pos1+1;
        }
        result[size-1] = line.substring(pos0);
        if(!done)
        {
            done = true;
            Utils.print(result);
        }
    }

}
