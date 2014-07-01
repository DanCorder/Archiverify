//    Archiverify is an archive synching and verification tool
//    Copyright (C) 2014  Daniel Corder (contact: archiverify@dancorder.com)
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.dancorder.Archiverify;

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
	private static final String OPTION_SINGLE_DIRECTORY_MODE = "s";
	
	Parameters(String[] args) {
		try {
			options = getOptions();
			commandLine = getCommandLine(args, options);
			getPaths(commandLine.getArgs());
			validateOptions();
		}
		catch (Exception e) {
			isValid = false;
			errorMessage = e.getMessage();
		}
	}
	
	private void validateOptions() throws Exception {
		if (getIsSingleDirectoryMode()) {
			if (path1 == null) {
				throw new Exception("No directory supplied");
			}
			if (path2 != null) {
				throw new Exception("Only supply one directory in single directory mode");
			}
			
			validatePath(path1);
		}
		else {
			if (path1 == null || path2 == null) {
				throw new Exception("Two directories must be supplied");
			}
			
			validatePath(path1);
			validatePath(path2);
		}
	}

	void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		// TODO: Improve this and make it testable. Commons.CLI makes it hard to just get a string of the usage message.
		formatter.printHelp( "Archiverify [options] <path1> [path2]", options);
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
	
	boolean getIsSingleDirectoryMode() {
		return commandLine.hasOption(OPTION_SINGLE_DIRECTORY_MODE);
	}
	
	private Options getOptions() {
		Options options = new Options();
		options.addOption(new Option(OPTION_EXECUTE_ACTIONS, false, "Automatically execute all found actions"));
		options.addOption(new Option(OPTION_SINGLE_DIRECTORY_MODE, false, "Generate and check hashes for a single directory"));

		return options;
	}
	
	private CommandLine getCommandLine(String[] args, Options options) throws ParseException {
		return new BasicParser().parse(options, args);
	}
	
	private void getPaths(String[] remainingArgs) throws Exception {
		if (remainingArgs.length > 0) {
			path1 = Paths.get(remainingArgs[0]);
		}
		if (remainingArgs.length > 1) {
			path2 = Paths.get(remainingArgs[1]);
		}
	}
	
	private void validatePath(Path path) throws Exception {
		if (!path.toFile().exists() || !path.toFile().isDirectory()) {
			throw new Exception("Path must be an existing directory: " + path);
		}		
	}
}
