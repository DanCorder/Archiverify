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

import java.nio.file.Path
import java.nio.file.Paths
import com.dancorder.Archiverify.testHelpers.*

class FileHashStoreTest extends spock.lang.Specification {

	private final static tempDir = FileSystem.getTempDirectory()
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
	
	def "remove hash"() {
		setup:
		def store = new FileHashStore(getMockSource([line1, line2]))
		
		when:
		store.removeHash(testFile1RelativePath)
		
		then:
		!store.hashExists(testFile1RelativePath)
		store.hashExists(testFile2RelativePath)
		store.getHash(testFile2RelativePath) == testHash2
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
	
	def "new store is clean"() {
		setup:
		def mockSource = getMockSource(data)
		def store = new FileHashStore(mockSource)
		
		expect:
		!store.isDirty()

		where:
		data           | _
		[]             | _
		[""]           | _
		[line1]        | _
		[line1, line2] | _
	}
	
	def "adding existing hash doesn't make the store dirty"() {
		setup:
		def mockSource = getMockSource([line1])
		def store = new FileHashStore(mockSource)
		
		when:
		store.setHash(testFile1RelativePath, testHash1)
		
		then:
		!store.isDirty()
	}
	
	def "adding new hash makes the store dirty"() {
		setup:
		def mockSource = getMockSource([])
		def store = new FileHashStore(mockSource)
		
		when:
		store.setHash(testFile1RelativePath, testHash1)
		
		then:
		store.isDirty()
	}
	
	def "changing an existing hash makes the store dirty"() {
		setup:
		def mockSource = getMockSource([line1])
		def store = new FileHashStore(mockSource)
		
		when:
		store.setHash(testFile1RelativePath, testHash2)
		
		then:
		store.isDirty()
	}
	
	def "removing an existing hash makes the store dirty"() {
		setup:
		def mockSource = getMockSource([line1])
		def store = new FileHashStore(mockSource)
		
		when:
		store.removeHash(testFile1RelativePath)
		
		then:
		store.isDirty()
	}
	
	def "removing a non-existant hash doesn't make the store dirty"() {
		setup:
		def mockSource = getMockSource([line1])
		def store = new FileHashStore(mockSource)
		
		when:
		store.removeHash(testFile2RelativePath)
		
		then:
		!store.isDirty()
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