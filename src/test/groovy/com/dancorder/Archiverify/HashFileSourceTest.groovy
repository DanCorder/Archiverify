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

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


class HashFileSourceTest extends spock.lang.Specification {
	
	private final static line1 = "Some test text"
	private final static line2 = "Some more text"
	
	private final static tempDir = Paths.get(System.getProperty("java.io.tmpdir"))
	
	private final static defaultHashFile = Paths.get("hashFileSoureTest_DefaultFileName")
	private final static alternateHashFile = Paths.get("hashFileSoureTest_AlternateFileName")
	
	def cleanup() {
		clearTempFile(defaultHashFile)
		clearTempFile(alternateHashFile)
	}
	
	def "Retrieve directory"() {
		setup:
		def source = new HashFileSource(defaultHashFile, defaultHashFile, tempDir)
		
		expect:
		source.getDirectory() == tempDir
	}
	
	def "Read file from disk"() {
		setup:
		writeTempFile(line1, defaultHashFile)
		
		when: "it is created with a directory" 
		def source = new HashFileSource(defaultHashFile, defaultHashFile, tempDir)
		def data = source.getData();
		
		then: "it reads the data from the hash file"
		data.size() == 1
		data[0] == line1
	}
	
	def "Read empty file"() {
		setup:
		writeTempFile("", defaultHashFile)
		
		when: "it is created with a directory"
		def source = new HashFileSource(defaultHashFile, defaultHashFile, tempDir)
		def data = source.getData();
		
		then: "it reads the data from the hash file"
		data.size() == 0
	}
	
	def "Read non-existant file"() {
		setup:
		
		when: "it is created with a directory"
		def source = new HashFileSource(defaultHashFile, defaultHashFile, tempDir)
		def data = source.getData();
		
		then: "data is empty"
		data.size() == 0
	}
	
	def "Check various line endings"() {
		setup:
		writeTempFile(line1 + lineEnding + line2 + lineEnding, defaultHashFile)

		when: "it reads a file with certain line endings" 
		def source = new HashFileSource(defaultHashFile, defaultHashFile, tempDir)
		def data = source.getData();
		
		then: "it reads multiple lines from the hash file"
		data.size() == 2
		data[0] == line1
		data[1] == line2
		
		where:
		lineEnding | _
		"\r\n"     | _
		"\r"       | _
		"\n"       | _
	}
	
	def "Write to disk"() {
		setup:
		def source = new HashFileSource(defaultHashFile, defaultHashFile, tempDir)
		
		when: "data is passed in"
		source.writeData([line1])
		
		then: "that writer writes to the hash file"
		tempFileExists(defaultHashFile)
		def fileContent = readTempFile(defaultHashFile)
		fileContent.size() == 1
		fileContent[0] == line1
	}

	def "Replace existing file"() {
		setup:
		writeTempFile(line1 + '\n' + line2, defaultHashFile)
		def source = new HashFileSource(defaultHashFile, defaultHashFile, tempDir)

		when: "data is passed in"
		source.writeData([line2])

		then: "that writer writes to the hash file"
		tempFileExists(defaultHashFile)
		def fileContent = readTempFile(defaultHashFile)
		fileContent.size() == 1
		fileContent[0] == line2
	}
	
	def "Delete read and write files when no hashes present"() {
		setup:
		writeTempFile(line1, defaultHashFile)
		writeTempFile(line1, alternateHashFile)
		def source = new HashFileSource(defaultHashFile, alternateHashFile, tempDir)
		
		when: "no data is written"
		source.writeData([])
		
		then: "the file is removed"
		!tempFileExists(defaultHashFile)
		!tempFileExists(alternateHashFile)
	}
	
	def "Delete read file when write filename is different"() {
		setup:
		writeTempFile(line1, defaultHashFile)
		clearTempFile(alternateHashFile)
		def source = new HashFileSource(defaultHashFile, alternateHashFile, tempDir)
		
		when: "data is written"
		source.writeData([line2])
		
		then: "the read file is removed and the write file is written"
		!tempFileExists(defaultHashFile)
		tempFileExists(alternateHashFile)
	}
	
	def "No error when read and write filenames are different and read file doesn't exist"() {
		setup:
		clearTempFile(defaultHashFile)
		clearTempFile(alternateHashFile)
		def source = new HashFileSource(defaultHashFile, alternateHashFile, tempDir)
		
		when: "data is written"
		source.writeData([line2])
		
		then: "the read file is removed and the write file is written"
		!tempFileExists(defaultHashFile)
		tempFileExists(alternateHashFile)
	}
	
	private void writeTempFile(String data, Path fileName) throws IOException {
		def writer = Files.newBufferedWriter(tempDir.resolve(fileName), Charset.defaultCharset())
		writer.write(data)
		writer.close()
	}
	
	private List<String> readTempFile(Path fileName) {
		return Files.readAllLines(tempDir.resolve(fileName), Charset.defaultCharset)
	}
	
	private void clearTempFile(Path fileName) {
		Files.deleteIfExists(tempDir.resolve(fileName))
	}

	private boolean tempFileExists(Path fileName) {
		return Files.exists(tempDir.resolve(fileName))
	}
}
