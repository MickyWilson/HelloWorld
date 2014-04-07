import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class HelloWorld
{

    public static void main(String[] args)
    {
        run(new CsvSplitter());
        run(new TokenSplitter());
    }

    static public void run(LineProcessor lp)
    {
        System.out.println(lp.getClass().getName() + " started.");
        
        String csvFile = "c:/temp/log.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile)))
        {
            String line = "";
            long startTime = System.currentTimeMillis();
            while ((line = br.readLine()) != null)
            {
                lp.processLine(line);
            }
            long endTime = System.currentTimeMillis();
            System.out.println(lp.getClass().getName() + " took " + (endTime - startTime)
                    + " milliseconds");

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
