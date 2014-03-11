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
	
	List<Action> compareFiles(Path file1, FileHashStore store1, Path file2, FileHashStore store2, FileExistence existence) throws IOException {
		String hashFromFile1 = hashGenerator.calculateMd5(file1);
		String hashFromFile2 = hashGenerator.calculateMd5(file2);
		String hashFromStore1 = store1.getHash(file1.getFileName());
		String hashFromStore2 = store2.getHash(file2.getFileName());

		if (allHashesMatch(hashFromFile1, hashFromStore1, hashFromFile2, hashFromStore2)) {
			return new ArrayList<Action>();
		}
		
		if (hashFromFile1 == hashFromStore1) {
			return oneFileAndStoreHashMatch(hashFromFile1, hashFromStore1, hashFromFile2, hashFromStore2, file1, file2, store1, store2);
		}
		
		if (hashFromFile2 == hashFromStore2) {
			return oneFileAndStoreHashMatch(hashFromFile2, hashFromStore2, hashFromFile1, hashFromStore1, file2, file1, store2, store1);
		}

		return null;
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
		
		if (otherHashFromFile == otherHashFromStore) {
			actions.add(new SyncWarningAction("File " + matchingFile + " and file " + otherFile + " are different but both have matching hashes. Please manually move or delete the incorrect file."));
		}
		else if (matchingHashFromFile == otherHashFromFile) {
			otherStore.setHash(otherFile.getFileName(), otherHashFromFile);
		}
		else if (matchingHashFromStore == otherHashFromStore) {
			actions.add(new FileCopyAction(matchingFile, otherFile));
		}
		else {
			otherStore.setHash(otherFile.getFileName(), matchingHashFromStore);
			actions.add(new FileCopyAction(matchingFile, otherFile));
		}

		return actions;
	}

	private boolean allHashesMatch(String hash1, String hash2,String hash3, String hash4) {
		return hash1 == hash2 && hash2 == hash3 && hash3 == hash4;
	}

	List<Action> compareDirectories(Path absolutePath1, Path absolutePath2, FileExistence existence) {
		return new ArrayList<Action>();
	}
}
