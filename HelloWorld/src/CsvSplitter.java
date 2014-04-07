
public class CsvSplitter implements
        LineProcessor
{

    @Override
    public void processLine(String line)
    {
        @SuppressWarnings("unused")
        String [] result = line.split(",",7);
    }

}
