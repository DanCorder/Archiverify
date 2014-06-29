package com.dancorder.Archiverify;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.codec.digest.DigestUtils;

class FileHashGenerator {
	String calculateMd5(Path file) throws IOException {
		if (!Files.exists(file)) {
			return null;
		}
		
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(file.toString());
			return DigestUtils.md5Hex(stream);
		}
		finally {
			if (stream != null) {
				stream.close();
			}
		}
	}
}
