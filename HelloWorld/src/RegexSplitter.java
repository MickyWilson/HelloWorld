import java.util.regex.Pattern;


public class RegexSplitter implements
        LineProcessor
{
    Pattern pattern = Pattern.compile(",");
    boolean done = false;
    @Override
    public void processLine(String line)
    {
        String [] result = pattern.split(line);
        if(!done)
        {
            done = true;
            Utils.print(result);
        }
    }

}
