package com.dancorder.Archiverify;

import java.nio.file.Paths

import com.dancorder.Archiverify.FileHashStoreFactory;

class FileHashStoreFactoryTest extends spock.lang.Specification {

	private final static dir = Paths.get(System.getProperty("java.io.tmpdir")).resolve("temp1")

	def "Create a FileHashStore"() {
		setup:
		def factory = new FileHashStoreFactory()
		
		when:
		def store = factory.createFileHashStore(dir)
		
		then:
		store.getDirectory() == dir
	}
}