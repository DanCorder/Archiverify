package com.dancorder.PhotoSync;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dancorder.PhotoSync.ParallelFileTreeWalker.FileExistence;

class SyncLogic {
	
	private final FileHashGenerator hashGenerator;
	
	SyncLogic(FileHashGenerator hashGenerator) {
		this.hashGenerator = hashGenerator;
	}
	
	List<Action> compareDirectories(Path absolutePath1, Path absolutePath2, FileExistence existence) {
		List<Action> actions = new ArrayList<Action>();
		
		if (existence == FileExistence.Path1Only) {
			actions.add(new CreateDirectoryAction(absolutePath2));
		}
		else if (existence == FileExistence.Path2Only) {
			actions.add(new CreateDirectoryAction(absolutePath1));
		}
		
		return actions;
	}
	
	List<Action> checkHashStores(FileHashStore store1, FileHashStore store2) {
		ArrayList<Action> actions = new ArrayList<Action>();
		
		addActionIfDirty(store1, actions);
		addActionIfDirty(store2, actions);

		return actions;
	}
	
	private void addActionIfDirty(FileHashStore store, ArrayList<Action> actions) {
		if (store.isDirty()) {
			actions.add(new UpdateHashesAction(store));
		}
	}
	
	void removeUnvisitedHashes(FileHashStore store, List<Path> visitedFiles) {
		List<Path> filesInStore = store.getFiles();
		
		Set<Path> filesOnlyInStore = new HashSet<Path>();

		for (Path fileInStore : filesInStore) {
			if (!visitedFiles.contains(fileInStore)) {
				filesOnlyInStore.add(fileInStore);
			}
		}
		
		for (Path fileOnlyInStore : filesOnlyInStore) {
			store.removeHash(fileOnlyInStore);
		}
	}
	
	List<Action> compareFiles(Path absoultePath1, FileHashStore store1, Path absolutePath2, FileHashStore store2) throws IOException {
		String hashFromFile1 = hashGenerator.calculateMd5(absoultePath1);
		String hashFromFile2 = hashGenerator.calculateMd5(absolutePath2);
		String hashFromStore1 = store1.getHash(absoultePath1.getFileName());
		String hashFromStore2 = store2.getHash(absolutePath2.getFileName());

		if (allHashesMatch(hashFromFile1, hashFromStore1, hashFromFile2, hashFromStore2)) {
			return new ArrayList<Action>();
		}
		else if (fileAndStoreHashesExistAndMatch(hashFromFile1, hashFromStore1)) {
			return oneFileAndStoreHashMatch(hashFromFile1, hashFromStore1, hashFromFile2, hashFromStore2, absoultePath1, absolutePath2, store1, store2);
		}
		else if (fileAndStoreHashesExistAndMatch(hashFromFile2, hashFromStore2)) {
			return oneFileAndStoreHashMatch(hashFromFile2, hashFromStore2, hashFromFile1, hashFromStore1, absolutePath2, absoultePath1, store2, store1);
		}
		else if (fileAndStoreHashesExistAndDontMatch(hashFromFile1, hashFromStore1)) {
			return mismatchedFileAndStoreHash(hashFromFile1, hashFromStore1, hashFromFile2, hashFromStore2, absoultePath1, absolutePath2);
		}
		else if (fileAndStoreHashesExistAndDontMatch(hashFromFile2, hashFromStore2)) {
			return mismatchedFileAndStoreHash(hashFromFile2, hashFromStore2, hashFromFile1, hashFromStore1, absolutePath2, absoultePath1);
		}
		else if (storeHashExistsButFileDoesnt(hashFromFile1, hashFromStore1)) {
			return missingFileWithStoreHash(hashFromStore1, hashFromFile2, hashFromStore2, absoultePath1, absolutePath2, store1, store2);
		}
		else if (storeHashExistsButFileDoesnt(hashFromFile2, hashFromStore2)) {
			return missingFileWithStoreHash(hashFromStore2, hashFromFile1, hashFromStore1, absolutePath2, absoultePath1, store2, store1);
		}
		else if (fileHashExistsButStoreHashDoesnt(hashFromFile1, hashFromStore1)) {
			return fileWithMissingStoreHash(hashFromFile1, hashFromFile2, hashFromStore2, absoultePath1, absolutePath2, store1, store2);
		}
		else if (fileHashExistsButStoreHashDoesnt(hashFromFile2, hashFromStore2)) {
			return fileWithMissingStoreHash(hashFromFile2, hashFromFile1, hashFromStore1, absolutePath2, absoultePath1, store2, store1);
		}

		throw new RuntimeException(
				String.format("Unforeseen combination of file and store hashes. File1: %s, Store1: %s, File2: %s, Store2: %s",
						hashFromFile1,
						hashFromStore1,
						hashFromFile2,
						hashFromStore2));
	}

