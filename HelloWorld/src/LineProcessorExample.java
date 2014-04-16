import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LineProcessorExample
{

    public static void main(String[] args)
    {
        
        run(new NullReader());
        run(new NullReader());
        
        run(new CustomSplitter());        
        run(new CsvSplitter());
        run(new NullReader());
        run(new TokenSplitter());
        run(new RegexSplitter());
    }

    static public void run(LineProcessor lp)
    {
        System.out.println(lp.getClass().getName() + " started.");
        
        String csvFile = MyWriter.filename;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile)))
        {
            String line = "";
            int i = 0;
            long startTime = System.currentTimeMillis();
            while ((line = br.readLine()) != null)
            {
                ++i;
                lp.processLine(line);
            }
            long endTime = System.currentTimeMillis();
            System.out.println(lp.getClass().getName() + " " + i + " took " + (endTime - startTime)/1000.0
                    + "s");

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println("Done");
    }

}
