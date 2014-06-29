package com.dancorder.Archiverify;

import java.nio.file.Path;

class FileHashStoreFactory {
	FileHashStore createFileHashStore(Path dir1) throws Exception {
		return new FileHashStore(new HashFileSource(dir1));
	}
}
