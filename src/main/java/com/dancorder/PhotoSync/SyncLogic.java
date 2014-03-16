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
	
	List<Action> compareFiles(Path file1, FileHashStore store1, Path file2, FileHashStore store2) throws IOException {
		String hashFromFile1 = hashGenerator.calculateMd5(file1);
		String hashFromFile2 = hashGenerator.calculateMd5(file2);
		String hashFromStore1 = store1.getHash(file1.getFileName());
		String hashFromStore2 = store2.getHash(file2.getFileName());

		if (allHashesMatch(hashFromFile1, hashFromStore1, hashFromFile2, hashFromStore2)) {
			return new ArrayList<Action>();
		}
		else if (hashFromFile1 == hashFromStore1 && hashFromFile1 != null) {
			return oneFileAndStoreHashMatch(hashFromFile1, hashFromStore1, hashFromFile2, hashFromStore2, file1, file2, store1, store2);
		}
		else if (hashFromFile2 == hashFromStore2 && hashFromFile2 != null) {
			return oneFileAndStoreHashMatch(hashFromFile2, hashFromStore2, hashFromFile1, hashFromStore1, file2, file1, store2, store1);
		}
		else if (hashFromFile1 != hashFromStore1 && hashFromFile1 != null && hashFromStore1 != null) {
			return mismatchedFileAndStoreHash(hashFromFile1, hashFromStore1, hashFromFile2, hashFromStore2, file1, file2);
		}
		else if (hashFromFile2 != hashFromStore2 && hashFromFile2 != null && hashFromStore2 != null) {
			return mismatchedFileAndStoreHash(hashFromFile2, hashFromStore2, hashFromFile1, hashFromStore1, file2, file1);
		}
		else if (hashFromFile1 == null && hashFromStore1 != null) {
			return missingFileWithHash(hashFromStore1, hashFromFile2, hashFromStore2, file1, file2, store1, store2);
		}
		else if (hashFromFile2 == null && hashFromStore2 != null) {
			return missingFileWithHash(hashFromStore2, hashFromFile1, hashFromStore1, file2, file1, store2, store1);
		}
		else if (hashFromFile1 != null && hashFromStore1 == null) {
			return missingHashWithFile(hashFromFile1, hashFromFile2, hashFromStore2, file1, file2, store1, store2);
		}
		else if (hashFromFile2 != null && hashFromStore2 == null) {
			return missingHashWithFile(hashFromFile2, hashFromFile1, hashFromStore1, file2, file1, store2, store1);
		}

		return null;
	}
	
	private List<Action> missingHashWithFile(
			String hashForFileWithMissingStoreHash,
			String hashFromOtherFile,
			String hashFromOtherStore,
			Path fileWithMissingStoreHash,
			Path otherFile,
			FileHashStore storeWithMissingHash,
			FileHashStore otherStore) {
		ArrayList<Action> actions = new ArrayList<Action>();
		
		if (hashForFileWithMissingStoreHash == hashFromOtherFile) {
			storeWithMissingHash.setHash(fileWithMissingStoreHash.getFileName(), hashForFileWithMissingStoreHash);
			otherStore.setHash(otherFile.getFileName(), hashFromOtherFile);
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
			actions.add(new FileCopyAction(otherFile, missingFile));
			otherStore.setHash(otherFile.getFileName(), hashFromStoreForMissingFile);
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
			overwriteFileAndHash(matchingHashFromStore, matchingFile, otherFile, otherStore, actions);
		}
		else if (otherHashFromFile == otherHashFromStore) {
			actions.add(new SyncWarningAction(matchingFile, matchingHashFromFile, matchingHashFromStore, otherFile, otherHashFromFile, otherHashFromStore));
		}
		else if (matchingHashFromFile == otherHashFromFile) {
			overwriteHash(otherHashFromFile, otherFile, otherStore);
		}
		else if (matchingHashFromStore == otherHashFromStore) {
			overwriteFile(matchingFile, otherFile, actions);
		}
		else {
			overwriteFileAndHash(matchingHashFromStore, matchingFile, otherFile, otherStore, actions);
		}

		return actions;
	}

	private void overwriteFileAndHash(
			String goodHash,
			Path goodFile,
			Path badFile,
			FileHashStore badStore,
			ArrayList<Action> actions) {
		overwriteHash(goodHash, badFile, badStore);
		overwriteFile(goodFile, badFile, actions);
	}

	private void overwriteHash(String goodHash, Path badFile, FileHashStore badStore) {
		badStore.setHash(badFile.getFileName(), goodHash);
	}

	private void overwriteFile(Path goodFile, Path badFile,	ArrayList<Action> actions) {
		actions.add(new FileCopyAction(goodFile, badFile));
	}

	private boolean allHashesMatch(String hash1, String hash2,String hash3, String hash4) {
		return hash1 == hash2 && hash2 == hash3 && hash3 == hash4;
	}

	List<Action> compareDirectories(Path absolutePath1, Path absolutePath2, FileExistence existence) {
		return new ArrayList<Action>();
	}
}
