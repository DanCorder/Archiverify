package com.dancorder.PhotoSync;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


class Parameters {

	private Path path1 = null;
	private Path path2 = null;
	private boolean isValid = true;
	private String errorMessage = "";
	private Options options = null;
	private CommandLine commandLine = null;
	
	private static final String OPTION_EXECUTE_ACTIONS = "y";
	
	Parameters(String[] args) {
		try {
			options = getOptions();
			commandLine = getCommandLine(args, options);
			
			getPaths(commandLine.getArgs());
		}
		catch (Exception e) {
			isValid = false;
			errorMessage = e.getMessage();
		}
	}
	
	void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		// TODO: Improve this and make it testable. Commons.CLI makes it hard to just get a string of the usage message.
		formatter.printHelp( "PhotoSync [options] <path1> <path2>", options);
	}
	
	boolean isValid() {
		return isValid;
	}
	
	String getErrorMessage() {
		return errorMessage;
	}

	Path getPath1() {
		return path1;
	}
	
	Path getPath2() {
		return path2;
	}
	
	boolean getExecuteActions() {
		return commandLine.hasOption(OPTION_EXECUTE_ACTIONS);
	}
	
	private Options getOptions() {
		Options options = new Options();
		Option executeActions = new Option(OPTION_EXECUTE_ACTIONS, false, "Automatically execute all found actions");
		
		options.addOption(executeActions);
		return options;
	}
	
	private CommandLine getCommandLine(String[] args, Options options) throws ParseException {
		return new BasicParser().parse(options, args);
	}
	
	private void getPaths(String[] remainingArgs) throws UsageException {
		if (remainingArgs == null || remainingArgs.length != 2)
		{
			throw new UsageException("Two directories must be supplied");
		}
		
		path1 = Paths.get(remainingArgs[0]);
		path2 = Paths.get(remainingArgs[1]);
		
		validatePath(path1);
		validatePath(path2);
	}
	
	private void validatePath(Path path) throws UsageException {
		if (!path.toFile().exists() || !path.toFile().isDirectory()) {
			throw new UsageException("Path must be an existing directory: " + path);
		}		
	}
}
