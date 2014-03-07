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
		String hashFromStore1 = store1.getHash(file1.getFileName());
		String hashFromStore2 = store2.getHash(file2.getFileName());
		String hashFromFile1 = hashGenerator.calculateMd5(file1);
		String hashFromFile2 = hashGenerator.calculateMd5(file2);
		
		if (hashFromStore1 != hashFromStore2) {
			if (hashFromStore1 == hashFromFile1 && hashFromStore1 == hashFromFile2) {
				store2.setHash(file2.getFileName(), hashFromStore1);
			}
			if (hashFromStore2 == hashFromFile1 && hashFromStore2 == hashFromFile2) {
				store1.setHash(file1.getFileName(), hashFromStore2);
			}
		}

		return new ArrayList<Action>();
	}
	
	List<Action> compareDirectories(Path absolutePath1, Path absolutePath2, FileExistence existence) {
		return new ArrayList<Action>();
	}
}
