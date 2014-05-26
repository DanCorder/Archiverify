package com.dancorder.PhotoSync;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

import com.dancorder.PhotoSync.ParallelFileTreeWalker.ParallelFileTreeWalker;

class PhotoSync {

	public static void main(String[] args) throws Exception {

		Parameters params = new Parameters(args);
		
		if (!params.isValid()) {
			System.out.println(params.getErrorMessage());
			params.printUsage();
			System.exit(1);
		}
		
		List<Action> actions;

		if (params.getIsSingleDirectoryMode()) {
			HashCheckingVisitor visitor = new HashCheckingVisitor(new FileHashStoreFactory(), new FileHashGenerator());
			Files.walkFileTree(params.getPath1(), visitor);
			actions = visitor.getActions();
		} else {
			SynchingVisitor visitor = new SynchingVisitor(new SyncLogic(new FileHashGenerator()), new FileHashStoreFactory(), params.getPath1(), params.getPath2());
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
			System.out.println("Actions found:");
			for (Action action : sortedActions) {
				System.out.println(action.toString());
			}
		}
		else {
			System.out.println("Nothing to do");
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
				System.out.println("Executing: " + action.toString());
				tryExecuteAction(action);
			}
		}
	}
	
	private static void tryExecuteAction(Action action) {
		try {
			action.doAction();
		}
		catch (Exception e) {
			System.out.println("Error executing action: " + e.getMessage());
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
