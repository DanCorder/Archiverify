package com.dancorder.PhotoSync;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

class UpdateHashesAction implements Action {
	private final FileHashStore store;

	UpdateHashesAction(FileHashStore hashes) {
		if (hashes == null) {
			throw new IllegalArgumentException("hashes cannot be null");
		}
		
		store = hashes;
	}
	
	@Override
	public void doAction() throws IOException {
		store.write();
	}
	
	@Override
	public String toString() {
		List<Path> directories = store.getDirectories();
		return "Write hashes to " + directories.get(0).toString() + " and " + directories.get(1).toString();
		
	}
}
