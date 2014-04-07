
public class CsvSplitter implements
        LineProcessor
{

    boolean done = false;
    @Override
    public void processLine(String line)
    {
        String [] result = line.split(",",8);
        if(!done)
        {
            done = true;
            Utils.print(result);
        }
    }

}
