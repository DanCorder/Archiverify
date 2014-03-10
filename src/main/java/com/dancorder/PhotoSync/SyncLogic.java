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
		ArrayList<Action> actions = new ArrayList<Action>();
		
		if (hashFromStore1 != hashFromStore2) {
			if (hashFromFile1 == hashFromFile2) {
				if (hashFromStore1 == hashFromFile1) {
					store2.setHash(file2.getFileName(), hashFromStore1);
					return actions;
				}
				if (hashFromStore2 == hashFromFile1) {
					store1.setHash(file1.getFileName(), hashFromStore2);
					return actions;
				}
			}
			else if (hashFromFile1 == hashFromStore1 && hashFromFile2 == hashFromStore2) {
				actions.add(new SyncWarningAction("File " + file1 + " and file " + file2 + " are different but both have matching hashes. Please manually move or delete the incorrect file."));
				return actions;
			}
			else if (hashFromFile1 == hashFromStore1 && hashFromFile2 != hashFromStore2) {
				actions.add(new FileCopyAction(file1, file2));
				store2.setHash(file2.getFileName(), hashFromStore1);
				return actions;
			}
			else if (hashFromFile1 != hashFromStore1 && hashFromFile2 == hashFromStore2) {
				actions.add(new FileCopyAction(file2, file1));
				store1.setHash(file1.getFileName(), hashFromStore2);
				return actions;
			}
		}
		else if (hashFromFile1 != hashFromFile2) {
			if (hashFromStore1 == hashFromStore2) {
				if (hashFromFile1 == hashFromStore1) {
					actions.add(new FileCopyAction(file1, file2));
					return actions;
				}
				if (hashFromFile2 == hashFromStore1) {
					actions.add(new FileCopyAction(file2, file1));
					return actions;
				}
			}
		}

		return actions;
	}
	
	List<Action> compareDirectories(Path absolutePath1, Path absolutePath2, FileExistence existence) {
		return new ArrayList<Action>();
	}
}
