package com.dancorder.PhotoSync;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.dancorder.PhotoSync.Action.Action;
import com.dancorder.PhotoSync.Action.CreateDirectoryAction;
import com.dancorder.PhotoSync.Action.FileCopyAction;
import com.dancorder.PhotoSync.ParallelFileTreeWalker.FileExistence;
import com.dancorder.PhotoSync.ParallelFileTreeWalker.ParallelFileTreeVisitor;

class SynchingVisitor implements ParallelFileTreeVisitor {

	private final Path root1;
	private final Path root2;
	private final ArrayList<Action> actions = new ArrayList<Action>();
	
	SynchingVisitor(Path root1, Path root2) {
		this.root1 = root1;
		this.root2 = root2;
	}

	@Override
	public void preVisitDirectory(Path directoryPath, FileExistence existence) {
		if (existence == FileExistence.Path1Only) {
			actions.add(new CreateDirectoryAction(root2.resolve(directoryPath)));
		}
		else if (existence == FileExistence.Path2Only) {
			actions.add(new CreateDirectoryAction(root1.resolve(directoryPath)));
		}
	}

	@Override
	public void visitFile(Path filePath, FileExistence existence) {
		if (existence == FileExistence.Path1Only) {
			actions.add(new FileCopyAction(root1.resolve(filePath), root2.resolve(filePath)));
		}
		else if (existence == FileExistence.Path2Only) {
			actions.add(new FileCopyAction(root2.resolve(filePath), root1.resolve(filePath)));
		}
	}
	
	List<Action> getActions() {
		return actions;
	}
}
