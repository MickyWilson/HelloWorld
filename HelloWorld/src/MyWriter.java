import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;


public class MyWriter
{

    static String filename = "C:\\temp\\log.txt";
    public static void main(String[] args)
    {
        long startTime = System.currentTimeMillis();

        run();
        
        long endTime = System.currentTimeMillis();

        System.out.println("That took " + (endTime - startTime) + " milliseconds");
    }
   
    static void run()
    {
        final long c = 1*1000000;
        
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename))))
        {
            for(long i = 0; i != c; ++i)
                writer.write("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30\n");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
    }
}

