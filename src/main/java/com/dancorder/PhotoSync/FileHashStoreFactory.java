package com.dancorder.PhotoSync;

import java.nio.file.Path;

public class FileHashStoreFactory {
	FileHashStore createFileHashStore(Path dir1) throws Exception {
		return new FileHashStore(new HashFileSource(dir1));
	}
}
