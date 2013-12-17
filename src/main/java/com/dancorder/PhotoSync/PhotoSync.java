package com.dancorder.PhotoSync;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import com.dancorder.PhotoSync.ParallelFileTreeWalker.ParallelFileTreeWalker;

public class PhotoSync {

	public static void main(String[] args) throws UsageException, IOException {
		//TODO: Test/refactor this method
		Parameters params = new Parameters(args);

		SynchingVisitor visitor = new SynchingVisitor(params.getPath1(), params.getPath2());
		ParallelFileTreeWalker walker = new ParallelFileTreeWalker(params.getPath1(), params.getPath2(), visitor);
		
		walker.walk();
		
		List<Action> actions = visitor.getActions();
		
		System.out.println("Actions found:");
		for (Action action :actions) {
			System.out.println(action.toString());
		}
		
		System.out.println("Execute actions (y/n)?");
		String response;
		try (Scanner scanIn = new Scanner(System.in)) {
			do {
			    response = scanIn.nextLine();
			} while (!response.equals("y") && !response.equals("n"));
		}
		
		if (response.equals("y")) {
			for (Action action :actions) {
				System.out.println("Executing: " + action.toString());
				action.doAction();
			}
		}
	}
}
