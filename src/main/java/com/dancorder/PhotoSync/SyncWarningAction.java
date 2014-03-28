package com.dancorder.PhotoSync;

import java.nio.file.Path;

class SyncWarningAction extends WarningAction {

	SyncWarningAction(Path file1, String calculatedHash1, String storedHash1, Path file2, String calculatedHash2, String storedHash2) {
		super("There was a problem synching " +
				file1 + " (calculated hash: " + calculatedHash1 + ", stored hash: " + storedHash1 + ") and " +
				file2 + " (calculated hash: " + calculatedHash2 + ", stored hash: " + storedHash2 + ")" +
				" please determine the correct file and hash and update the file(s) and/or hash(es).");
	}
}
