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
	
	private final Path directory;
	private final Path readFile;
	private final Path writeFile;
	
	HashFileSource(Path readFileName, Path writeFileName, Path directory) {
		this.directory = directory;
		this.readFile = directory.resolve(readFileName);
		this.writeFile = directory.resolve(writeFileName);
	}
	
	Path getDirectory() {
		return directory;
	}
	
	List<String> getData() throws IOException {
		if (Files.exists(readFile)) {
			return Files.readAllLines(readFile, getCharset());
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
			deleteHashFile(writeFile);
		}
		
		if (!writeFile.equals(readFile)) {
			deleteHashFile(readFile);
		}
	}
	
	private void deleteHashFile(Path file) throws IOException {
		Files.deleteIfExists(file);
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
				writeFile,
				getCharset(),
				StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.WRITE);
	}
	
	private Charset getCharset() {
		return Charset.defaultCharset();
	}
}
