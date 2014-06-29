package com.dancorder.Archiverify;

import java.nio.file.Path;

class FileOverwriteAction extends FileCopyAction {
	
	FileOverwriteAction(Path from, Path to, String goodHash, FileHashGenerator hashGenerator) {
		super(from, to, goodHash, hashGenerator);
	}
	
	@Override
	public String toString() {
		return String.format("Overwrite %s with %s with hash %s", to, from, goodHash);
	}
}