	private boolean storeHashExistsButFileDoesnt(String hashFromFile, String hashFromStore) {
		return hashFromFile == null && hashFromStore != null;
	}

	private boolean fileHashExistsButStoreHashDoesnt(String hashFromFile, String hashFromStore) {
		return hashFromFile != null && hashFromStore == null;
	}

	private boolean fileAndStoreHashesExistAndDontMatch(String hashFromFile, String hashFromStore) {
		return hashFromFile != null && hashFromStore != null && !hashFromFile.equals(hashFromStore);
	}

	private boolean allHashesMatch(String hash1, String hash2,String hash3, String hash4) {
		return  hash1 != null &&
				hash1.equals(hash2) &&
				hash1.equals(hash3) &&
				hash1.equals(hash4);
	}
	
	private boolean fileAndStoreHashesExistAndMatch(String hashFromFile, String hashFromStore) {
		return hashFromFile != null && hashFromFile.equals(hashFromStore);
	}
	
	private List<Action> fileWithMissingStoreHash(
			String hashForFileWithMissingStoreHash,
			String hashFromOtherFile,
			String hashFromOtherStore,
			Path fileWithMissingStoreHash,
			Path otherFile,
			FileHashStore storeWithMissingHash,
			FileHashStore otherStore) {
		ArrayList<Action> actions = new ArrayList<Action>();
		
		if (hashForFileWithMissingStoreHash.equals(hashFromOtherFile)) {
			setHash(hashForFileWithMissingStoreHash, fileWithMissingStoreHash, storeWithMissingHash);
			setHash(hashFromOtherFile, otherFile, otherStore);
		}
		else if (hashFromOtherStore == null && hashFromOtherFile == null) {
			copyFile(fileWithMissingStoreHash, otherFile, hashForFileWithMissingStoreHash, actions);
			setHash(hashForFileWithMissingStoreHash, fileWithMissingStoreHash, storeWithMissingHash);
			setHash(hashForFileWithMissingStoreHash, otherFile, otherStore);
		}
		else if (hashFromOtherStore == null) {
			actions.add(new SyncWarningAction(fileWithMissingStoreHash, hashForFileWithMissingStoreHash, null, otherFile, hashFromOtherFile, hashFromOtherStore));
		}

		return actions;
	}

	private List<Action> missingFileWithStoreHash(
			String hashFromStoreForMissingFile,
			String hashFromOtherFile,
			String hashFromOtherStore,
			Path missingFile,
			Path otherFile,
			FileHashStore storeForMissingFile,
			FileHashStore otherStore) {
		ArrayList<Action> actions = new ArrayList<Action>();
		
		if (hashFromOtherFile == null) {
			storeForMissingFile.removeHash(missingFile.getFileName());
			if (hashFromOtherStore != null) {
				otherStore.removeHash(otherFile.getFileName());
			}
		}
		else if (hashFromStoreForMissingFile.equals(hashFromOtherFile) && hashFromOtherStore == null) {
			copyFile(otherFile, missingFile, hashFromOtherFile, actions);
			setHash(hashFromStoreForMissingFile, otherFile, otherStore);
		}
		else if (!hashFromStoreForMissingFile.equals(hashFromOtherFile) && hashFromOtherStore == null) {
			actions.add(new SyncWarningAction(missingFile, null, hashFromStoreForMissingFile, otherFile, hashFromOtherFile, hashFromOtherStore));
		}
		
		return actions;
	}

