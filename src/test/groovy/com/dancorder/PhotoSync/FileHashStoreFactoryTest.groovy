package com.dancorder.PhotoSync;

import java.nio.file.Paths

class FileHashStoreFactoryTest extends spock.lang.Specification {

	private final static tempDir = Paths.get(System.getProperty("java.io.tmpdir"))
	private final static dir1 = tempDir.resolve("temp1")
	private final static dir2 = tempDir.resolve("temp2")

	def "Create a FileHashStore"() {
		setup:
		def factory = new FileHashStoreFactory()
		
		when:
		def store = factory.createFileHashStore(dir1, dir2)
		
		then:
		def directories = store.getDirectories()
		directories.size() == 2
		directories.contains(dir1)
		directories.contains(dir2)
	}
}