import java.util.StringTokenizer;


public class TokenSplitter implements
        LineProcessor
{
    String [] result = new String[8];
    boolean done = false;
    
    @Override
    public void processLine(String line)
    {
        StringTokenizer st = new StringTokenizer(line, ",");
        int i = 0;
        while(st.hasMoreTokens() && i < 8)
        {
            result[i] = st.nextToken();
            ++i;
        }
        if(!done)
        {
            done = true;
            for(int j = 0; j != result.length; ++j)
                System.out.println(j + " " + result[j]);
        }
     }

}
