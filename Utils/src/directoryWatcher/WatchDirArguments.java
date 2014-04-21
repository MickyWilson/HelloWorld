package directoryWatcher;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class WatchDirArguments 
{
    @Parameter(names = {"-h", "?", "--help"}, help = true, 
    		description = "Display this help")
    public boolean help = false;
    
    @Parameter(names = {"-d", "--directories"}, required = true, variableArity = true,
    		description = "List of one or more directories to watch ")
    public List<String> directories = new ArrayList<String>();

    static WatchDirArguments create(String strArgs[], String appName)
    {
		WatchDirArguments args = new WatchDirArguments();
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
