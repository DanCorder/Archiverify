package com.dancorder.PhotoSync;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.dancorder.PhotoSync.ParallelFileTreeWalker.FileExistence;

class SyncLogic {
	SyncLogic(FileHashGenerator hashGenerator) {
	}
	
	List<Action> compareFiles(Path file1, FileHashStore store1, Path file2, FileHashStore store2, FileExistence existence) {
		return new ArrayList<Action>();
	}
	
	List<Action> compareDirectories(Path absolutePath1, Path absolutePath2, FileExistence existence) {
		return new ArrayList<Action>();
	}
}
