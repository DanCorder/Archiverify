package com.dancorder.PhotoSync;

import java.nio.file.Path
import java.nio.file.Paths

import spock.lang.*

class FileHashStoreTest extends spock.lang.Specification {

	private static final testFilename1 = "testFile1"
	private static final testFilename2 = "testFile2"
	
	private static final testHash1 = "testHash1"
	private static final testHash2 = "testHash2"
	
	private static final line1 = testHash1 + "\t" + testFilename1
	private static final line1Bad = "badHash1\t" + testFilename1
	private static final line2 = testHash2 + "\t" + testFilename2
	
	private static final String lineEnding = System.getProperty('line.separator')
	
	def "no data"() {
		setup:
		StringReader fileData1 = new StringReader("")
		StringReader fileData2 = new StringReader("")
		Path testFile = Paths.get(testFilename1)
		
		when: "it is created with no data"
		def store = new FileHashStore(fileData1, fileData2)
		
		then: "no hash exists"
		!store.hashExists(testFile)
		store.getHash(testFile) == null
	}
	
	def "single file hash exists"() {
		setup:
		StringReader fileData1 = new StringReader(data1)
		StringReader fileData2 = new StringReader(data2)
		Path testFile = Paths.get(testFilename1)
		
		when: "it is created"
		def store = new FileHashStore(fileData1, fileData2)
		
		then: "a hash exists for the test file"
		store.hashExists(testFile)
		store.getHash(testFile) == testHash1
		
		where:
		data1 | data2
		line1 | ""
		""    | line1
	}
	
	def "same file appears in both paths"() {
		setup:
		StringReader fileData1 = new StringReader(line1)
		StringReader fileData2 = new StringReader(line1)
		Path testFile = Paths.get(testFilename1)
		
		when: "it is created"
		def store = new FileHashStore(fileData1, fileData2)
		
		then: "a hash exists for the test file"
		store.hashExists(testFile)
		store.getHash(testFile) == testHash1
	}

	def "same file appears multiple times with different hashes"() {
		setup:
		StringReader fileData1 = new StringReader(data1)
		StringReader fileData2 = new StringReader(data2)
		
		when: "it is created"
		def store = new FileHashStore(fileData1, fileData2)
		
		then: "a hash exists for the test file"
		thrown IllegalArgumentException
		
		where:
		data1                   | data2
		line1 + "\n" + line1Bad | ""
		line1                   | line1Bad
		line1Bad                | line1
		""                      | line1 + "\n" + line1Bad
	}
	
	def "Check different line endings between lines"() {
		setup:
		StringReader fileData1 = new StringReader(data1)
		StringReader fileData2 = new StringReader(data2)
		Path testFile1 = Paths.get(testFilename1)
		Path testFile2 = Paths.get(testFilename2)
		
		when: "it is created"
		def store = new FileHashStore(fileData1, fileData2)
		
		then: "a hash exists for each test file"
		store.hashExists(testFile1)
		store.getHash(testFile1) == testHash1
		store.hashExists(testFile2)
		store.getHash(testFile2) == testHash2
		
		where:
		data1                  | data2
		line1 + "\r" + line2   | ""
		line1 + "\n" + line2   | ""
		line1 + "\r\n" + line2 | ""
		""                     | line1 + "\r" + line2
		""                     | line1 + "\n" + line2
		""                     | line1 + "\r\n" + line2
	}
	
	def "Check different line endings at end of file"() {
		setup:
		StringReader fileData1 = new StringReader(data1)
		StringReader fileData2 = new StringReader(data2)
		Path testFile1 = Paths.get(testFilename1)
		
		when: "it is created"
		def store = new FileHashStore(fileData1, fileData2)
		
		then: "a hash exists for each test file"
		store.hashExists(testFile1)
		store.getHash(testFile1) == testHash1
		
		where:
		data1                  | data2
		line1          | ""
		line1 + "\r"   | ""
		line1 + "\n"   | ""
		line1 + "\r\n" | ""
		""             | line1
		""             | line1 + "\r"
		""             | line1 + "\n"
		""             | line1 + "\r\n"
	}
	
	def "empty store writes empty string"() {
		setup:
		def store = new FileHashStore(new StringReader(""), new StringReader(""))
		def writer = new StringWriter()
		
		when:
		store.write(writer)
		writer.close();
		
		then:
		writer.toString() == ""
	}
	
	def "write single file store"() {
		setup:
		def store = new FileHashStore(new StringReader(data1), new StringReader(data2))
		def writer = new StringWriter()
		
		when:
		store.write(writer)
		writer.close();
		
		then:
		writer.toString() == line1 + lineEnding
		
		where:
		data1 | data2
		line1 | ""
		""    | line1
	}
	
	def "write double file store"() {
		setup:
		def store = new FileHashStore(new StringReader(data1), new StringReader(data2))
		def writer = new StringWriter()
		
		when:
		store.write(writer)
		writer.close();
		
		then:
		writer.toString() == line1 + lineEnding + line2 + lineEnding
		
		where:
		data1                      | data2
		line1 + lineEnding + line2 | ""
		""                         | line1 + lineEnding + line2
	}	
}