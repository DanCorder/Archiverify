package com.dancorder.PhotoSync;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import com.dancorder.PhotoSync.ParallelFileTreeWalker.ParallelFileTreeWalker;

public class PhotoSync {

	public static void main(String[] args) throws Exception {
		
		Parameters params = new Parameters(args);

		SynchingVisitor visitor = new SynchingVisitor(new SyncLogic(new FileHashGenerator()), new FileHashStoreFactory(), params.getPath1(), params.getPath2());
		ParallelFileTreeWalker walker = new ParallelFileTreeWalker(params.getPath1(), params.getPath2(), visitor);
		
		walker.walk();
		
		List<Action> actions = visitor.getActions();
		
		System.out.println("Actions found:");
		for (Action action :actions) {
			System.out.println(action.toString());
		}
		
		System.out.println("Execute actions (y/n)?");
		String response = getAnswerFromUser();
		
		if (response.equals("y")) {
			executeActions(actions);
		}
	}

	private static void executeActions(List<Action> actions) throws IOException {
		for (Action action :actions) {
			if (!(action instanceof WarningAction)) {
				System.out.println("Executing: " + action.toString());
				action.doAction();
			}
		}
	}

	private static String getAnswerFromUser() {
		String response;
		try (Scanner scanIn = new Scanner(System.in)) {
			do {
			    response = scanIn.nextLine();
			} while (!response.equals("y") && !response.equals("n"));
		}
		return response;
	}
}
