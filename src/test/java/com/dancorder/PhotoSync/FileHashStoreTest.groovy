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
		Path testFile = Paths.get(testFilename1)
		
		when: "it is created with no data"
		def store = new FileHashStore(data1, data2)
		
		then: "no hash exists"
		!store.hashExists(testFile)
		store.getHash(testFile) == null
		
		where:
		data1 | data2
		[]    | []
		[""]  | [""]
	}
	
	def "single file hash exists"() {
		setup:
		Path testFile = Paths.get(testFilename1)
		
		when: "it is created"
		def store = new FileHashStore(data1, data2)
		
		then: "a hash exists for the test file"
		store.hashExists(testFile)
		store.getHash(testFile) == testHash1
		
		where:
		data1   | data2
		[line1] | []
		[]      | [line1]
	}
	
	def "same file appears in both paths"() {
		setup:
		Path testFile = Paths.get(testFilename1)
		
		when: "it is created"
		def store = new FileHashStore([line1], [line1])
		
		then: "a hash exists for the test file"
		store.hashExists(testFile)
		store.getHash(testFile) == testHash1
	}

	def "same file appears multiple times with different hashes"() {
		when: "it is created"
		def store = new FileHashStore(data1, data2)
		
		then: "a hash exists for the test file"
		thrown IllegalArgumentException
		
		where:
		data1             | data2
		[line1, line1Bad] | []
		[line1]           | [line1Bad]
		[line1Bad]        | [line1]
		[]                | [line1, line1Bad]
	}

	def "empty store creates empty list"() {
		setup:
		def store = new FileHashStore([], [])
		
		when:
		def data = store.getData()
		
		then:
		data.size() == 0
	}
	
	def "data for single file store"() {
		setup:
		def store = new FileHashStore(data1, data2)
		
		when:
		def data = store.getData()
		
		then:
		data.size() == 1
		data[0] == line1

		where:
		data1   | data2
		[line1] | []
		[]      | [line1]
		[line1] | [line1]
	}
	
	def "data for double file store"() {
		setup:
		def store = new FileHashStore(data1, data2)
		
		when:
		def data = store.getData()
		
		then:
		data.size() == 2
		data[0] == line1
		data[1] == line2
		
		where:
		data1          | data2
		[line1, line2] | []
		[]             | [line1, line2]
		[line1]        | [line2]
		[line2]        | [line1]
	}	
}