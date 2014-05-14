package com.dancorder.PhotoSync;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

class HashFileSource {
	
	static boolean isHashFile(Path filePath) {
		return HASH_FILE_NAME.equals(filePath.getFileName().toString());
	}
	
	private static final String HASH_FILE_NAME = "hashes.txt";
	private final Path directory;
	
	HashFileSource(Path directory) {
		this.directory = directory;
	}
	
	Path getDirectory() {
		return directory;
	}
	
	List<String> getData() throws IOException {
		if (Files.exists(getFilePath())) {
			return Files.readAllLines(getFilePath(), getCharset());
		}
		else {
			return new ArrayList<String>();
		}
			
	}

	void writeData(List<String> data) throws IOException {
		try (Writer bufferedWriter = getBufferedWriter()) {
			try (PrintWriter writer = new PrintWriter(bufferedWriter)) {
				for (String line : data) {
					writer.println(line);
				}
			}
		}
	}
	
	private Writer getBufferedWriter() throws IOException {
		return Files.newBufferedWriter(
				getFilePath(),
				getCharset(),
				StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.WRITE);
	}
	
	private Charset getCharset() {
		return Charset.defaultCharset();
	}

	private Path getFilePath() {
		return directory.resolve(HASH_FILE_NAME);
	}
}
