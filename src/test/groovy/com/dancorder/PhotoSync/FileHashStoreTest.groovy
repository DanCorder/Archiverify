package com.dancorder.PhotoSync;

import java.nio.file.Paths

class FileHashStoreTest extends spock.lang.Specification {

	private static final testFilename1 = "testFile1"
	private static final testFilename2 = "testFile2"
	
	private static final testHash1 = "testHash1"
	private static final testHash2 = "testHash2"
	
	private static final line1 = testHash1 + "\t" + testFilename1
	private static final line1Bad = "badHash1\t" + testFilename1
	private static final line2 = testHash2 + "\t" + testFilename2
	
	private static final lineEnding = System.getProperty('line.separator')
	
	def "no data"() {
		setup:
		def testFile = Paths.get(testFilename1)
		
		when: "it is created with no data"
		def store = new FileHashStore(getMockSource(data1), getMockSource(data2))
		
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
		def testFile = Paths.get(testFilename1)
		
		when: "it is created"
		def store = new FileHashStore(getMockSource(data1), getMockSource(data2))
		
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
		def testFile = Paths.get(testFilename1)
		
		when: "it is created"
		def store = new FileHashStore(getMockSource([line1]), getMockSource([line1]))
		
		then: "a hash exists for the test file"
		store.hashExists(testFile)
		store.getHash(testFile) == testHash1
	}

	def "same file appears multiple times with different hashes"() {
		when: "it is created"
		def store = new FileHashStore(getMockSource(data1), getMockSource(data2))
		
		then: "an exception is thrown"
		thrown Exception
		
		where:
		data1             | data2
		[line1, line1Bad] | []
		[line1]           | [line1Bad]
		[line1Bad]        | [line1]
		[]                | [line1, line1Bad]
	}

	def "empty store creates empty list"() {
		setup:
		def mockSource1 = getMockSource([])
		def mockSource2 = getMockSource([])
		def store = new FileHashStore(mockSource1, mockSource2)

		when:
		def data = store.write()

		then:
		1 * mockSource1.writeData([])
		1 * mockSource2.writeData([])
		then:
		0 * _._
	}

	
	def "write single file hash"() {
		setup:
		def mockSource1 = getMockSource(data1)
		def mockSource2 = getMockSource(data2)
		def store = new FileHashStore(mockSource1, mockSource2)
		
		when:
		def data = store.write()
		
		then:
		1 * mockSource1.writeData([line1])
		1 * mockSource2.writeData([line1])
		then:
		0 * _._

		where:
		data1   | data2
		[line1] | []
		[]      | [line1]
		[line1] | [line1]
	}
	
	def "write two file hashes"() {
		setup:
		def mockSource1 = getMockSource(data1)
		def mockSource2 = getMockSource(data2)
		def store = new FileHashStore(mockSource1, mockSource2)
		
		when:
		def data = store.write()
		
		then:
		1 * mockSource1.writeData([line1, line2])
		1 * mockSource2.writeData([line1, line2])
		then:
		0 * _._
		
		where:
		data1          | data2
		[line1, line2] | []
		[]             | [line1, line2]
		[line1]        | [line2]
		[line2]        | [line1]
	}

	private def HashFileSource getMockSource(List<String> data) {
		def source = Mock(HashFileSource)
		source.getData() >> data
		return source
	}
}