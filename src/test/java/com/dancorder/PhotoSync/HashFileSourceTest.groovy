package com.dancorder.PhotoSync;

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import spock.lang.*

class HashFileSourceTest extends spock.lang.Specification {
	
	private final static String line1 = "Some test text"
	private final static String line2 = "Some more text"
	
	private final Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
	private final Path tempHashFile = tempDir.resolve("hashes.txt");
	
	def cleanup() {
		Files.deleteIfExists(tempHashFile)
	}
	
	def "Read file from disk"() {
		setup:
		def writer = Files.newBufferedWriter(tempHashFile, Charset.defaultCharset())
		writer.write(line1)
		writer.close()
		
		when: "it is created with a directory" 
		def source = new HashFileSource(tempHashFile.getParent())
		def data = source.getData();
		
		then: "it reads the data from hashes.txt"
		data.size() == 1
		data[0] == line1
	}
	
	def "Read empty file"() {
		setup:
		def writer = Files.createFile(tempHashFile)
		
		when: "it is created with a directory"
		def source = new HashFileSource(tempHashFile.getParent())
		def data = source.getData();
		
		then: "it reads the data from hashes.txt"
		data.size() == 0
	}
	
	def "Check various line endings"() {
		setup:
		def writer = Files.newBufferedWriter(tempHashFile, Charset.defaultCharset())
		writer.write(line1 + lineEnding + line2 + lineEnding)
		writer.close()
		
		when: "it reads a file with certain line endings" 
		def source = new HashFileSource(tempHashFile.getParent())
		def data = source.getData();
		
		then: "it reads multiple lines from hashes.txt"
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
		def source = new HashFileSource(tempHashFile.getParent())
		
		when: "data is passed in"
		source.writeData([line1])
		
		then: "that writer writes to hashes.txt"
		def fileContent = Files.readAllLines(tempHashFile, Charset.defaultCharset)
		fileContent.size() == 1
		fileContent[0] == line1
	}	
}
