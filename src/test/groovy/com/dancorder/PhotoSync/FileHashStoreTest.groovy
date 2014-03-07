package com.dancorder.PhotoSync;

import java.nio.file.Path
import java.nio.file.Paths

class FileHashStoreTest extends spock.lang.Specification {

	private final static tempDir = Paths.get(System.getProperty("java.io.tmpdir"))
	private static final directory1 = tempDir.resolve("dir1")

	private static final testFilename1 = "testFile1"
	private static final testFilename2 = "testFile2"

	private static final testFile1RelativePath = Paths.get(testFilename1)
	private static final testFile2RelativePath = Paths.get(testFilename2)

	private static final testHash1 = "testHash1"
	private static final testHash2 = "testHash2"
	private static final badHash = "badHash1"

	private static final line1 = testHash1 + "\t" + testFilename1
	private static final line1Bad = badHash + "\t" + testFilename1
	private static final line2 = testHash2 + "\t" + testFilename2

	def "get directory"() {
		setup:
		def store = new FileHashStore(getMockSource([], directory1))
		
		expect:
		store.getDirectory() == directory1
	}
	
	def "create with no data"() {
		setup:
		def store = new FileHashStore(getMockSource(data))
		
		expect:
		!store.hashExists(testFile1RelativePath)
		store.getHash(testFile1RelativePath) == null
		
		where:
		data | _
		[]   | _
		[""] | _
	}

	def "create with single file hash"() {
		setup:
		def store = new FileHashStore(getMockSource([line1]))

		expect:
		store.hashExists(testFile1RelativePath)
		store.getHash(testFile1RelativePath) == testHash1
	}

	def "create with two file hashes"() {
		setup:
		def store = new FileHashStore(getMockSource([line1, line2]))
		
		expect:
		store.hashExists(testFile1RelativePath)
		store.getHash(testFile1RelativePath) == testHash1
		store.hashExists(testFile2RelativePath)
		store.getHash(testFile2RelativePath) == testHash2
	}

	def "don't write empty data"() {
		setup:
		def mockSource = getMockSource([])
		def store = new FileHashStore(mockSource)

		when:
		def data = store.write()

		then:
		0 * _._
	}

	def "write hashes from creation"() {
		setup:
		def mockSource = getMockSource(data)
		def store = new FileHashStore(mockSource)
		
		when:
		store.write()
		
		then: 1 * mockSource.writeData(data)
		then: 0 * _._
		
		where:
		data           | _
		[line1]        | _
		[line1, line2] | _
	}

	def "Add hash to empty store"() {
		setup:
		def store = new FileHashStore(getMockSource([]))
		
		when: "a new hash is added"
		store.setHash(path, hash)

		then: "a hash exists in the store"
		store.hashExists(path)
		store.getHash(path) == hash

		where:
		path                  | hash
		testFile1RelativePath | testHash1
		testFile2RelativePath | testHash2
	}

	def "write hash added to empty store"() {
		setup:
		def mockSource = getMockSource([])
		def store = new FileHashStore(mockSource)
		store.setHash(testFile1RelativePath, testHash1)

		when:
		def data = store.write()
		
		then: "the new hash is written out"
		1 * mockSource.writeData([line1])
		then:
		0 * _._
	}

	def "Add new hash to store"() {
		setup:
		def store = new FileHashStore(getMockSource([line1]))
		
		when: "a new hash is added"
		store.setHash(testFile2RelativePath, testHash2)
		
		then: "both hashes exist in the store"
		store.hashExists(testFile1RelativePath)
		store.getHash(testFile1RelativePath) == testHash1
		store.hashExists(testFile2RelativePath)
		store.getHash(testFile2RelativePath) == testHash2
	}

	def "write hash added to non-empty store"() {
		setup:
		def mockSource = getMockSource([line1])
		def store = new FileHashStore(mockSource)
		store.setHash(testFile2RelativePath, testHash2)
		
		when:
		def data = store.write()
		
		then: "the new hash is written out"
		1 * mockSource.writeData([line1, line2])
		then:
		0 * _._
	}
	
	def "Update hash in store"() {
		setup:
		def store = new FileHashStore(getMockSource([line1]))
		
		when: "a hash is updated"
		store.setHash(testFile1RelativePath, testHash2)
		
		then: "the new hash is stored"
		store.hashExists(testFile1RelativePath)
		store.getHash(testFile1RelativePath) == testHash2
	}

	def "same file appears multiple times with different hashes"() {
		when: "it is created"
		def store = new FileHashStore(getMockSource([line1, line1Bad]))
		
		then: "an exception is thrown"
		thrown Exception
	}

	private def HashFileSource getMockSource(List<String> data) {
		return getMockSource(data, directory1)
	}

	private def HashFileSource getMockSource(List<String> data, Path directory) {
		def source = Mock(HashFileSource)
		source.getData() >> data
		source.getDirectory() >> directory
		return source
	}
}