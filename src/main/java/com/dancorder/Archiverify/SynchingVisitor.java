package com.dancorder.Archiverify;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.dancorder.Archiverify.ParallelFileTreeWalker.FileExistence;
import com.dancorder.Archiverify.ParallelFileTreeWalker.ParallelFileTreeVisitor;

class SynchingVisitor implements ParallelFileTreeVisitor {

	private final Path root1;
	private final Path root2;
	private final ArrayList<Action> actions = new ArrayList<Action>();
	private final SyncLogic syncLogic;
	private final FileHashStoreFactory fileHashStoreFactory;
	private Path currentRelativeDirectoryPath;
	private Path errorPath;
	private Dictionary<Path, Pair<FileHashStore, FileHashStore>> hashStoresByDirectory = new Hashtable<Path, Pair<FileHashStore, FileHashStore>>();
	private Dictionary<Path, List<Path>> visitedFilesByDirectory = new Hashtable<Path, List<Path>>();
	
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
			Logger.log("Scanning " + relativeDirectoryPath.toString());
			currentRelativeDirectoryPath = relativeDirectoryPath;
			
			if (isNotInErrorPath(relativeDirectoryPath))
			{
				errorPath = null;
				
				Path directory1 = root1.resolve(relativeDirectoryPath);
				Path directory2 = root2.resolve(relativeDirectoryPath);
				
				Pair<FileHashStore, FileHashStore> hashStorePair = Pair.of(fileHashStoreFactory.createFileHashStore(directory1), fileHashStoreFactory.createFileHashStore(directory2));
				
				hashStoresByDirectory.put(relativeDirectoryPath, hashStorePair);
				visitedFilesByDirectory.put(relativeDirectoryPath, new ArrayList<Path>());
				
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
				Pair<FileHashStore, FileHashStore> hashStorePair = hashStoresByDirectory.get(relativeDirectoryPath);
				List<Path> visitedFiles = visitedFilesByDirectory.get(relativeDirectoryPath);
				
				syncLogic.removeUnvisitedHashes(hashStorePair.getLeft(), visitedFiles);
				syncLogic.removeUnvisitedHashes(hashStorePair.getRight(), visitedFiles);
				List<Action> newActions = syncLogic.checkHashStores(hashStorePair.getLeft(), hashStorePair.getRight());
				actions.addAll(newActions);
			}
			
			hashStoresByDirectory.remove(relativeDirectoryPath);
			visitedFilesByDirectory.remove(relativeDirectoryPath);
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
			if (isNotInErrorPath(relativeFilePath) && !HashFileSource.isHashFile(relativeFilePath))
			{
				Path file1 = root1.resolve(relativeFilePath);
				Path file2 = root2.resolve(relativeFilePath);
			
				visitedFilesByDirectory.get(currentRelativeDirectoryPath).add(relativeFilePath.getFileName());
				
				Pair<FileHashStore, FileHashStore> hashStorePair = hashStoresByDirectory.get(currentRelativeDirectoryPath);
				List<Action> newActions = syncLogic.compareFiles(file1, hashStorePair.getLeft(), file2, hashStorePair.getRight());
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

	private boolean isNotInErrorPath(Path relativeFilePath) {
		return errorPath == null || !relativeFilePath.startsWith(errorPath);
	}
	
	List<Action> getActions() {
		return actions;
	}
}