	private List<Action> mismatchedFileAndStoreHash(
			String mismatchHashFromFile,
			String mismatchedHashFromStore,
			String otherHashFromFile,
			String otherHashFromStore,
			Path mismatchedFile,
			Path otherFile) {
		ArrayList<Action> actions = new ArrayList<Action>();
		actions.add(new SyncWarningAction(mismatchedFile, mismatchHashFromFile, mismatchedHashFromStore, otherFile, otherHashFromFile, otherHashFromStore));
		return actions;
	}

	private List<Action> oneFileAndStoreHashMatch(
			String matchingHashFromFile,
			String matchingHashFromStore,
			String otherHashFromFile,
			String otherHashFromStore,
			Path matchingFile,
			Path otherFile,
			FileHashStore matchingStore,
			FileHashStore otherStore) {
		
		ArrayList<Action> actions = new ArrayList<Action>();
		
		if (otherHashFromFile == null && otherHashFromStore == null) {
			copyFileAndSetHash(matchingHashFromStore, matchingFile, otherFile, otherStore, actions);
		}
		else if (otherHashFromFile != null && otherHashFromFile.equals(otherHashFromStore)) {
			actions.add(new SyncWarningAction(matchingFile, matchingHashFromFile, matchingHashFromStore, otherFile, otherHashFromFile, otherHashFromStore));
		}
		else if (matchingHashFromFile.equals(otherHashFromFile)) {
			setHash(otherHashFromFile, otherFile, otherStore);
		}
		else if (matchingHashFromStore.equals(otherHashFromStore) && otherHashFromFile == null) {
			copyFile(matchingFile, otherFile, matchingHashFromStore, actions);
		}
		else if (matchingHashFromStore.equals(otherHashFromStore)) {
			overwriteFile(matchingFile, otherFile, matchingHashFromStore, actions);
		}
		else if (otherHashFromFile != null && otherHashFromStore != null) {
			overwriteFileAndSetHash(matchingHashFromStore, matchingFile, otherFile, otherStore, actions);
		}
		else if (otherHashFromFile != null && otherHashFromStore == null) {
			actions.add(new SyncWarningAction(matchingFile, matchingHashFromFile, matchingHashFromStore, otherFile, otherHashFromFile, otherHashFromStore));
		}
		else {
			copyFileAndSetHash(matchingHashFromStore, matchingFile, otherFile, otherStore, actions);
		}

		return actions;
	}

	private void copyFileAndSetHash(
			String goodHash,
			Path goodFile,
			Path badFile,
			FileHashStore badStore,
			ArrayList<Action> actions) {
		setHash(goodHash, badFile, badStore);
		copyFile(goodFile, badFile, goodHash, actions);
	}
	
	private void overwriteFileAndSetHash(
			String goodHash,
			Path goodFile,
			Path badFile,
			FileHashStore badStore,
			ArrayList<Action> actions) {
		setHash(goodHash, badFile, badStore);
		overwriteFile(goodFile, badFile, goodHash, actions);
	}

	private void setHash(String goodHash, Path badFile, FileHashStore badStore) {
		badStore.setHash(badFile.getFileName(), goodHash);
	}

	private void copyFile(Path goodFile, Path badFile, String goodHash, ArrayList<Action> actions) {
		actions.add(new FileCopyAction(goodFile, badFile, goodHash, hashGenerator));
	}
	
	private void overwriteFile(Path goodFile, Path badFile, String goodHash, ArrayList<Action> actions) {
		actions.add(new FileOverwriteAction(goodFile, badFile, goodHash, hashGenerator));
	}
}
