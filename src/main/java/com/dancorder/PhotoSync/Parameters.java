package com.dancorder.PhotoSync;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


class Parameters {

	private Path path1 = null;
	private Path path2 = null;
	private boolean isValid = true;
	private String errorMessage = "";
	
	Parameters(String[] args) {

		try {
			CommandLine line = getCommandLine(args);
			
			getPaths(line.getArgs());
		}
		catch (Exception e) {
			isValid = false;
			errorMessage = e.getMessage();
		}
	}
	
	public void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		// TODO: Improve this and make it testable. Commons.CLI makes it hard to just get a string of the usage message.
		formatter.printHelp( "PhotoSync <path1> <path2>", new Options(), true);
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
	
	String getErrorMessage() {
		return errorMessage;
	}
	
	boolean isValid() {
		return isValid;
	}
	
	private CommandLine getCommandLine(String[] args) throws ParseException {
		return new BasicParser().parse(new Options(), args);
	}
	
	private void validatePath(Path path) throws UsageException {
		if (!path.toFile().exists() || !path.toFile().isDirectory()) {
			throw new UsageException("Path must be an existing directory: " + path);
		}		
	}

	Path getPath1() {
		return path1;
	}
	
	Path getPath2() {
		return path2;
	}

}
