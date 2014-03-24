package com.dancorder.PhotoSync;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.dancorder.PhotoSync.ParallelFileTreeWalker.FileExistence;

class SyncLogic {
	
	private final FileHashGenerator hashGenerator;
	
	SyncLogic(FileHashGenerator hashGenerator) {
		this.hashGenerator = hashGenerator;
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
		else if (fileHashExistsButStoreHashDoesnt(hashFromFile1, hashFromStore1)) {
			return missingFileWithHash(hashFromStore1, hashFromFile2, hashFromStore2, absoultePath1, absolutePath2, store1, store2);
		}
		else if (fileHashExistsButStoreHashDoesnt(hashFromFile2, hashFromStore2)) {
			return missingFileWithHash(hashFromStore2, hashFromFile1, hashFromStore1, absolutePath2, absoultePath1, store2, store1);
		}
		else if (storeHashExistsButFileDoesnt(hashFromFile1, hashFromStore1)) {
			return fileWithMissingHash(hashFromFile1, hashFromFile2, hashFromStore2, absoultePath1, absolutePath2, store1, store2);
		}
		else if (storeHashExistsButFileDoesnt(hashFromFile2, hashFromStore2)) {
			return fileWithMissingHash(hashFromFile2, hashFromFile1, hashFromStore1, absolutePath2, absoultePath1, store2, store1);
		}

		throw new RuntimeException(
				String.format("Unforeseen combination of file and store hashes. File1: %s, Store1: %s, File2: %s, Store2: %s",
						hashFromFile1,
						hashFromStore1,
						hashFromFile2,
						hashFromStore2));
	}

	private boolean storeHashExistsButFileDoesnt(String hashFromFile,
			String hashFromStore) {
		return hashFromFile != null && hashFromStore == null;
	}

	private boolean fileHashExistsButStoreHashDoesnt(String hashFromFile,
			String hashFromStore) {
		return hashFromFile == null && hashFromStore != null;
	}

	private boolean fileAndStoreHashesExistAndDontMatch(String hashFromFile,
			String hashFromStore) {
		return hashFromFile != hashFromStore && hashFromFile != null && hashFromStore != null;
	}

	private boolean allHashesMatch(String hash1, String hash2,String hash3, String hash4) {
		return hash1 == hash2 && hash2 == hash3 && hash3 == hash4;
	}
	
	private boolean fileAndStoreHashesExistAndMatch(String hashFromFile, String hashFromStore) {
		return hashFromFile == hashFromStore && hashFromFile != null;
	}
	
	private List<Action> fileWithMissingHash(
			String hashForFileWithMissingStoreHash,
			String hashFromOtherFile,
			String hashFromOtherStore,
			Path fileWithMissingStoreHash,
			Path otherFile,
			FileHashStore storeWithMissingHash,
			FileHashStore otherStore) {
		ArrayList<Action> actions = new ArrayList<Action>();
		
		if (hashForFileWithMissingStoreHash == hashFromOtherFile) {
			setHash(hashForFileWithMissingStoreHash, fileWithMissingStoreHash, storeWithMissingHash);
			setHash(hashFromOtherFile, otherFile, otherStore);
		}
		else if (hashFromOtherStore == null && hashFromOtherFile == null) {
			copyFile(fileWithMissingStoreHash, otherFile, actions);
			setHash(hashForFileWithMissingStoreHash, fileWithMissingStoreHash, storeWithMissingHash);
			setHash(hashForFileWithMissingStoreHash, otherFile, otherStore);
		}
		else if (hashFromOtherStore == null) {
			actions.add(new SyncWarningAction(fileWithMissingStoreHash, hashForFileWithMissingStoreHash, null, otherFile, hashFromOtherFile, hashFromOtherStore));
		}

		return actions;
	}

	private List<Action> missingFileWithHash(
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
		else if (hashFromStoreForMissingFile == hashFromOtherFile && hashFromOtherStore == null) {
			copyFile(otherFile, missingFile, actions);
			setHash(hashFromStoreForMissingFile, otherFile, otherStore);
		}
		else if (hashFromStoreForMissingFile != hashFromOtherFile && hashFromOtherStore == null) {
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
		else if (otherHashFromFile == otherHashFromStore) {
			actions.add(new SyncWarningAction(matchingFile, matchingHashFromFile, matchingHashFromStore, otherFile, otherHashFromFile, otherHashFromStore));
		}
		else if (matchingHashFromFile == otherHashFromFile) {
			setHash(otherHashFromFile, otherFile, otherStore);
		}
		else if (matchingHashFromStore == otherHashFromStore) {
			copyFile(matchingFile, otherFile, actions);
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
		copyFile(goodFile, badFile, actions);
	}

	private void setHash(String goodHash, Path badFile, FileHashStore badStore) {
		badStore.setHash(badFile.getFileName(), goodHash);
	}

	private void copyFile(Path goodFile, Path badFile, ArrayList<Action> actions) {
		actions.add(new FileCopyAction(goodFile, badFile));
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
}
