package com.dancorder.PhotoSync;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class HashFileSource {
	
	private Path directory;
	
	HashFileSource(Path directory) {
		this.directory = directory;
	}
	
	public List<String> getData() throws IOException {
		return Files.readAllLines(getFilePath(), Charset.defaultCharset());
	}

	public void writeData(List<String> data) throws IOException {
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
				Charset.defaultCharset(),
				StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.WRITE);
	}

	private Path getFilePath() {
		return directory.resolve("hashes.txt");
	}
}
