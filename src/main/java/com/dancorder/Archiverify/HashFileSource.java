//    Archiverify is an archive synching and verification tool
//    Copyright (C) 2014  Daniel Corder (contact: archiverify@dancorder.com)
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.dancorder.Archiverify;

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
		if (data.size() > 0) {
			writeDataToFile(data);
		}
		else {
			deleteHashFile();
		}
	}
	
	private void deleteHashFile() throws IOException {
		Files.deleteIfExists(getFilePath());
	}

	private void writeDataToFile(List<String> data) throws IOException {
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
