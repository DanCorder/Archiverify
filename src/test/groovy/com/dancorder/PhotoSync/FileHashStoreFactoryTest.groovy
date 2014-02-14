package com.dancorder.PhotoSync;

import java.nio.file.Files
import java.nio.file.Paths

class FileHashStoreFactoryTest extends spock.lang.Specification {
	
	private final static line1 = "Some test text"
	private final static line2 = "Some more text"
	
	private final static tempDir = Paths.get(System.getProperty("java.io.tmpdir"))
	private final static tempHashFile = tempDir.resolve("hashes.txt")
	
	def cleanup() {
		Files.deleteIfExists(tempHashFile)
	}
	
	def "Create a FileHashStore"() {
		setup:
		def factory = new FileHashStoreFactory()
		
		when:
		def store = factory.createFileHashStore(tempDir, tempDir)
		
		then:
		def directories = store.getDirectories()
		directories.size() == 2
		directories[0] == tempDir
		directories[1] == tempDir
	}
}