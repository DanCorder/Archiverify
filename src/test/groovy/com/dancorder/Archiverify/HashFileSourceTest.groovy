package com.dancorder.Archiverify;

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

import com.dancorder.Archiverify.HashFileSource;

class HashFileSourceTest extends spock.lang.Specification {
	
	private final static line1 = "Some test text"
	private final static line2 = "Some more text"
	
	private final static tempDir = Paths.get(System.getProperty("java.io.tmpdir"))
	private final static tempHashFile = tempDir.resolve(HashFileSource.HASH_FILE_NAME)
	
	def cleanup() {
		Files.deleteIfExists(tempHashFile)
	}
	
	def "Retrieve directory"() {
		setup:
		def source = new HashFileSource(tempHashFile.getParent())
		
		expect:
		source.getDirectory() == tempHashFile.getParent()
		
	}
	
	def "Read file from disk"() {
		setup:
		writeTempFile(line1)
		
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
	
	def "Read non-existant file"() {
		setup:
		
		when: "it is created with a directory"
		def source = new HashFileSource(tempHashFile.getParent())
		def data = source.getData();
		
		then: "data is empty"
		data.size() == 0
	}
	
	def "Check various line endings"() {
		setup:
		writeTempFile(line1 + lineEnding + line2 + lineEnding)

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

	def "Replace existing file"() {
		setup:
		writeTempFile(line1 + '\n' + line2)
		def source = new HashFileSource(tempHashFile.getParent())

		when: "data is passed in"
		source.writeData([line2])

		then: "that writer writes to hashes.txt"
		def fileContent = Files.readAllLines(tempHashFile, Charset.defaultCharset)
		fileContent.size() == 1
		fileContent[0] == line2
	}
	
	def "Delete file when no hashes present"() {
		setup:
		writeTempFile(line1)
		def source = new HashFileSource(tempHashFile.getParent())
		
		when: "no data is written"
		source.writeData([])
		
		then: "the file is removed"
		!Files.exists(tempHashFile)
	}

	private void writeTempFile(String data) throws IOException {
		def writer = Files.newBufferedWriter(tempHashFile, Charset.defaultCharset())
		writer.write(data)
		writer.close()
	}
}