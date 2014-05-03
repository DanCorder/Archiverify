package com.dancorder.PhotoSync;

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
	private Path errorPath;
	
	SynchingVisitor(SyncLogic logic, FileHashStoreFactory factory, Path root1, Path root2) {
		syncLogic = logic;
		fileHashStoreFactory = factory;
		this.root1 = root1;
		this.root2 = root2;
	}

	@Override
	public void preVisitDirectory(Path relativeDirectoryPath, FileExistence existence) {
		try
		{
			if (isNotInErrorPath(relativeDirectoryPath))
			{
				errorPath = null;
				
				Path directory1 = root1.resolve(relativeDirectoryPath);
				Path directory2 = root2.resolve(relativeDirectoryPath);
				
				hashStore1 = fileHashStoreFactory.createFileHashStore(directory1);
				hashStore2 = fileHashStoreFactory.createFileHashStore(directory2);
				
				List<Action> newActions = syncLogic.compareDirectories(directory1, directory2, existence);
				
				actions.addAll(newActions);
			}
		}
		catch (Exception e)
		{
			errorPath = relativeDirectoryPath;
			
			actions.add(
				new WarningAction(
					String.format("Error caught visiting directory %s this directory will not be synched. %s", relativeDirectoryPath, e)));
		}
	}
	
	@Override
	public void postVisitDirectory(Path relativeDirectoryPath, FileExistence existence) {
		try
		{
			if (isNotInErrorPath(relativeDirectoryPath))
			{
				List<Action> newActions = syncLogic.checkHashStores(hashStore1, hashStore2);
				actions.addAll(newActions);
			}
		}
		catch (Exception e)
		{
			errorPath = relativeDirectoryPath;
			
			actions.add(
				new WarningAction(
					String.format("Error caught ending visit of directory %s. Hashes in this directory will not be synched. %s", relativeDirectoryPath, e)));
		}
	}

	@Override
	public void visitFile(Path relativeFilePath, FileExistence existence) {
		try
		{
			if (isNotInErrorPath(relativeFilePath) && isNotHashFile(relativeFilePath))
			{
				Path file1 = root1.resolve(relativeFilePath);
				Path file2 = root2.resolve(relativeFilePath);
				
				List<Action> newActions = syncLogic.compareFiles(file1, hashStore1, file2, hashStore2);
				actions.addAll(newActions);
			}
		}
		catch (Exception e)
		{
			actions.add(
				new WarningAction(
					String.format("Error caught visiting file %s this file will not be synched. %s", relativeFilePath, e)));
		}
	}

	private boolean isNotHashFile(Path relativeFilePath) {
		return !HashFileSource.HASH_FILE_NAME.equals(relativeFilePath.getFileName().toString());
	}

	private boolean isNotInErrorPath(Path relativeFilePath) {
		return errorPath == null || !relativeFilePath.startsWith(errorPath);
	}
	
	List<Action> getActions() {
		return actions;
	}
}
