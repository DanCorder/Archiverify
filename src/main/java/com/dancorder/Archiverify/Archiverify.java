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

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

import com.dancorder.Archiverify.ParallelFileTreeWalker.ParallelFileTreeWalker;

public class Archiverify {

	public static void main(String[] args) throws Exception {

		Parameters params = new Parameters(args);
		
		if (!params.isValid()) {
			Logger.logError(params.getErrorMessage());
			params.printUsage();
			System.exit(1);
		}
		
		List<Action> actions;
		
		FileHashStoreFactory fileHashStoreFactory = new FileHashStoreFactory(params.getReadFile(), params.getWriteFile());

		if (params.getIsSingleDirectoryMode()) {
			HashCheckingVisitor visitor = new HashCheckingVisitor(fileHashStoreFactory, new FileHashGenerator(), params.getExcludedExtensions());
			Files.walkFileTree(params.getPath1(), visitor);
			actions = visitor.getActions();
		} else {
			SynchingVisitor visitor = new SynchingVisitor(new SyncLogic(new FileHashGenerator()), fileHashStoreFactory, params.getExcludedExtensions(), params.getPath1(), params.getPath2());
			ParallelFileTreeWalker walker = new ParallelFileTreeWalker(params.getPath1(), params.getPath2(), visitor);
			walker.walk();
			actions = visitor.getActions();
		}
		
		actions = sortActions(actions);
		
		printActions(actions);
		
		if (containsNonWarningActions(actions) && (params.getExecuteActions() || getAnswerFromUser("Execute actions?"))) {
			executeActions(actions);
		}
	}

	private static List<Action> sortActions(List<Action> actions) {
		ActionSorter sorter = new ActionSorter(actions);
		return sorter.sortForDisplay();
	}

	private static void printActions(List<Action> sortedActions) {
		if (sortedActions.size() > 0) {
			Logger.log("Actions found:");
			for (Action action : sortedActions) {
				if (action instanceof WarningAction) {
					Logger.logWarning(action.toString());
				}
				else {
					Logger.log(action.toString());
				}
			}
		}
		else {
			Logger.log("Nothing to do");
		}
	}

	private static boolean containsNonWarningActions(List<Action> sortedActions) {
		if (sortedActions.size() == 0) {
			return false;
		}
		
		Action lastAction = sortedActions.get(sortedActions.size() - 1);
		return !(lastAction instanceof WarningAction);
	}

	private static void executeActions(List<Action> actions) throws IOException {
		for (Action action :actions) {
			if (!(action instanceof WarningAction)) {
				Logger.log("Executing: " + action.toString());
				tryExecuteAction(action);
			}
		}
	}
	
	private static void tryExecuteAction(Action action) {
		try {
			action.doAction();
		}
		catch (Exception e) {
			Logger.logError("Problem executing action: " + e.getMessage());
		}
	}

	private static boolean getAnswerFromUser(String question) {
		System.out.println(question + " (y/n)");
		String response;
		try (Scanner scanIn = new Scanner(System.in)) {
			do {
			    response = scanIn.nextLine();
			} while (!response.equals("y") && !response.equals("n"));
		}
		return response.equals("y");
	}
}
