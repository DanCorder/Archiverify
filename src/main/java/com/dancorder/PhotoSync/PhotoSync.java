/**
 * 
 */
package com.dancorder.PhotoSync;

import java.io.IOException;
import java.util.List;

import com.dancorder.PhotoSync.ParallelFileTreeWalker.ParallelFileTreeWalker;

/**
 * @author Dan
 *
 */
public class PhotoSync {

	/**
	 * @param args
	 * @throws UsageException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws UsageException, IOException {
		Parameters params = new Parameters(args);

		SynchingVisitor visitor = new SynchingVisitor(params.getPath1(), params.getPath2());
		ParallelFileTreeWalker walker = new ParallelFileTreeWalker(params.getPath1(), params.getPath2(), visitor);
		
		walker.walk();
		
		List<Action> actions = visitor.getActions();
		
		for (Action action :actions) {
			System.out.println(action.toString());
		}
	}

}
