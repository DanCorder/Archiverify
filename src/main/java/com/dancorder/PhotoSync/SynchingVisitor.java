package com.dancorder.PhotoSync;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.dancorder.PhotoSync.ParallelFileTreeWalker.FileExistence;
import com.dancorder.PhotoSync.ParallelFileTreeWalker.ParallelFileTreeVisitor;

class SynchingVisitor implements ParallelFileTreeVisitor {

	private final Path root1;
	private final Path root2;
	private final ArrayList<Action> actions = new ArrayList<Action>();
	private final SyncLogic syncLogic;
	private final FileHashStoreFactory fileHashStoreFactory;
	private FileHashStore hashStore1;
	private FileHashStore hashStore2;
	
	SynchingVisitor(SyncLogic logic, FileHashStoreFactory factory, Path root1, Path root2) {
		syncLogic = logic;
		fileHashStoreFactory = factory;
		this.root1 = root1;
		this.root2 = root2;
	}

	@Override
	public void preVisitDirectory(Path relativeDirectoryPath, FileExistence existence) throws Exception {
		Path directory1 = root1.resolve(relativeDirectoryPath);
		Path directory2 = root2.resolve(relativeDirectoryPath);
		
		List<Action> newActions = syncLogic.compareDirectories(directory1, directory2, existence);
		actions.addAll(newActions);
		
		hashStore1 = fileHashStoreFactory.createFileHashStore(directory1);
		hashStore2 = fileHashStoreFactory.createFileHashStore(directory2);
	}

	@Override
	public void visitFile(Path relativeFilePath, FileExistence existence) throws IOException {
		Path file1 = root1.resolve(relativeFilePath);
		Path file2 = root2.resolve(relativeFilePath);
		
		List<Action> newActions = syncLogic.compareFiles(file1, hashStore1, file2, hashStore2);
		actions.addAll(newActions);
	}
	
	List<Action> getActions() {
		return actions;
	}
}
