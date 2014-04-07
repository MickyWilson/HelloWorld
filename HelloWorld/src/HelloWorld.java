import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class HelloWorld
{

    public static void main(String[] args)
    {
        System.out.println("Hello World");
        run();
    }

    static String f(String line)
    {
        StringTokenizer st = new StringTokenizer(line, ",");
        String s = null;
        while(st.hasMoreTokens())
        {
            s = st.nextToken();
        }
        return s;
    }

    static public void run(LineProcessor lp) {
        
        String csvFile = "c:/temp/log.txt";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
     
        try {
     
            br = new BufferedReader(new FileReader(csvFile));
            long startTime = System.currentTimeMillis();
            while ((line = br.readLine()) != null) 
            {
                lp.processLine(line);
            }
            long endTime = System.currentTimeMillis();
            System.out.println("That took " + (endTime - startTime) + " milliseconds");
     
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
     
        System.out.println("Done");
      }
    
    
}
