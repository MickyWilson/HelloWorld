package notifier;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class RunWatcherArguments 
{
    @Parameter(names = {"-h", "?", "--help"}, help = true, 
    		description = "Display this help")
    public boolean help = false;

    @Parameter(names = {"-l", "--log4j"}, required = false, 
            description = "Log4j properties file")
    public String log4j = "log4j.properties";
    
    @Parameter(names = {"-d", "--directory"}, required = true,
    		description = "Run directory to watch ")
    public String directory;

    @Parameter(names = {"-b", "--batch"}, required = false, 
            description = "Batch file to execute")
    public String batchFile = null;

    static RunWatcherArguments create(String strArgs[], String appName)
    {
		RunWatcherArguments args = new RunWatcherArguments();
		JCommander commander = new JCommander(args);
		commander.setAllowAbbreviatedOptions(true);
		commander.setCaseSensitiveOptions(false);
		commander.setProgramName(appName);
		
		try
		{
			commander.parse(strArgs);	
		}
		catch(ParameterException e)
		{
			System.out.println(e.getMessage() +"\n");
			args.help = true;
		}
		
		if (args.help)
		{
			commander.usage();
			System.out.println("All options marked with a '*' are mandatory");
			return null;
		}
		return args;
    }
}
